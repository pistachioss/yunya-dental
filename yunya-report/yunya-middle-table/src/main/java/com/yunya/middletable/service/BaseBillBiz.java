package com.yunya.middletable.service;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.patient.MemberExpendRecordMapper;
import com.yunya.middletable.dao.patient.PrepaidExpendRecordMapper;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.BaseBillMapper;
import com.yunya.middletable.dao.report.BaseBillPayMapper;
import com.yunya.middletable.dao.treatment.*;
import com.yunya.models.patient_central.MemberExpendRecord;
import com.yunya.models.patient_central.PrepaidExpendRecord;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseBillDetail;
import com.yunya.models.report.BaseBillPay;
import com.yunya.models.treatment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

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
  /** 订单明细付款记录 */
  @Autowired private OrderDetailPayRecordMapper orderDetailPayRecordMapper;
  /** 会员卡消费记录 */
  @Autowired private MemberExpendRecordMapper memberExpendRecordMapper;
  /** 预付款消费记录 */
  @Autowired private PrepaidExpendRecordMapper prepaidExpendRecordMapper;
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
          // 保存账单收费记录
          saveBaseBillPay(dataId);
          // 保存账单收费明细
          saveBaseBillDetail(dataId);
        } else {
          mapper.deleteByPrimaryKey(dataId);
          baseBillPayMapper.deleteByBillId(dataId);
          baseBillDetailMapper.deleteByBillId(dataId);
        }
        break;
      case 1:
        if (null != bill) {
          BaseBill result = mapper.selectByPrimaryKey(dataId);
          if (null == result) {
            mapper.deleteByPrimaryKey(dataId);
            mapper.insertSelective(bill);
            saveBaseBillPay(dataId);
            saveBaseBillDetail(dataId);
          } else {
            mapper.updateByPrimaryKeySelective(bill);
            updateBaseBillPay(dataId);
            updateBaseBillDetail(dataId);
          }
        } else {
          mapper.deleteByPrimaryKey(dataId);
          baseBillPayMapper.deleteByBillId(dataId);
          baseBillDetailMapper.deleteByBillId(dataId);
        }
        break;
      case 2:
        if (null == bill) {
          mapper.deleteByPrimaryKey(dataId);
          baseBillPayMapper.deleteByBillId(dataId);
          baseBillDetailMapper.deleteByBillId(dataId);
        } else {
          mapper.insertSelective(bill);
          saveBaseBillPay(dataId);
          saveBaseBillDetail(dataId);
        }
        break;
      default:
        break;
    }
  }

  /**
   * 初始化中间表账单信息
   *
   * @param dataId 订单ID
   * @return
   */
  private BaseBill generateBaseBill(Integer dataId) {
    OrderRecord orderRecord = orderRecordMapper.selectByPrimaryKey(dataId);
    if (null != orderRecord && orderRecord.getInservice()) {
      BaseBill baseBill = new BaseBill();
      baseBill.setBillId(orderRecord.getId());
      baseBill.setOrgId(orderRecord.getOrgId());
      baseBill.setPatientId(orderRecord.getPatientId());
      baseBill.setTreatmentId(orderRecord.getTreatmentRecordId());
      baseBill.setOrderNum(orderRecord.getOrderRecordNum());
      baseBill.setOrderAmount(orderRecord.getTotalAmount());
      baseBill.setBillerId(orderRecord.getCrtId());
      baseBill.setOrderDate(orderRecord.getCrtTime());
      // 设置账单的收费信息
      setBaseBillChargeValue(dataId, baseBill);
      return baseBill;
    }
    return null;
  }

  /**
   * 设置中间表收费汇总信息
   *
   * @param orderRecordId 订单ID
   * @param baseBill 中间表账单
   */
  private void setBaseBillChargeValue(Integer orderRecordId, BaseBill baseBill) {
    BillRecord bill = new BillRecord();
    bill.setOrderRecordId(orderRecordId);
    bill.setInservice(true);
    BillRecord billRecord = billRecordMapper.selectOne(bill);
    if (null != billRecord) {
      BigDecimal debtAmount = billRecord.getDebtAmount();
      baseBill.setBillStatus(debtAmount.compareTo(BigDecimal.valueOf(0)) > 0 ? (byte) 0 : (byte) 1);
      baseBill.setPrivilegeType(billRecord.getPrivilegeType());
      baseBill.setPrivilegeAmount(billRecord.getPrivilegeAmount());
      baseBill.setBillDate(billRecord.getCrtTime());
      baseBill.setBillNum(billRecord.getBillNumber());
      baseBill.setActualAmount(billRecord.getActualReceivableAmount());
      baseBill.setReceivedAmount(billRecord.getReceivedAmount());
      baseBill.setDebtAmount(debtAmount);
    }
  }

  /**
   * 保存中间表账单支付记录
   *
   * @param orderRecordId 订单记录ID
   */
  private void saveBaseBillPay(Integer orderRecordId) {
    System.out.println("**************************************** 订单记录ID" + orderRecordId);
    BillPayRecord payRecord = new BillPayRecord();
    payRecord.setOrderRecordId(orderRecordId);
    List<BillPayRecord> billPayRecords = billPayRecordMapper.select(payRecord);
    if (StringHelper.isNotEmpty(billPayRecords)) {
      BaseBillPay baseBillPay = new BaseBillPay();
      billPayRecords.forEach(
          billPayRecord -> {
            generateBaseBillPayValue(billPayRecord, baseBillPay);
            baseBillPayMapper.deleteByPrimaryKey(billPayRecord.getId());
            baseBillPayMapper.insertSelective(baseBillPay);
          });
    }
  }

  /**
   * 构建中间表账单收费记录
   *
   * @param payRecord 原始收费记录
   * @param baseBillPay 中间表账单信息
   */
  private void generateBaseBillPayValue(BillPayRecord payRecord, BaseBillPay baseBillPay) {
    Integer payRecordId = payRecord.getId();
    baseBillPay.setBillPayId(payRecordId);
    baseBillPay.setBillId(payRecord.getOrderRecordId());
    baseBillPay.setOrgId(payRecord.getOrgId());
    baseBillPay.setTreatmentId(payRecord.getTreatmentRecordId());
    baseBillPay.setPayeeUserId(payRecord.getCrtId());
    baseBillPay.setPayeeDate(payRecord.getCrtTime());
    baseBillPay.setReceivedAmount(payRecord.getReceivedAmount());
    baseBillPay.setStillOweAmount(payRecord.getStillOweAmount());
    setBaseBillPayAccountItemValue(baseBillPay, payRecordId);
  }

  /**
   * 保存中间表账单明细
   *
   * @param orderRecordId 订单记录ID
   */
  private void saveBaseBillDetail(Integer orderRecordId) {
    System.out.println("******************************保存中间表账单明细");
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    orderDetail.setInservice(true);
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

  /**
   * 根据订单明细构建中间表账单明细
   *
   * @param details 订单明细列表
   * @return
   */
  private List<BaseBillDetail> generateBaseBillDetail(List<OrderDetail> details) {
    List<BaseBillDetail> billDetails = Lists.newArrayList();
    details.forEach(
        detail -> {
          BaseBillDetail billDetail = new BaseBillDetail();
          setBaseBillDetailValue(detail, billDetail);
          billDetails.add(billDetail);
        });
    return billDetails;
  }

  /**
   * 设置中间表账单明细属性
   *
   * @param detail 原始订单明细
   * @param baseBillDetail 中间表账单明细
   */
  private void setBaseBillDetailValue(OrderDetail detail, BaseBillDetail baseBillDetail) {
    Integer detailId = detail.getId();
    baseBillDetail.setBillDetailId(detailId);
    baseBillDetail.setOrgId(detail.getOrgId());
    baseBillDetail.setBillId(detail.getOrderRecordId());
    baseBillDetail.setExecutorId(detail.getExecutorId());
    baseBillDetail.setItemId(detail.getBillingItemId());
    baseBillDetail.setItemType(detail.getType());
    baseBillDetail.setQuantity(detail.getQuantity());
    baseBillDetail.setPrice(detail.getPrice());
    OrderDetailPayRecord detailPayRecord = new OrderDetailPayRecord();
    detailPayRecord.setOrderDetailId(detailId);
    detailPayRecord.setInservice(true);
    OrderDetailPayRecord detailPayRecordResult =
        orderDetailPayRecordMapper.selectOne(detailPayRecord);
    if (null != detailPayRecordResult) {
      baseBillDetail.setDiscountAmount(detailPayRecordResult.getPrivilegeAmount());
      baseBillDetail.setCouponWorkload(detailPayRecordResult.getCouponWorkload());
      baseBillDetail.setReceivedAmount(detailPayRecordResult.getReceivedAmount());
    }
  }

  /**
   * 更新中间表账单明细信息
   *
   * @param orderRecordId 订单记录ID
   */
  private void updateBaseBillDetail(Integer orderRecordId) {
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    List<OrderDetail> details = orderDetailMapper.select(orderDetail);
    if (StringHelper.isNotEmpty(details)) {
      for (OrderDetail detail : details) {
        Integer detailId = detail.getId();
        BaseBillDetail baseBillDetail = baseBillDetailMapper.selectByPrimaryKey(detailId);
        if (null != baseBillDetail) {
          setBaseBillDetailValue(detail, baseBillDetail);
          baseBillDetailMapper.updateByPrimaryKeySelective(baseBillDetail);
        } else {
          baseBillDetail = new BaseBillDetail();
          setBaseBillDetailValue(detail, baseBillDetail);
          baseBillDetailMapper.deleteByPrimaryKey(detailId);
          baseBillDetailMapper.insertSelective(baseBillDetail);
        }
      }
    }
  }

  /**
   * 更新中间表账单支付记录
   *
   * @param orderRecordId 订单记录ID
   */
  private void updateBaseBillPay(Integer orderRecordId) {
    BillPayRecord billPayRecord = new BillPayRecord();
    billPayRecord.setOrderRecordId(orderRecordId);
    List<BillPayRecord> billPayRecords = billPayRecordMapper.select(billPayRecord);
    if (StringHelper.isNotEmpty(billPayRecords)) {
      for (BillPayRecord payRecord : billPayRecords) {
        Integer payRecordId = payRecord.getId();
        BaseBillPay baseBillPay = baseBillPayMapper.selectByPrimaryKey(payRecordId);
        if (null != baseBillPay) {
          generateBaseBillPayValue(payRecord, baseBillPay);
          baseBillPayMapper.updateByPrimaryKeySelective(baseBillPay);
        } else {
          baseBillPay = new BaseBillPay();
          generateBaseBillPayValue(payRecord, baseBillPay);
          baseBillPayMapper.deleteByPrimaryKey(payRecordId);
          baseBillPayMapper.insertSelective(baseBillPay);
        }
      }
    }
  }

  /**
   * 设置中间表账单收费记录入账方式
   *
   * @param baseBillPay 账单收费记录
   * @param payRecordId 收费记录ID
   */
  private void setBaseBillPayAccountItemValue(BaseBillPay baseBillPay, Integer payRecordId) {
    MemberExpendRecord memberExpand = new MemberExpendRecord();
    memberExpand.setBillPayRecordId(payRecordId);
    List<MemberExpendRecord> memberExpendRecords = memberExpendRecordMapper.select(memberExpand);
    if (StringHelper.isNotEmpty(memberExpendRecords)) {
      memberExpendRecords.forEach(
          mer -> {
            BigDecimal principal = mer.getExpendPrincipal();
            baseBillPay.setMemberPrincipleAmount(
                baseBillPay.getMemberPrincipleAmount().add(principal));
            BigDecimal gift = mer.getExpendGift();
            baseBillPay.setMemberGiftAmount(baseBillPay.getMemberGiftAmount().add(gift));
          });
    }
    PrepaidExpendRecord prepaidExpand = new PrepaidExpendRecord();
    prepaidExpand.setBillPayRecordId(payRecordId);
    List<PrepaidExpendRecord> prepaidExpendRecords =
        prepaidExpendRecordMapper.select(prepaidExpand);
    if (StringHelper.isNotEmpty(prepaidExpendRecords)) {
      prepaidExpendRecords.forEach(
          per -> {
            BigDecimal principal = per.getExpendPrincipal();
            baseBillPay.setPrepaidPrincipleAmount(
                baseBillPay.getPrepaidPrincipleAmount().add(principal));
            BigDecimal gift = per.getExpendGift();
            baseBillPay.setPrepaidGiftAmount(baseBillPay.getPrepaidGiftAmount().add(gift));
          });
    }
    BillPayDetailRecord payDetailRecord = new BillPayDetailRecord();
    payDetailRecord.setBillPayRecordId(payRecordId);
    List<BillPayDetailRecord> payDetailRecords = billPayDetailRecordMapper.select(payDetailRecord);
    if (StringHelper.isNotEmpty(payDetailRecords)) {
      for (BillPayDetailRecord detailRecord : payDetailRecords) {
        if (detailRecord.getType() == 2) {
          Integer accountItemId = detailRecord.getAccountItemId();
          // todo 固定部分支付方式
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
  }

  /**
   * 根据条件拉取账单数据并更新中间表
   *
   * @param form 时间段
   */
  public void pullBillData(PullForm form) {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    Example orderExample = new Example(OrderRecord.class);
    orderExample.createCriteria().andBetween("updTime", startDate, endDate);
    List<OrderRecord> orderRecords = orderRecordMapper.selectByExample(orderExample);
    if (StringHelper.isNotEmpty(orderRecords)) {
      orderRecords.stream()
          .map(OrderRecord::getId)
          .forEach(
              orderRecordId -> {
                BaseBill baseBill = generateBaseBill(orderRecordId);
                mapper.deleteByPrimaryKey(orderRecordId);
                if (null != baseBill) {
                  mapper.insertSelective(baseBill);
                  // 保存账单收费记录
                  saveBaseBillPay(orderRecordId);
                  // 保存账单收费明细
                  saveBaseBillDetail(orderRecordId);
                } else {
                  baseBillPayMapper.deleteByBillId(orderRecordId);
                  baseBillDetailMapper.deleteByBillId(orderRecordId);
                }
              });
    }
  }
}
