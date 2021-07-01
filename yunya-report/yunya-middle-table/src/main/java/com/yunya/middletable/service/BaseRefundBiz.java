package com.yunya.middletable.service;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.*;
import com.yunya.middletable.dao.treatment.BillRefundOrderDetailMapper;
import com.yunya.middletable.dao.treatment.BillRefundPayDetailRecordMapper;
import com.yunya.middletable.dao.treatment.BillRefundRecordMapper;
import com.yunya.middletable.service.credits_shop.CreditsShopBiz;
import com.yunya.models.report.*;
import com.yunya.models.treatment.BillRefundOrderDetail;
import com.yunya.models.treatment.BillRefundPayDetailRecord;
import com.yunya.models.treatment.BillRefundRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 简介: 中间表退费业务处理
 *
 * @author: chow
 * @date: 2020/10/29 20:33
 * @description:
 * @since: 1.0.0
 */
@Service
@Slf4j
public class BaseRefundBiz extends BaseBiz<BaseRefundMapper, BaseRefund> {

  /** 退费记录 */
  @Autowired private BillRefundRecordMapper refundRecordMapper;

  /** 退费开单详情 */
  @Autowired private BillRefundOrderDetailMapper refundOrderDetailMapper;

  @Autowired private BillRefundPayDetailRecordMapper refundPayDetailRecordMapper;

  /** 退费明细 */
  @Autowired private BaseRefundDetailMapper refundDetailMapper;

  @Autowired private BaseRefundPayDetailMapper refundPayDetailMapper;
  @Autowired private BaseBillMapper baseBillMapper;
  @Autowired private BaseBillPayDetailMapper baseBillPayDetailMapper;
  @Autowired private BaseBillPayMapper baseBillPayMapper;
  @Autowired private CreditsShopBiz creditsShopBiz;
  @Autowired private BaseBillDetailMapper baseBillDetailMapper;
  @Autowired private BaseRefundDetailMapper baseRefundDetailMapper;

  /**
   * 根据消息更新中间表退费信息
   *
   * @param msg 消息
   */
  public void operateRefund(MessageModel msg) {
    Map<String, Object> paramMap = msg.getParamMap();
    Integer dataId = (Integer) paramMap.get("id");
    BaseRefund refund = generateBaseRefund(dataId);
    Integer operateType = msg.getOperateType();
    switch (operateType) {
      case 0:
        mapper.deleteByPrimaryKey(dataId);
        if (null != refund) {
          mapper.insertSelective(refund);
          saveBaseRefundDetail(dataId);
          // 回滚积分记录
          callbackCredits(dataId,refund.getBillId());
        }
        break;
      case 1:
        if (null != refund) {
          BaseRefund result = mapper.selectByPrimaryKey(dataId);
          if (null == result) {
            mapper.deleteByPrimaryKey(dataId);
            mapper.insertSelective(refund);
            saveBaseRefundDetail(dataId);
          } else {
            mapper.updateByPrimaryKeySelective(refund);
            updateBaseRefundDetail(dataId);
          }
          // 回滚积分记录
          callbackCredits(dataId,refund.getBillId());
        } else {
          mapper.deleteByPrimaryKey(dataId);
        }
        break;
      case 2:
        if (null == refund) {
          mapper.deleteByPrimaryKey(dataId);
          refundDetailMapper.deleteByRefundId(dataId);
          deleteByRefundId(dataId);
        } else {
          mapper.insertSelective(refund);
          saveBaseRefundDetail(dataId);
        }
        // 回滚积分记录
        callbackCredits(dataId,refund.getBillId());
        break;
      default:
        break;
    }
  }

  private void deleteByRefundId(Integer refundId) {
    BaseRefundPayDetail entity = new BaseRefundPayDetail();
    entity.setRefundId(refundId);
    refundPayDetailMapper.delete(entity);
  }

  /**
   * 构建中间表退费信息
   *
   * @param refundId 退费记录ID
   * @return BaseRefund
   */
  private BaseRefund generateBaseRefund(Integer refundId) {
    BillRefundRecord billRefundRecord = refundRecordMapper.selectByPrimaryKey(refundId);
    return null != billRefundRecord ? setBaseRefundValue(billRefundRecord) : null;
  }

  /**
   * 设置退费账单
   *
   * @param billRefundRecord 账单退费记录
   * @return BaseRefund
   */
  private BaseRefund setBaseRefundValue(BillRefundRecord billRefundRecord) {
    BaseRefund refund = new BaseRefund();
    refund.setRefundId(billRefundRecord.getId());
    refund.setOrgId(billRefundRecord.getOrgId());
    refund.setPatientId(billRefundRecord.getPatientId());
    refund.setTreatmentId(billRefundRecord.getTreatmentRecordId());
    refund.setBillId(billRefundRecord.getOrderRecordId());
    refund.setRefundAmount(billRefundRecord.getTotalRefundAmount());
    refund.setRefundOperatorId(billRefundRecord.getCrtId());
    refund.setRefundDate(billRefundRecord.getCrtTime());
    refund.setRefundReason(billRefundRecord.getReason());
    return refund;
  }

  /**
   * 根据退费记录ID保存退费明细
   *
   * @param refundId 退费记录ID
   */
  private void saveBaseRefundDetail(Integer refundId) {
    BillRefundOrderDetail refundOrderDetail = new BillRefundOrderDetail();
    refundOrderDetail.setBillRefundRecordId(refundId);
    List<BillRefundOrderDetail> refundOrderDetails =
        refundOrderDetailMapper.select(refundOrderDetail);
    if (StringHelper.isNotEmpty(refundOrderDetails)) {
      BaseRefundDetail refundDetail = new BaseRefundDetail();
      refundOrderDetails.forEach(
          detail -> {
            Integer refundDetailId = detail.getId();
            refundDetail.setRefundDetailId(refundDetailId);
            refundDetail.setRefundId(refundId);
            refundDetail.setBillDetailId(detail.getOrderDetailId());
            refundDetail.setRefundAmount(detail.getRefundAmount());
            refundDetailMapper.deleteByPrimaryKey(refundDetailId);
            refundDetailMapper.insertSelective(refundDetail);
          });
    }

    saveBaseRefundPayDetail(refundId);
  }

  private void saveBaseRefundPayDetail(Integer refundId) {
    BillRefundPayDetailRecord billRefundPayDetailRecord = new BillRefundPayDetailRecord();
    billRefundPayDetailRecord.setBillRefundRecordId(refundId);
    List<BillRefundPayDetailRecord> refundPayDetails =
            refundPayDetailRecordMapper.select(billRefundPayDetailRecord);
    if (StringHelper.isNotEmpty(refundPayDetails)) {
      deleteByRefundId(refundId);
      BaseRefundPayDetail refundPayDetail = new BaseRefundPayDetail();
      refundPayDetails.forEach(
              detail -> {
                Integer refundDetailId = detail.getId();
                refundPayDetail.setBillRefundPayDetailRecordId(refundDetailId);
                refundPayDetail.setRefundId(refundId);
                refundPayDetail.setAccountItemId(detail.getAccountItemId());
                refundPayDetail.setBonusAmount(detail.getGiftAmount());
                refundPayDetail.setPrincipalAmount(detail.getPrincipalAmount());
                refundPayDetailMapper.insertSelective(refundPayDetail);
              });
    }
  }

  /**
   * 更新中间表退费明细
   *
   * @param refundId 退费记录ID
   */
  private void updateBaseRefundDetail(Integer refundId) {
    BillRefundOrderDetail refundOrderDetail = new BillRefundOrderDetail();
    refundOrderDetail.setBillRefundRecordId(refundId);
    List<BillRefundOrderDetail> refundOrderDetails =
        refundOrderDetailMapper.select(refundOrderDetail);
    if (StringHelper.isNotEmpty(refundOrderDetails)) {
      for (BillRefundOrderDetail refundDetail : refundOrderDetails) {
        Integer refundOrderDetailId = refundDetail.getId();
        BaseRefundDetail baseRefundDetail =
            refundDetailMapper.selectByPrimaryKey(refundOrderDetailId);
        if (null != baseRefundDetail) {
          baseRefundDetail.setRefundDetailId(refundOrderDetailId);
          baseRefundDetail.setBillDetailId(refundDetail.getOrderDetailId());
          baseRefundDetail.setRefundId(refundId);
          baseRefundDetail.setRefundAmount(refundDetail.getRefundAmount());
          refundDetailMapper.updateByPrimaryKeySelective(baseRefundDetail);
        } else {
          baseRefundDetail = new BaseRefundDetail();
          baseRefundDetail.setRefundDetailId(refundOrderDetailId);
          baseRefundDetail.setBillDetailId(refundDetail.getOrderDetailId());
          baseRefundDetail.setRefundId(refundId);
          baseRefundDetail.setRefundAmount(refundDetail.getRefundAmount());
          refundDetailMapper.deleteByPrimaryKey(refundOrderDetailId);
          refundDetailMapper.insertSelective(baseRefundDetail);
        }
      }
    }
    saveBaseRefundPayDetail(refundId);
  }

  /**
   * 通过时间段更新中间表退费记录
   *
   * @param form 时间段
   */
  public void pullRefundData(PullForm form) {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    Example refundExample = new Example(BillRefundRecord.class);
    refundExample.createCriteria().andBetween("updTime", startDate, endDate);
    List<BillRefundRecord> refundRecords = refundRecordMapper.selectByExample(refundExample);
    if (StringHelper.isNotEmpty(refundRecords)) {
      refundRecords.stream()
          .map(BillRefundRecord::getId)
          .forEach(
              refundRecordId -> {
                mapper.deleteByPrimaryKey(refundRecordId);
                BaseRefund baseRefund = generateBaseRefund(refundRecordId);
                if (null != baseRefund) {
                  mapper.insertSelective(baseRefund);
                  saveBaseRefundDetail(refundRecordId);
                } else {
                  refundDetailMapper.deleteByRefundId(refundRecordId);
                }
              });
    }
  }

  /**
   * 回滚会员积分
   * @param refundId 退费记录ID
   * @param billId 账单ID
   */
  private void callbackCredits(Integer refundId,Integer billId) {
    if (refundId != null && billId != null) {
      BaseRefundDetail query = new BaseRefundDetail();
      query.setRefundId(refundId);
      List<BaseRefundDetail> bfds = baseRefundDetailMapper.select(query);
      BaseBillDetail bbdQuery = new BaseBillDetail();
      bbdQuery.setBillId(billId);
      List<BaseBillDetail> bbds = baseBillDetailMapper.select(bbdQuery);
      if (StringHelper.isNotEmpty(bfds) && StringHelper.isNotEmpty(bbds)) {
        // 过滤出非商品账单项目ID集合
        List<Integer> medicalBillDetailIds = bbds.stream().filter(item -> item.getItemType().intValue() == 0).mapToInt(BaseBillDetail::getBillDetailId).boxed().collect(Collectors.toList());
        if (StringHelper.isNotEmpty(medicalBillDetailIds)) {
          // 过滤出退费项目是非商品的所有项目
          List<BaseRefundDetail> medicalBaseRefunds = bfds.stream().filter(item -> medicalBillDetailIds.contains(item.getBillDetailId())).collect(Collectors.toList());
          if (StringHelper.isNotEmpty(medicalBaseRefunds)) {
            // 非商品类项目退款总额
            BigDecimal refundMedicalMony = new BigDecimal(0);
            for (BaseRefundDetail item: medicalBaseRefunds) {
              refundMedicalMony = refundMedicalMony.add(item.getRefundAmount());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("refundId",refundId);
            BaseRefund baseRefund = mapper.selectByPrimaryKey(refundId);
            // 增加会员积分  1元=1积分
            creditsShopBiz.ivyConsumeAddCredits(baseRefund.getPatientId(), refundMedicalMony, jsonObject.toJSONString(),(byte) 1);
          }
        }
      }
    }
  }

}
