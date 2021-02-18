package com.yunya.middletable.service;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.BaseBillMapper;
import com.yunya.middletable.dao.treatment.BillRecordMapper;
import com.yunya.middletable.dao.treatment.OrderDetailMapper;
import com.yunya.middletable.dao.treatment.OrderDetailPayRecordMapper;
import com.yunya.middletable.dao.treatment.OrderRecordMapper;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseBillDetail;
import com.yunya.models.treatment.BillRecord;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderDetailPayRecord;
import com.yunya.models.treatment.OrderRecord;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.yunya.framework.common.constant.BusinessConstants.ORDER_FINISH_STATUS;

/**
 * 简介: 中间表账单业务层
 *
 * @author: chow
 * @date: 2020/10/21 16:52
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseBillBiz extends BaseBiz<BaseBillMapper, BaseBill> {

  /** 订单记录 */
  @Autowired private OrderRecordMapper orderRecordMapper;
  /** 订单明细 */
  @Autowired private OrderDetailMapper orderDetailMapper;
  /** 订单明细付款记录 */
  @Autowired private OrderDetailPayRecordMapper orderDetailPayRecordMapper;
  /** 账单记录 */
  @Autowired private BillRecordMapper billRecordMapper;
  /** 中间表账单详情 */
  @Autowired private BaseBillDetailMapper baseBillDetailMapper;
  /** 多线程 */
  @Resource(name = "customizeThreadPool")
  private ExecutorService importExcelThreadPool;

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
      case 2:
      case 1:
        mapper.deleteByPrimaryKey(dataId);
        if (null != bill) {
          mapper.insertSelective(bill);
          baseBillDetailMapper.deleteByBillId(dataId);
          // 保存账单明细
          saveBaseBillDetail(dataId);
        } else {
          baseBillDetailMapper.deleteByBillId(dataId);
        }
        break;
      default:
        break;
    }
  }

  /**
   * 初始化中间表账单信息
   *
   * @param orderRecordId 账单ID（开单记录ID）
   * @return BaseBill
   */
  private BaseBill generateBaseBill(Integer orderRecordId) {
    OrderRecord orderRecord = orderRecordMapper.selectByPrimaryKey(orderRecordId);
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
      if (ORDER_FINISH_STATUS.equals(orderRecord.getStatus())) {
        setBaseBillChargeValue(orderRecordId, baseBill);
      }
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
      baseBill.setPrivilegeDate(billRecord.getPrivilegeDate());
      baseBill.setBillDate(billRecord.getCrtTime());
      baseBill.setBillNum(billRecord.getBillNumber());
      baseBill.setActualAmount(billRecord.getActualReceivableAmount());
      baseBill.setReceivedAmount(billRecord.getReceivedAmount());
      baseBill.setDebtAmount(debtAmount);
      baseBill.setCheckerId(billRecord.getCrtId());
    }
  }

  /**
   * 保存中间表账单明细
   *
   * @param orderRecordId 订单记录ID
   */
  private void saveBaseBillDetail(Integer orderRecordId) {
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    orderDetail.setInservice(true);
    List<OrderDetail> details = orderDetailMapper.select(orderDetail);
    if (StringHelper.isNotEmpty(details)) {
      // 构建中间表账单明细列表
      List<BaseBillDetail> billDetails = generateBaseBillDetail(details);
      if (StringHelper.isNotEmpty(billDetails)) {
        baseBillDetailMapper.deleteByBillId(orderRecordId);
        billDetails.forEach(billDetail -> baseBillDetailMapper.insertSelective(billDetail));
      }
    }
  }

  /**
   * 根据订单明细构建中间表账单明细
   *
   * @param details 订单明细列表
   * @return List<BaseBillDetail>
   */
  private List<BaseBillDetail> generateBaseBillDetail(List<OrderDetail> details) {
    List<BaseBillDetail> billDetails = Lists.newArrayList();
    details.forEach(
        detail -> {
          BaseBillDetail billDetail = new BaseBillDetail();
          // 设置中间表订单明细属性
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
    baseBillDetail.setSourceType(detail.getSourceType());
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
   * 根据条件拉取账单数据并更新中间表
   *
   * @param form 时间段
   */
  public void pullBillData(PullForm form) throws InterruptedException {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    List<String> dateRanges = DateUtil.sliceUpDateRange(startDate, endDate);
    if (StringHelper.isNotEmpty(dateRanges)) {
      CountDownLatch latch = new CountDownLatch(dateRanges.size());
      List<Future> resultFutures = new ArrayList<>();
      for (String date : dateRanges) {
        resultFutures.add(
            importExcelThreadPool.submit(
                () -> {
                  try {
                    Example orderExample = new Example(OrderRecord.class);
                    orderExample
                        .createCriteria()
                        .andEqualTo("inservice", true)
                        .andBetween(
                            "crtTime",
                            new DateTime(date).toDate(),
                            new DateTime(date).plusDays(1).toDate());
                    List<OrderRecord> orderRecords =
                        orderRecordMapper.selectByExample(orderExample);
                    if (StringHelper.isNotEmpty(orderRecords)) {
                      List<BaseBill> baseBills = generateBaseBillList(orderRecords);
                      if (StringHelper.isNotEmpty(baseBills)) {
                        for (BaseBill baseBill : baseBills) {
                          Integer billId = baseBill.getBillId();
                          mapper.deleteByPrimaryKey(billId);
                          baseBillDetailMapper.deleteByBillId(billId);
                          mapper.insertSelective(baseBill);
                          // 保存账单明细
                          saveBaseBillDetail(billId);
                        }
                      }
                    }
                  } finally {
                    latch.countDown();
                  }
                }));
      }
      latch.await();
      BaseTreatmentProcessBiz.printExceptionLog(resultFutures, log);
    }
  }

  /**
   * 批量生成中间表账单记录
   *
   * @param orderRecords 订单记录列表
   * @return 账单记录列表
   */
  private List<BaseBill> generateBaseBillList(List<OrderRecord> orderRecords) {
    List<BaseBill> baseBills = new ArrayList<>();
    if (StringHelper.isNotEmpty(orderRecords)) {
      for (OrderRecord orderRecord : orderRecords) {
        if (orderRecord.getInservice()) {
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
          if (ORDER_FINISH_STATUS.equals(orderRecord.getStatus())) {
            setBaseBillChargeValue(orderRecord.getId(), baseBill);
          }
          baseBills.add(baseBill);
        }
      }
    }
    return baseBills;
  }
}
