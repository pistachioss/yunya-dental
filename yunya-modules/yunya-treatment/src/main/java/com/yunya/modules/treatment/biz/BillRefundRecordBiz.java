package com.yunya.modules.treatment.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.vo.BillRefundGroupInfoVO;
import com.yunya.feign.treatment.domain.vo.BillRefundOrderDetailVO;
import com.yunya.feign.treatment.domain.vo.BillRefundPaymentVO;
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
      List<BillRefundOrderDetailVO> billRefundOrderDetails =
          getBillRefundOrderDetails(associateRecordId);
      billRefundGroupInfo.setBillRefundOrderDetails(billRefundOrderDetails);
      List<BillRefundPaymentVO> billRefundPayments = getBillRefundPaymentList(associateRecordId);
      billRefundGroupInfo.setBillRefundPayments(billRefundPayments);
    }
    resultMap.put("billRefundGroupInfo", billRefundGroupInfo);
    return resultMap;
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
