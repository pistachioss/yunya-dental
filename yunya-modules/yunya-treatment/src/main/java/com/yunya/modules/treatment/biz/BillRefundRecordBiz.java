package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.query.BillRefundQuery;
import com.yunya.feign.treatment.domain.vo.BillRefundGroupInfoVO;
import com.yunya.feign.treatment.domain.vo.BillRefundOrderDetailVO;
import com.yunya.feign.treatment.domain.vo.BillRefundPaymentVO;
import com.yunya.feign.treatment.domain.vo.BillRefundRecordVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.system.AccountItem;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.treatment.*;
import com.yunya.modules.treatment.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简介: 账单退费记录业务层
 *
 * @author: chow
 * @date: 2020/9/26 14:39
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillRefundRecordBiz extends BaseBiz<BillRefundRecordMapper, BillRefundRecord> {

  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 异常处理详情 */
  @Autowired private BillExceptionHandleDetailRecordMapper billExceptionHandleDetailRecordMapper;
  /** 基础处置项目 */
  @Autowired private BaseTariffMapper tariffMapper;
  /** 基础商品 */
  @Autowired private BaseOralTariffMapper baseOralTariffMapper;
  /** 开单详情 */
  @Autowired private OrderDetailMapper orderDetailMapper;
  /** 开单详情付款 */
  @Autowired private OrderDetailPayRecordMapper orderDetailPayRecordMapper;
  /** 退费开单详情 */
  @Autowired private BillRefundOrderDetailMapper billRefundOrderDetailMapper;
  /** 退费打款记录 */
  @Autowired private BillRefundPayDetailRecordMapper billRefundPayDetailRecordMapper;

  /**
   * 根据条件查询患者退费记录列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<BillRefundRecordVO> findBillRefundList(BillRefundQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillRefundRecordVO> resultList = mapper.selectBillRefundList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            Integer orgId = vo.getOrgId();
            OrganizationInfo organizationInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
            if (null != organizationInfo) {
              vo.setOrgName(organizationInfo.getAbbreviation());
            }
          });
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据账单退费记录ID查询账单退费详情
   *
   * @param billRefundRecordId 账单退费记录ID
   * @return
   */
  public BillRefundGroupInfoVO findBillRefundDetailByBillRefundRecordId(
      Integer billRefundRecordId) {
    BillRefundGroupInfoVO resultData = new BillRefundGroupInfoVO();
    BillRefundRecord billRefundRecord = mapper.selectByPrimaryKey(billRefundRecordId);
    if (null != billRefundRecord) {
      resultData.setBillRefundRecordId(billRefundRecordId);
      resultData.setReason(billRefundRecord.getReason());
      resultData.setRefundCertificate(billRefundRecord.getRefundCertificate());
      // 设置账单退费详情、退费方式
      setBillRefundGroupInfoValue(resultData, billRefundRecordId);
    }
    return resultData;
  }

  /**
   * 查询账单退费记录信息
   *
   * @param billHandleRecordId 异常处理记录ID
   * @return
   */
  public Map<String, Object> findBillRefundRecordInfo(Integer billHandleRecordId) {
    Map<String, Object> resultMap = new HashMap<>(16);
    BillRefundGroupInfoVO billRefundGroupInfo = new BillRefundGroupInfoVO();
    BillExceptionHandleDetailRecord exceptionHandleDetail = new BillExceptionHandleDetailRecord();
    exceptionHandleDetail.setBillHandleRecordId(billHandleRecordId);
    BillExceptionHandleDetailRecord exceptionHandleDetailRecord =
        billExceptionHandleDetailRecordMapper.selectOne(exceptionHandleDetail);
    if (null != exceptionHandleDetailRecord) {
      Integer associateRecordId = exceptionHandleDetailRecord.getAssociateRecordId();
      BillRefundRecord billRefundRecord = mapper.selectByPrimaryKey(associateRecordId);
      if (null != billRefundRecord) {
        billRefundGroupInfo.setBillRefundRecordId(associateRecordId);
        billRefundGroupInfo.setReason(billRefundRecord.getReason());
        billRefundGroupInfo.setRefundCertificate(billRefundRecord.getRefundCertificate());
      }
      setBillRefundGroupInfoValue(billRefundGroupInfo, associateRecordId);
    }
    resultMap.put("billRefundGroupInfo", billRefundGroupInfo);
    return resultMap;
  }

  /**
   * 设置账单退费信息退费订单详情、退费方式
   *
   * @param billRefundGroupInfo 账单退费vo
   * @param billRefundRecordId 账单退费记录ID
   */
  private void setBillRefundGroupInfoValue(
      BillRefundGroupInfoVO billRefundGroupInfo, Integer billRefundRecordId) {
    // 获取退费订单详情
    List<BillRefundOrderDetailVO> billRefundOrderDetails = getBillRefundOrderDetails(billRefundRecordId);
    billRefundGroupInfo.setBillRefundOrderDetails(
        StringHelper.isEmpty(billRefundOrderDetails)
            ? Lists.newArrayList()
            : billRefundOrderDetails);
    // 获取退费方式
    List<BillRefundPaymentVO> billRefundPayments = getBillRefundPaymentList(billRefundRecordId);
    billRefundGroupInfo.setBillRefundPayments(
        StringHelper.isEmpty(billRefundPayments) ? Lists.newArrayList() : billRefundPayments);
  }

  /**
   * 根据退费记录ID获取退费开单记录列表
   *
   * @param billRefundRecordId 退费记录ID
   * @return
   */
  private List<BillRefundOrderDetailVO> getBillRefundOrderDetails(Integer billRefundRecordId) {
    List<BillRefundOrderDetailVO> billRefundOrderDetails = Lists.newArrayList();
    BillRefundOrderDetail billRefundOrderDetail = new BillRefundOrderDetail();
    billRefundOrderDetail.setBillRefundRecordId(billRefundRecordId);
    List<BillRefundOrderDetail> refundOrderDetails =
        billRefundOrderDetailMapper.select(billRefundOrderDetail);
    if (StringHelper.isNotEmpty(refundOrderDetails)) {
      refundOrderDetails.forEach(
          detail -> {
            BillRefundOrderDetailVO vo = new BillRefundOrderDetailVO();
            Integer orderDetailId = detail.getOrderDetailId();
            vo.setId(detail.getId());
            vo.setOrderDetailId(orderDetailId);
            OrderDetail orderDetail = orderDetailMapper.selectByPrimaryKey(orderDetailId);
            if (null != orderDetail) {
              Integer billingItemId = orderDetail.getBillingItemId();
              vo.setBillingItemId(billingItemId);
              Byte type = orderDetail.getType();
              vo.setType(type);
              switch (type) {
                case 0:
                  BaseTariff tariff = tariffMapper.selectByPrimaryKey(billingItemId);
                  if (null != tariff) {
                    vo.setBillingItemName(tariff.getName());
                    vo.setUnit(tariff.getUnit());
                  }
                  break;
                case 1:
                  BaseOralTariff oralTariff =
                      baseOralTariffMapper.selectByPrimaryKey(billingItemId);
                  if (null != oralTariff) {
                    vo.setBillingItemName(oralTariff.getName());
                    vo.setUnit(oralTariff.getUnit());
                  }
                  break;
                default:
                  break;
              }
              vo.setPrice(orderDetail.getPrice());
              vo.setQuantity(orderDetail.getQuantity());
              vo.setToothBit(orderDetail.getToothBit());
              Integer executorId = orderDetail.getExecutorId();
              vo.setExecutorId(executorId);
              if (null != executorId) {
                SysUserInfoDetail employeeInfo =
                    systemServiceFeign.findSysUserEmployeeInfoByUserId(executorId);
                vo.setExecutorName(null != employeeInfo ? employeeInfo.getName() : "--");
              }
              vo.setRemarks(orderDetail.getRemarks());
            }
            OrderDetailPayRecord orderDetailPayRecord = new OrderDetailPayRecord();
            orderDetailPayRecord.setOrderDetailId(orderDetailId);
            OrderDetailPayRecord detailPayRecord =
                orderDetailPayRecordMapper.selectOne(orderDetailPayRecord);
            vo.setReceivedAmount(
                null != detailPayRecord
                    ? detailPayRecord.getReceivedAmount()
                    : BigDecimal.valueOf(0));
            vo.setRefundAmount(detail.getRefundAmout());
            billRefundOrderDetails.add(vo);
          });
    }
    return billRefundOrderDetails;
  }

  /**
   * 获取退费付款方式列表
   *
   * @param billRefundRecordId 退费记录ID
   * @return
   */
  private List<BillRefundPaymentVO> getBillRefundPaymentList(Integer billRefundRecordId) {
    BillRefundPayDetailRecord refundPayDetail = new BillRefundPayDetailRecord();
    refundPayDetail.setBillRefundRecordId(billRefundRecordId);
    List<BillRefundPayDetailRecord> refundPayDetailRecords =
        billRefundPayDetailRecordMapper.select(refundPayDetail);
    List<BillRefundPaymentVO> billRefundPayments = Lists.newArrayList();
    if (StringHelper.isNotEmpty(refundPayDetailRecords)) {
      refundPayDetailRecords.forEach(
          refundPayDetailRecord -> {
            BillRefundPaymentVO vo = new BillRefundPaymentVO();
            vo.setBillRefundPayDetailRecordId(refundPayDetailRecord.getId());
            Integer accountItemId = refundPayDetailRecord.getAccountItemId();
            vo.setAccountItemId(accountItemId);
            AccountItem accountItem = systemServiceFeign.findAccountItemById(accountItemId);
            if (null != accountItem) {
              vo.setAccountItemName(accountItem.getName());
            }
            vo.setRefundPayAmount(refundPayDetailRecord.getRefundPayAmount());
            vo.setPrincipalAmount(refundPayDetailRecord.getPrincipalAmount());
            vo.setGiftAmount(refundPayDetailRecord.getGiftAmount());
            vo.setRemark(refundPayDetailRecord.getRemark());
            billRefundPayments.add(vo);
          });
    }
    return billRefundPayments;
  }
}
