package com.yunya.middletable.service;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.BaseBillMapper;
import com.yunya.middletable.dao.report.BasePatientOriginLogMapper;
import com.yunya.middletable.dao.report.credits_shop.CreditsShopMapper;
import com.yunya.middletable.dao.treatment.BillRecordMapper;
import com.yunya.middletable.dao.treatment.OrderDetailMapper;
import com.yunya.middletable.dao.treatment.OrderDetailPayRecordMapper;
import com.yunya.middletable.dao.treatment.OrderRecordMapper;
import com.yunya.middletable.service.credits_shop.BillCreditsCallback;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseBillDetail;
import com.yunya.models.report.BasePatientOriginLog;
import com.yunya.models.report.CreditsShop;
import com.yunya.models.treatment.BillRecord;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderDetailPayRecord;
import com.yunya.models.treatment.OrderRecord;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
  /** 员工账单时统计*/
  @Autowired private StatEmpBillBiz statEmpBillBiz;
  /** 员工使用优惠时统计*/
  @Autowired private StatEmpPrivilegeBiz statEmpPrivilegeBiz;

  @Autowired private BaseBillPayBiz baseBillPayBiz;

  /** 多线程 */
  @Resource(name = "customizeThreadPool")
  private ExecutorService importExcelThreadPool;

  @Resource(name = "billCreditsCallbackImpl")
  private BillCreditsCallback baseBillPayCallback;
  @Autowired
  private RedisUtils redisUtils;

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
        baseBillDetail.setConsulterId(detail.getConsulterId());
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
        mapper.deleteByPrimaryKey(dataId);
        if (null != bill) {
          mapper.insertSelective(bill);
          baseBillDetailMapper.deleteByBillId(dataId);
          // 保存账单明细
          saveBaseBillDetail(dataId);
          // 推荐积分
          addPatientIntegral(bill.getPatientId());
          // 回调收费增加积分
          log.info("回调积分baseBillPayCallback");
          baseBillPayBiz.addCallBack(bill.getBillId(), baseBillPayCallback);
          log.info("消息dataId = {}",dataId);
          // 做接口幂等性校验
          String key = String.format("msgId:%d", dataId);
          if (redisUtils.hasKey(key)) {
            return;
          }
          redisUtils.set(key,"",15, TimeUnit.SECONDS);
        } else {
          baseBillDetailMapper.deleteByBillId(dataId);
        }
      default:
        statisticsEmployeeWorkload(bill, dataId);
        break;
    }
  }

  /**
   * 统计执行人的账单数据
   *
   * @param bill
   * @param dataId
   */
  private void statisticsEmployeeWorkload(BaseBill bill, Integer dataId) {
      if (ObjectUtils.isEmpty(bill)) {
        bill = new BaseBill();
        BillRecord query = new BillRecord();
        query.setOrderRecordId(dataId);
        query.setInservice(false);
        BillRecord billRecord = billRecordMapper.selectOne(query);
        record2baseReport(billRecord, bill);
      }
      if (!ObjectUtils.isEmpty(bill)) {
        Integer billId = bill.getBillId();
        OrderDetail query = new OrderDetail();
        query.setOrderRecordId(billId);
        List<OrderDetail> orderDetails = orderDetailMapper.select(query);
        if (!ObjectUtils.isEmpty(bill.getBillDate())) {
          statEmpBillBiz.statisticsEmployeeByBillDate(orderDetails, bill);
        }
        if (!ObjectUtils.isEmpty(bill.getPrivilegeDate())) {
          statEmpPrivilegeBiz.statisticsEmployeeByPrivilegeDate(orderDetails, bill);
        }
      }
  }

  /** 判断是否首次下单，若是则推荐者增加500积分 */
  public void addPatientIntegral(Integer patientId) {
    CreditsShop addPatientIntegral = new CreditsShop();
    if (isPatientFirstBill(patientId)) {
      String type = "recommend";
      Integer originId = findPatientOriginId(patientId, type);
      if (!ObjectUtils.isEmpty(originId)) {
        CreditsShop patientCreditsShop =
            creditsShopMapper.selectLastCredits(originId);
        if (patientCreditsShop != null) {
          addPatientIntegral.setPatientId(patientCreditsShop.getPatientId());
          // recommend 患者推荐
          addPatientIntegral.setCreditsAccount(patientCreditsShop.getCreditsAccount() + 500);
        } else {
          // 没有患者积分帐户就新建
          addPatientIntegral.setPatientId(originId);
          // recommend 患者推荐
          addPatientIntegral.setCreditsAccount(500L);
        }
        addPatientIntegral.setCreditsOption((byte) 0);
        addPatientIntegral.setChannel((byte) 0);
        addPatientIntegral.setType(type);
        addPatientIntegral.setCredits(500L);
        addPatientIntegral.setDescription("患者推荐");
        addPatientIntegral.setCrtId(patientId);
        addPatientIntegral.setCrtTime(new Date(System.currentTimeMillis()));
        log.info("新加积分操作: addPatientIntegral = {}",addPatientIntegral);
        // 增加500积分
        creditsShopMapper.insertSelective(addPatientIntegral);
      }
    }
  }

  /**
   * 查找有效（未增加推荐积分）的推荐人
   *
   * @param patientId 患者id
   * @param type 积分类型
   * @return 推荐人id
   */
  private Integer findPatientOriginId(Integer patientId, String type) {
    BasePatientOriginLog basePatientOrigin = new BasePatientOriginLog();
    basePatientOrigin.setPatientId(patientId);
    basePatientOrigin.setOriginType(2);
    basePatientOrigin.setInservice(true);
    BasePatientOriginLog basePatientOriginLog =
            basePatientOriginLogMapper.selectOne(basePatientOrigin);
    log.info("患者基本信息：basePatientOriginLog = {}",basePatientOriginLog);
    CreditsShop query = new CreditsShop();
    Integer originId = basePatientOriginLog.getOriginId();
    query.setPatientId(originId);
    query.setType(type);
    query.setInservice(true);
    query.setCrtId(patientId);
    List<CreditsShop> list = creditsShopMapper.select(query);
    if (StringHelper.isNotEmpty(list)) {
      // 已记录下 该患者patientId 的推荐积分
      return null;
    }
    return originId;
  }

  /**
   * 是否是患者的首个账单
   *
   * @param patientId
   * @return
   */
  private Boolean isPatientFirstBill(Integer patientId) {
    Example example = new Example(BaseBill.class);
    Example.Criteria c = example.createCriteria();
    c.andEqualTo("patientId", patientId);
    c.andIsNotNull("billDate");
    List<BaseBill> bills = mapper.selectByExample(example);
    if (StringHelper.isNotEmpty(bills) && bills.size()==1) {
      return true;
    }
    return false;
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
        record2baseReport(billRecord, baseBill);
      }
    }
  }

  /**
   * 账单实体转换
   *
   * @param billRecord
   * @param baseBill
   */
  private void record2baseReport(BillRecord billRecord, BaseBill baseBill) {
    BigDecimal debtAmount = billRecord.getDebtAmount();
    baseBill.setBillStatus(
            debtAmount.compareTo(BigDecimal.valueOf(0)) > 0 ? (byte) 0 : (byte) 1);
    baseBill.setBillId(billRecord.getOrderRecordId());
    baseBill.setOrgId(billRecord.getOrgId());
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
    baseBillDetail.setConsulterId(detail.getConsulterId());
    baseBillDetail.setItemId(detail.getBillingItemId());
    baseBillDetail.setItemType(detail.getType());
    baseBillDetail.setSourceType(detail.getSourceType());
    baseBillDetail.setQuantity(detail.getQuantity());
    baseBillDetail.setPrice(detail.getPrice());
    baseBillDetail.setDiscountAmount(new BigDecimal("0"));
    baseBillDetail.setCouponWorkload(new BigDecimal("0"));
    baseBillDetail.setReceivedAmount(new BigDecimal("0"));
    baseBillDetail.setRemark(detail.getRemarks());
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
      baseBillDetail.setConsulterId(detail.getConsulterId());
      baseBillDetail.setItemId(detail.getBillingItemId());
      baseBillDetail.setItemType(detail.getType());
      baseBillDetail.setSourceType(detail.getSourceType());
      baseBillDetail.setQuantity(detail.getQuantity());
      baseBillDetail.setPrice(detail.getPrice());
      baseBillDetail.setDiscountAmount(new BigDecimal("0"));
      baseBillDetail.setCouponWorkload(new BigDecimal("0"));
      baseBillDetail.setReceivedAmount(new BigDecimal("0"));
      baseBillDetail.setRemark(detail.getRemarks());
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

  public void updateBaseBill(BaseBill baseBill) {
    mapper.updateByPrimaryKeySelective(baseBill);
  }
}
