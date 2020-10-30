package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseRefundDetailMapper;
import com.yunya.middletable.dao.report.BaseRefundMapper;
import com.yunya.middletable.dao.treatment.BillRefundOrderDetailMapper;
import com.yunya.middletable.dao.treatment.BillRefundRecordMapper;
import com.yunya.models.report.BaseRefund;
import com.yunya.models.report.BaseRefundDetail;
import com.yunya.models.treatment.BillRefundOrderDetail;
import com.yunya.models.treatment.BillRefundRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * 简介: 中间表退费业务处理
 *
 * @author: chow
 * @date: 2020/10/29 20:33
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseRefundBiz extends BaseBiz<BaseRefundMapper, BaseRefund> {

  /** 退费记录 */
  @Autowired private BillRefundRecordMapper refundRecordMapper;

  /** 退费开单详情 */
  @Autowired private BillRefundOrderDetailMapper refundOrderDetailMapper;

  /** 退费明细 */
  @Autowired private BaseRefundDetailMapper refundDetailMapper;

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
        } else {
          mapper.deleteByPrimaryKey(dataId);
        }
        break;
      case 2:
        if (null == refund) {
          mapper.deleteByPrimaryKey(dataId);
          refundDetailMapper.deleteByRefundId(dataId);
        } else {
          mapper.insertSelective(refund);
          saveBaseRefundDetail(dataId);
        }
        break;
      default:
        break;
    }
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
            refundDetail.setRefundAmount(detail.getRefundAmout());
            refundDetailMapper.deleteByPrimaryKey(refundDetailId);
            refundDetailMapper.insertSelective(refundDetail);
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
          baseRefundDetail.setRefundAmount(refundDetail.getRefundAmout());
          refundDetailMapper.updateByPrimaryKeySelective(baseRefundDetail);
        } else {
          baseRefundDetail = new BaseRefundDetail();
          baseRefundDetail.setRefundDetailId(refundOrderDetailId);
          baseRefundDetail.setBillDetailId(refundDetail.getOrderDetailId());
          baseRefundDetail.setRefundId(refundId);
          baseRefundDetail.setRefundAmount(refundDetail.getRefundAmout());
          refundDetailMapper.deleteByPrimaryKey(refundOrderDetailId);
          refundDetailMapper.insertSelective(baseRefundDetail);
        }
      }
    }
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
}
