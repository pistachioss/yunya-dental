package com.yunya.middletable.service;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.BaseBillMapper;
import com.yunya.middletable.dao.report.BasePatientOriginLogMapper;
import com.yunya.middletable.dao.report.credits_shop.CreditsShopMapper;
import com.yunya.middletable.dao.treatment.BillRecordMapper;
import com.yunya.middletable.dao.treatment.OrderDetailMapper;
import com.yunya.middletable.dao.treatment.OrderDetailPayRecordMapper;
import com.yunya.middletable.dao.treatment.OrderRecordMapper;
import com.yunya.models.credits_shop.CreditsShop;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseBillDetail;
import com.yunya.models.report.BasePatientOriginLog;
import com.yunya.models.treatment.BillRecord;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderDetailPayRecord;
import com.yunya.models.treatment.OrderRecord;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
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
  @Resource private OrderRecordMapper orderRecordMapper;
  /** 订单明细 */
  @Resource private OrderDetailMapper orderDetailMapper;
  /** 订单明细付款记录 */
  @Resource private OrderDetailPayRecordMapper orderDetailPayRecordMapper;
  /** 账单记录 */
  @Resource private BillRecordMapper billRecordMapper;
  /** 中间表账单详情 */
  @Resource private BaseBillDetailMapper baseBillDetailMapper;
  /** 患者推荐关系 */
  @Resource private BasePatientOriginLogMapper basePatientOriginLogMapper;
  /** 患者积分信息 */
  @Resource private CreditsShopMapper creditsShopMapper;
  /** 多线程 */
  @Resource(name = "customizeThreadPool")
  private ExecutorService importExcelThreadPool;

  /**
   * 更新开单明细
   *
   * @param msg 消息
   */
  public void updateBaseBillDetail(MessageModel msg) {
    Integer dataId = (Integer) msg.getParamMap().get("id");
    OrderDetail detail = orderDetailMapper.selectByPrimaryKey(dataId);
    if (detail != null && detail.getInservice()) {
      BaseBillDetail baseBillDetail = baseBillDetailMapper.selectByPrimaryKey(dataId);
      if (baseBillDetail != null) {
        baseBillDetail.setExecutorId(detail.getExecutorId());
        baseBillDetailMapper.updateByPrimaryKeySelective(baseBillDetail);
      }
    }
  }

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
        // 推荐积分
        if (null != bill){
          addPatientIntegral(bill.getPatientId());
        }
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
   * 判断是否首次下单，若是则推荐者增加500积分
   */
  public void addPatientIntegral(Integer patientId){
   Integer count =  mapper.selectCountByPatientId(patientId);
   CreditsShop addPatientIntegral = new CreditsShop();
   if (count <= 0){
     BasePatientOriginLog basePatientOrigin = new BasePatientOriginLog();
     basePatientOrigin.setPatientId(patientId);
     basePatientOrigin.setOriginType(2);
     BasePatientOriginLog basePatientOriginLog = basePatientOriginLogMapper.selectOne(basePatientOrigin);
     if (basePatientOriginLog != null){
       CreditsShop patientCreditsShop = creditsShopMapper.selectLastCredits(basePatientOriginLog.getOriginId());
       if (patientCreditsShop != null){
         addPatientIntegral.setPatientId(patientCreditsShop.getPatientId());
         // recommend 患者推荐
         addPatientIntegral.setType("recommend");
         addPatientIntegral.setChannel((byte)0);
         addPatientIntegral.setOrderNum("");
         addPatientIntegral.setCreditsAccount(patientCreditsShop.getCreditsAccount()+500);
         addPatientIntegral.setCredits(500L);
         addPatientIntegral.setCreditsOption((byte)1);
         addPatientIntegral.setActualPrice(0);
         addPatientIntegral.setItemCode("");
         addPatientIntegral.setDescription("500");
         addPatientIntegral.setRemarks("患者推荐");
         addPatientIntegral.setInservice(false);
         addPatientIntegral.setCrtId(patientId);
         addPatientIntegral.setCrtTime(new Date(System.currentTimeMillis()));
       }else {
         // 没有患者积分帐户就新建
         addPatientIntegral.setPatientId(patientCreditsShop.getPatientId());
         // recommend 患者推荐
         addPatientIntegral.setType("recommend");
         addPatientIntegral.setChannel((byte)0);
         addPatientIntegral.setOrderNum("");
         addPatientIntegral.setCreditsAccount(500L);
         addPatientIntegral.setCredits(500L);
         addPatientIntegral.setCreditsOption((byte)1);
         addPatientIntegral.setActualPrice(0);
         addPatientIntegral.setItemCode("");
         addPatientIntegral.setDescription("500");
         addPatientIntegral.setRemarks("患者推荐");
         addPatientIntegral.setInservice(false);
         addPatientIntegral.setCrtId(patientId);
         addPatientIntegral.setCrtTime(new Date(System.currentTimeMillis()));
       }
       // 增加500积分
       creditsShopMapper.insertSelective(addPatientIntegral);
     }
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
      baseBill.setBillStatus((byte) 0);
      baseBill.setPrivilegeType((byte) 0);
      baseBill.setPrivilegeAmount(new BigDecimal("0"));
      baseBill.setActualAmount(new BigDecimal("0"));
      baseBill.setReceivedAmount(new BigDecimal("0"));
      baseBill.setDebtAmount(new BigDecimal("0"));
      // 设置账单的收费信息
      setBaseBillChargeValue(orderRecord, baseBill);
      return baseBill;
    }
    return null;
  }

  /**
   * 设置中间表收费汇总信息
   *
   * @param orderRecord 订单
   * @param baseBill 中间表账单
   */
  private void setBaseBillChargeValue(OrderRecord orderRecord, BaseBill baseBill) {
    BillRecord bill = new BillRecord();
    if (ORDER_FINISH_STATUS.equals(orderRecord.getStatus())) {
      bill.setOrderRecordId(orderRecord.getId());
      bill.setInservice(true);
      BillRecord billRecord = billRecordMapper.selectOne(bill);
      if (null != billRecord) {
        BigDecimal debtAmount = billRecord.getDebtAmount();
        baseBill.setBillStatus(
            debtAmount.compareTo(BigDecimal.valueOf(0)) > 0 ? (byte) 0 : (byte) 1);
        baseBill.setPrivilegeType(billRecord.getPrivilegeType());
        baseBill.setPrivilegeOrgId(billRecord.getPrivilegeOrgId());
        baseBill.setFirstPrivilege(billRecord.getFirstPrivilege());
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
    baseBillDetail.setDiscountAmount(new BigDecimal("0"));
    baseBillDetail.setCouponWorkload(new BigDecimal("0"));
    baseBillDetail.setReceivedAmount(new BigDecimal("0"));
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
                        .andCondition(
                            "crt_time >= '" + new DateTime(date).toString("yyyy-MM-dd") + "'")
                        .andCondition(
                            "crt_time < '"
                                + new DateTime(date).plusDays(1).toString("yyyy-MM-dd")
                                + "'");
                    List<OrderRecord> orderRecords =
                        orderRecordMapper.selectByExample(orderExample);
                    if (StringHelper.isNotEmpty(orderRecords)) {
                      Set<BaseBill> baseBills = generateBaseBillList(orderRecords);
                      if (StringHelper.isNotEmpty(baseBills)) {
                        Example baseBillEmp = new Example(BaseBill.class);
                        baseBillEmp
                            .createCriteria()
                            .andCondition(
                                "order_date >= '" + new DateTime(date).toString("yyyy-MM-dd") + "'")
                            .andCondition(
                                "order_date < '"
                                    + new DateTime(date).plusDays(1).toString("yyyy-MM-dd")
                                    + "'");
                        mapper.deleteByExample(baseBillEmp);
                        mapper.batchInsertSelective(baseBills);

                        // 批量生成并保存中间表开单明细
                        generateAndSaveBaseBillDetailByOrderDate(date);

                        /*for (BaseBill baseBill : baseBills) {
                          Integer billId = baseBill.getBillId();
                          mapper.deleteByPrimaryKey(billId);
                          baseBillDetailMapper.deleteByBillId(billId);
                          mapper.insertSelective(baseBill);
                          // 保存账单明细
                          saveBaseBillDetail(billId);
                        }*/
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
   * 根据日期批量生成中间表开单明细并保存
   *
   * @param date 查询日期
   */
  private void generateAndSaveBaseBillDetailByOrderDate(String date) {
    Example orderDetailEmp = new Example(OrderDetail.class);
    orderDetailEmp
        .createCriteria()
        .andEqualTo("inservice", true)
        .andCondition("crt_time >= '" + new DateTime(date).toString("yyyy-MM-dd") + "'")
        .andCondition("crt_time < '" + new DateTime(date).plusDays(1).toString("yyyy-MM-dd") + "'");
    List<OrderDetail> details = orderDetailMapper.selectByExample(orderDetailEmp);
    if (StringHelper.isNotEmpty(details)) {
      List<BaseBillDetail> baseBillDetails = generateBaseBillDetailByOrderDetail(details);

      // 订单明细收费记录
      Example orderDetailPayRecordEmp = new Example(OrderDetailPayRecord.class);
      orderDetailPayRecordEmp
          .createCriteria()
          .andEqualTo("inservice", true)
          .andCondition("crt_time >= '" + new DateTime(date).toString("yyyy-MM-dd") + "'")
          .andCondition(
              "crt_time < '" + new DateTime(date).plusDays(1).toString("yyyy-MM-dd") + "'");
      List<OrderDetailPayRecord> orderDetailPayRecords =
          orderDetailPayRecordMapper.selectByExample(orderDetailPayRecordEmp);
      if (StringHelper.isNotEmpty(baseBillDetails)) {
        for (BaseBillDetail baseBillDetail : baseBillDetails) {
          for (OrderDetailPayRecord orderDetailPayRecord : orderDetailPayRecords) {
            Integer billDetailId = baseBillDetail.getBillDetailId();
            Integer orderDetailId = orderDetailPayRecord.getOrderDetailId();
            if (billDetailId.equals(orderDetailId)) {
              baseBillDetail.setDiscountAmount(orderDetailPayRecord.getPrivilegeAmount());
              baseBillDetail.setCouponWorkload(orderDetailPayRecord.getCouponWorkload());
              baseBillDetail.setReceivedAmount(orderDetailPayRecord.getReceivedAmount());
            }
          }
        }

        for (BaseBillDetail baseBillDetail : baseBillDetails) {
          baseBillDetailMapper.deleteByPrimaryKey(baseBillDetail.getBillDetailId());
        }
        baseBillDetailMapper.batchInsertSelective(baseBillDetails);
      }
    }
  }

  /**
   * 构建中间表订单明细列表
   *
   * @param details 订单列表
   * @return
   */
  private List<BaseBillDetail> generateBaseBillDetailByOrderDetail(List<OrderDetail> details) {
    List<BaseBillDetail> billDetails = new ArrayList<>();
    for (OrderDetail detail : details) {
      BaseBillDetail baseBillDetail = new BaseBillDetail();
      baseBillDetail.setBillDetailId(detail.getId());
      baseBillDetail.setOrgId(detail.getOrgId());
      baseBillDetail.setBillId(detail.getOrderRecordId());
      baseBillDetail.setExecutorId(detail.getExecutorId());
      baseBillDetail.setItemId(detail.getBillingItemId());
      baseBillDetail.setItemType(detail.getType());
      baseBillDetail.setSourceType(detail.getSourceType());
      baseBillDetail.setQuantity(detail.getQuantity());
      baseBillDetail.setPrice(detail.getPrice());
      baseBillDetail.setDiscountAmount(new BigDecimal("0"));
      baseBillDetail.setCouponWorkload(new BigDecimal("0"));
      baseBillDetail.setReceivedAmount(new BigDecimal("0"));
      billDetails.add(baseBillDetail);
    }
    return billDetails;
  }

  /**
   * 批量生成中间表账单记录
   *
   * @param orderRecords 订单记录列表
   * @return 账单记录列表
   */
  private Set<BaseBill> generateBaseBillList(List<OrderRecord> orderRecords) {
    Set<BaseBill> baseBills = new LinkedHashSet<>();
    if (StringHelper.isNotEmpty(orderRecords)) {
      for (OrderRecord orderRecord : orderRecords) {
        BaseBill baseBill = new BaseBill();
        baseBill.setBillId(orderRecord.getId());
        baseBill.setOrgId(orderRecord.getOrgId());
        baseBill.setPatientId(orderRecord.getPatientId());
        baseBill.setTreatmentId(orderRecord.getTreatmentRecordId());
        baseBill.setOrderNum(orderRecord.getOrderRecordNum());
        baseBill.setOrderAmount(orderRecord.getTotalAmount());
        baseBill.setBillerId(orderRecord.getCrtId());
        baseBill.setOrderDate(orderRecord.getCrtTime());
        baseBill.setBillStatus((byte) 0);
        baseBill.setPrivilegeType((byte) 0);
        baseBill.setPrivilegeAmount(new BigDecimal("0"));
        baseBill.setActualAmount(new BigDecimal("0"));
        baseBill.setReceivedAmount(new BigDecimal("0"));
        baseBill.setDebtAmount(new BigDecimal("0"));
        // 设置账单的收费信息
        setBaseBillChargeValue(orderRecord, baseBill);
        baseBills.add(baseBill);
      }
    }
    return baseBills;
  }
}
