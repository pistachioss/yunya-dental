package com.yunya.middletable.service;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.BaseBillMapper;
import com.yunya.middletable.dao.report.BaseBillPayMapper;
import com.yunya.middletable.dao.treatment.*;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseBillDetail;
import com.yunya.models.report.BaseBillPay;
import com.yunya.models.treatment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 中间表账单业务层
 *
 * @author: chow
 * @date: 2020/10/21 16:52
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseBillBiz extends BaseBiz<BaseBillMapper, BaseBill> {

  /** 订单记录 */
  @Autowired private OrderRecordMapper orderRecordMapper;
  /** 订单明细 */
  @Autowired private OrderDetailMapper orderDetailMapper;
  /** 订单明细付款 */
  @Autowired private OrderDetailPayRecordMapper orderDetailPayRecordMapper;
  /** 助手配诊 */
  @Autowired private AssistantMatchingRecordMapper assistantMatchingRecordMapper;
  /** 账单记录 */
  @Autowired private BillRecordMapper billRecordMapper;
  /** 账单付款记录 */
  @Autowired private BillPayRecordMapper billPayRecordMapper;
  /** 账单付款详情 */
  @Autowired private BillPayDetailRecordMapper billPayDetailRecordMapper;
  /** 中间表账单付款记录 */
  @Autowired private BaseBillPayMapper baseBillPayMapper;
  /** 中间表账单详情 */
  @Autowired private BaseBillDetailMapper baseBillDetailMapper;

  /**
   * 根据消息操作中间表账单
   *
   * @param msg 消息
   */
  public void operateBill(MessageModel msg) {
    Integer dataId = (Integer) msg.getParamMap().get("id");
    BaseBill bill = generateBaseBill(dataId);
    Integer operateType = msg.getOperateType();
    switch (operateType) {
      case 0:
        mapper.deleteByPrimaryKey(dataId);
        if (null != bill) {
          mapper.insertSelective(bill);
          BillPayRecord payRecord = new BillPayRecord();
          payRecord.setOrderRecordId(dataId);
          List<BillPayRecord> billPayRecords = billPayRecordMapper.select(payRecord);
          if (StringHelper.isNotEmpty(billPayRecords)) {
            billPayRecords.forEach(
                billPayRecord -> {
                  Integer payRecordId = billPayRecord.getId();
                  BaseBillPay baseBillPay = new BaseBillPay();
                  baseBillPay.setBillPayId(payRecordId);
                  baseBillPay.setBillId(dataId);
                  baseBillPay.setOrgId(billPayRecord.getOrgId());
                  baseBillPay.setTreatmentId(billPayRecord.getTreatmentRecordId());
                  baseBillPay.setPayeeUserId(billPayRecord.getCrtId());
                  baseBillPay.setPayeeDate(billPayRecord.getCrtTime());
                  baseBillPay.setReceivedAmount(billPayRecord.getReceivedAmount());
                  baseBillPay.setStillOweAmount(billPayRecord.getStillOweAmount());
                  BillPayDetailRecord payDetailRecord = new BillPayDetailRecord();
                  payDetailRecord.setBillPayRecordId(payRecordId);
                  List<BillPayDetailRecord> payDetailRecords =
                      billPayDetailRecordMapper.select(payDetailRecord);
                  if (StringHelper.isNotEmpty(payDetailRecords)) {
                    for (BillPayDetailRecord detailRecord : payDetailRecords) {
                      if (detailRecord.getType() == 2) {
                        Integer accountItemId = detailRecord.getAccountItemId();
                        switch (accountItemId) {
                          case 56:
                            baseBillPay.setWechatAmount(detailRecord.getAmount());
                            break;
                          case 57:
                            baseBillPay.setAlipayAmount(detailRecord.getAmount());
                            break;
                          case 58:
                            baseBillPay.setCashAmount(detailRecord.getAmount());
                            break;
                          case 59:
                            baseBillPay.setBankCardAmount(detailRecord.getAmount());
                            break;
                          case 62:
                            baseBillPay.setCityMedicalInsuranceAmount(detailRecord.getAmount());
                            break;
                          case 65:
                            baseBillPay.setProvinceMedicalInsuranceAmount(detailRecord.getAmount());
                            break;
                          default:
                            break;
                        }
                      }
                    }
                  }
                });
          }
          OrderDetail orderDetail = new OrderDetail();
          orderDetail.setOrderRecordId(dataId);
          List<OrderDetail> details = orderDetailMapper.select(orderDetail);
          if (StringHelper.isNotEmpty(details)) {
            List<BaseBillDetail> billDetails = generateBaseBillDetail(details);
            if (StringHelper.isNotEmpty(billDetails)) {
              billDetails.forEach(
                  billDetail -> {
                    baseBillDetailMapper.deleteByPrimaryKey(billDetail.getBillDetailId());
                    baseBillDetailMapper.insertSelective(billDetail);
                  });
            }
          }
        }
        break;
      case 1:
        if (null != bill) {
          BaseBill result = mapper.selectByPrimaryKey(dataId);
          if (null == result) {
            mapper.deleteByPrimaryKey(dataId);
            mapper.insertSelective(bill);
          } else {
            mapper.updateByPrimaryKeySelective(bill);
          }
        } else {
          mapper.deleteByPrimaryKey(dataId);
        }
        break;
      case 2:
        if (null == bill) {
          mapper.deleteByPrimaryKey(dataId);
        } else {
          mapper.insertSelective(bill);
        }
        break;
      default:
        break;
    }
  }

  /**
   * 根据订单明细构建账单明细
   *
   * @param details 订单明细列表
   * @return
   */
  private List<BaseBillDetail> generateBaseBillDetail(List<OrderDetail> details) {
    List<BaseBillDetail> billDetails = Lists.newArrayList();
    details.forEach(
        detail -> {
          BaseBillDetail billDetail = new BaseBillDetail();
          Integer detailId = detail.getId();
          billDetail.setBillDetailId(detailId);
          billDetail.setBillId(detail.getOrderRecordId());
          billDetail.setExecutorId(detail.getExecutorId());
          billDetail.setItemId(detail.getBillingItemId());
          billDetail.setItemType(detail.getType());
          billDetail.setQuantity(detail.getQuantity());
          billDetail.setPrice(detail.getPrice());
          OrderDetailPayRecord detailPayRecord = new OrderDetailPayRecord();
          detailPayRecord.setOrderDetailId(detailId);
          OrderDetailPayRecord detailPayRecordResult =
              orderDetailPayRecordMapper.selectOne(detailPayRecord);
          if (null != detailPayRecordResult) {
            billDetail.setDiscountAmount(detailPayRecordResult.getPrivilegeAmount());
            billDetail.setCouponWorkload(detailPayRecordResult.getCouponWorkload());
            billDetail.setReceivedAmount(detailPayRecordResult.getReceivedAmount());
          }
          billDetails.add(billDetail);
        });
    return billDetails;
  }

  /**
   * 初始化中间表账单
   *
   * @param dataId 订单ID
   * @return
   */
  private BaseBill generateBaseBill(Integer dataId) {
    OrderRecord orderRecord = orderRecordMapper.selectByPrimaryKey(dataId);
    if (null != orderRecord) {
      BaseBill baseBill = new BaseBill();
      baseBill.setBillId(orderRecord.getId());
      baseBill.setOrgId(orderRecord.getOrgId());
      baseBill.setPatientId(orderRecord.getPatientId());
      baseBill.setTreatmentId(orderRecord.getTreatmentRecordId());
      baseBill.setOrderNum(orderRecord.getOrderRecordNum());
      baseBill.setOrderAmount(orderRecord.getTotalAmount());
      baseBill.setOrderDate(orderRecord.getCrtTime());
      setBaseBillAssistantValue(dataId, baseBill);
      setBaseBillChargeValue(dataId, baseBill);
      return baseBill;
    }
    return null;
  }

  /**
   * 设置中间表收费信息
   *
   * @param orderRecordId 订单ID
   * @param baseBill 中间表账单
   */
  private void setBaseBillChargeValue(Integer orderRecordId, BaseBill baseBill) {
    BillRecord bill = new BillRecord();
    bill.setOrderRecordId(orderRecordId);
    BillRecord billRecord = billRecordMapper.selectOne(bill);
    if (null != billRecord) {
      BigDecimal debtAmount = billRecord.getDebtAmount();
      baseBill.setBillStatus(debtAmount.compareTo(BigDecimal.valueOf(0)) > 0 ? (byte) 0 : (byte) 1);
      baseBill.setPrivilegeType(billRecord.getPrivilegeType());
      baseBill.setPrivilegeAmount(billRecord.getPrivilegeAmount());
      baseBill.setBillNum(billRecord.getBillNumber());
      baseBill.setActualAmount(billRecord.getActualReceivableAmount());
      baseBill.setReceivedAmount(billRecord.getReceivedAmount());
      baseBill.setDebtAmount(debtAmount);
    }
  }

  /**
   * 设置中间表账单关联助手
   *
   * @param orderRecordId 订单ID
   * @param baseBill 中间表账单
   */
  private void setBaseBillAssistantValue(Integer orderRecordId, BaseBill baseBill) {
    AssistantMatchingRecord assistantMatchRecord = new AssistantMatchingRecord();
    assistantMatchRecord.setOrderRecordId(orderRecordId);
    List<AssistantMatchingRecord> matchingRecords =
        assistantMatchingRecordMapper.select(assistantMatchRecord);
    if (StringHelper.isNotEmpty(matchingRecords)) {
      for (AssistantMatchingRecord record : matchingRecords) {
        Byte type = record.getType();
        Integer assistantId = record.getAssistantId();
        switch (type) {
          case 0:
            baseBill.setAssistant1(assistantId);
            break;
          case 1:
            baseBill.setAssistant2(assistantId);
            break;
          default:
            baseBill.setAssistant3(assistantId);
            break;
        }
      }
    }
  }

  /**
   * 根据条件拉取账单数据并更新中间表
   *
   * @param form 时间段
   */
  public void pullBillData(PullForm form) {}
}
