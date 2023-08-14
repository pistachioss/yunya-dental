package com.yunya.modules.treatment.biz;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.vo.DeductionItemBenefitVo;
import com.yunya.feign.discount.domain.vo.ItemUseBenefitVo;
import com.yunya.feign.discount.domain.vo.PatientItemBenefitVo;
import com.yunya.feign.discount.domain.vo.PatientOrderBenefitVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.treatment.domain.model.*;
import com.yunya.feign.treatment.domain.query.OrderPrivilegeQuery;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.PatientDepositAccountTypeEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.system.AccountItem;
import com.yunya.models.treatment.*;
import com.yunya.modules.treatment.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseTreatmentProcess;
import static com.yunya.framework.common.constant.BusinessConstants.ACCOUNT_ITEM_OF_PREPARE;
import static com.yunya.framework.common.constant.BusinessConstants.COMPANY_ORGID;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.*;
import static java.util.stream.Collectors.toMap;

/**
 * 简介: 就诊收费业务层
 *
 * @author: chow
 * @date: 2023/7/03 10:53
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class TreatTollBiz {

  /** 消息中间件 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
  /** 缓存 */
  @Autowired private RedisUtils redisUtils;
  /** 系统关联服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 优惠服务调用 */
  @Autowired private RemoteDiscountFeign discountFeign;
  /** 开单记录 */
  @Autowired private OrderRecordBiz orderRecordBiz;
  /** 开单明细 */
  @Autowired private OrderDetailBiz orderDetailBiz;
  /** 订单支付记录 */
  @Autowired private OrderDetailPayRecordBiz orderDetailPayRecordBiz;
  /** 账单记录 */
  @Autowired private BillRecordBiz billRecordBiz;
  /** 账单支付记录 */
  @Autowired private BillPayRecordMapper billPayRecordMapper;
  /** 账单支付明细记录 */
  @Autowired private BillPayDetailRecordMapper billPayDetailRecordMapper;
  /** 就诊记录 */
  @Autowired private TreatmentRecordMapper treatmentRecordMapper;

  @Autowired private MinorChargeProcessBiz minorChargeProcessBiz;
  @Autowired private BillPayShareDetailMapper billPayShareDetailMapper;

  @Autowired private BillPayRecordLogMapper billPayRecordLogMapper;

  /**
   * 根据优惠信息匹配订单优惠
   *
   * @param query 优惠条件
   * @return TreatOrderRecordVO
   */
  public TreatOrderRecordVO matchOrderTailPrivilege(OrderPrivilegeQuery query) {
    Integer orderRecordId = query.getOrderRecordId();
    List<OrderDetailChargeVO> detailList = orderDetailBiz.getChargeOrderDetailList(orderRecordId);
    if (StringHelper.isEmpty(detailList)) {
      throw new ClientServiceException("适用优惠失败，未查询到当前就诊开单数据！", PARAMETERS_IS_ILLEGAL);
    }
    Byte discountType = query.getDiscountType();
    GeneralDiscountModel generalDiscountModel = query.getGeneralDiscountModel();
    // 校验优惠参数
    checkPrivilegeParam(discountType, generalDiscountModel);
    TreatOrderRecordVO result = new TreatOrderRecordVO();
    switch (discountType) {
      case 1:
        // 匹配卡券优惠
        PatientOrderBenefitVo resultData = findGeneralPrivilege(orderRecordId, null, generalDiscountModel);
        orderDetailMatchDiscount(detailList, resultData, result);
        break;
      default:
        break;
    }
    return result;
  }

  /**
   * 订单明细匹配优惠信息列表
   *
   * @param detailList 订单详情列表
   * @param privilege  优惠项目列表
   * @param result 总优惠
   */
  public void orderDetailMatchDiscount(
          List<OrderDetailChargeVO> detailList,
          PatientOrderBenefitVo privilege, TreatOrderRecordVO result) {
    Map<Integer, OrderDetailChargeVO> detailMap = detailList.stream().collect(toMap(OrderDetailChargeVO::getOrderDetailId, Function.identity(),
            (u,v)->{ throw new IllegalStateException(String.format("Duplicate key %s", u));}, LinkedHashMap::new));
    List<OrderDetailChargeVO> swipeItemList = Lists.newArrayList();
    List<PatientItemBenefitVo> itemList = privilege.getItemList();
    List<DeductionItemBenefitVo> deductions = privilege.getDeductionList();
    BigDecimal benefitTotalAmount = BigDecimal.ZERO;
    if (StringHelper.isNotEmpty(deductions)) {
      for (DeductionItemBenefitVo deduct : deductions) {
        Integer orderDetailId = deduct.getOrderDetailId();
        OrderDetailChargeVO item = detailMap.get(orderDetailId);
        if (StringHelper.isNotNull(item)) {
          int swipeQuantity = deduct.getQuantity();
          int unSwipeQuantity = item.getQuantity() - swipeQuantity;
          OrderDetailChargeVO deductItem = null;
          if (unSwipeQuantity <= 0) {
            // 项目已全部划扣
            deductItem = detailMap.remove(orderDetailId);
          } else {
            // 设置剩余未划扣的项目数量
            item.setQuantity(unSwipeQuantity);
            deductItem = new OrderDetailChargeVO();
            BeanUtil.copyProperties(item, deductItem);
          }
          deductItem.setQuantity(swipeQuantity);
          deductItem.setReceivableAmount(item.getPrice().multiply(BigDecimal.valueOf(swipeQuantity)));
          deductItem.setDiscountRate(BigDecimal.ZERO);
          deductItem.setActualAmount(BigDecimal.ZERO);
          List<PrivilegeCouponInfoVO> privilegeInfo = getPrivilegeInfo(deduct.getItemBenefitList(), item.getPrice().multiply(BigDecimal.valueOf(swipeQuantity)));
          deductItem.setDiscountAppliesCoupons(privilegeInfo);
          deductItem.setCouponWorkload(deduct.getSupplyWorkload());
          benefitTotalAmount = benefitTotalAmount.add(deductItem.getReceivableAmount());
          swipeItemList.add(deductItem);
        }
      }
    }
    result.setSwipeItemList(swipeItemList);
    if (StringHelper.isNotEmpty(detailMap) && StringHelper.isNotEmpty(itemList)) {
      for (Map.Entry<Integer, OrderDetailChargeVO> entry : detailMap.entrySet()) {
        OrderDetailChargeVO vo = entry.getValue();
        BigDecimal receivableAmount = vo.getPrice().multiply(BigDecimal.valueOf(vo.getQuantity()));
        BigDecimal actualAmount = receivableAmount;
        for (PatientItemBenefitVo benefitVo : itemList) {
          if (entry.getKey().equals(benefitVo.getOrderDetailId())) {
            vo.setCouponWorkload(benefitVo.getSupplyWorkload());
            actualAmount = actualAmount.subtract(benefitVo.getItemBenefitAmount());
            // 设置折扣率
            vo.setDiscountRate(actualAmount.divide(receivableAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            // 设置订单明细卡券匹配信息
            if (StringHelper.gt(receivableAmount, actualAmount)) {
              List<PrivilegeCouponInfoVO> privilegeInfo = getPrivilegeInfo(benefitVo.getItemBenefitList(), null);
              vo.setDiscountAppliesCoupons(privilegeInfo);
            }
            benefitTotalAmount = benefitTotalAmount.add(receivableAmount.subtract(actualAmount));
          }
          vo.setReceivableAmount(receivableAmount);
          vo.setActualAmount(actualAmount);
        }
      }
    }
    result.setItemList(new ArrayList<>(detailMap.values()));
    result.setBenefitTotalAmount(benefitTotalAmount);
  }

  /**
   * 获取订单卡券优惠信息
   *
   * @param benefitList 匹配卡券列表
   * @param originAmount 原价
   */
  private List<PrivilegeCouponInfoVO> getPrivilegeInfo(
      List<ItemUseBenefitVo> benefitList, BigDecimal originAmount) {
    List<PrivilegeCouponInfoVO> discountAppliesCoupon = Lists.newArrayList();
    if (StringHelper.isNotEmpty(benefitList)) {
      benefitList.forEach(
        benefitVo -> {
          Integer benefitType = benefitVo.getBenefitType();
          PrivilegeCouponInfoVO couponInfo = new PrivilegeCouponInfoVO();
          couponInfo.setBenefitId(benefitVo.getBenefitId());
          // 99-会员卡，-1-授权折扣
          BigDecimal benefitAmount = benefitVo.getBenefitAmount();
          couponInfo.setCouponType(0 == benefitType ? 99 : 2==benefitType?-1:benefitVo.getCouponType());
          couponInfo.setBenefitName(benefitVo.getBenefitName());
          couponInfo.setCardNumber(benefitVo.getCardNumber());
          if (StringHelper.isNotNull(originAmount)) {
            couponInfo.setPackageTotalPrice(originAmount.subtract(benefitAmount));
            benefitAmount = originAmount;
          }
          couponInfo.setBenefitAmount(benefitAmount);
          discountAppliesCoupon.add(couponInfo);
        });
    }
    return discountAppliesCoupon;
  }

  private void updateTreatmentStatus(TreatmentRecord treatmentRecord) {
    int i = treatmentRecordMapper.updateByPrimaryKeySelective(treatmentRecord);
    if (i > 0) {
      Integer appointmentId = treatmentRecord.getAppointmentId();
      if (null != appointmentId) {
        rabbitMqServiceFeign.sendMessage(appointmentId, 0, 1, BaseTreatmentProcess);
      } else {
        rabbitMqServiceFeign.sendMessage(
            treatmentRecord.getRegisteredId(), 1, 1, BaseTreatmentProcess);
      }
    }
  }

  /**
   * 挂账
   *
   * @return
   */
  public TollConfirmVO chargeOnCredit(TreatTollModel model) {
    OrderRecord orderRecord = checkChargeParam(model);
    Integer orderRecordId = orderRecord.getId();
    Integer patientId = orderRecord.getPatientId();
    Byte discountType = model.getDiscountType();
    GeneralDiscountModel generalDiscount = model.getGeneralDiscountModel();
    BigDecimal privilegeAmount = calculatePrivilegeAmount(discountType, orderRecordId, patientId, generalDiscount);
    // 计算入账总额、应收总额
    BigDecimal actualReceivableAmount = orderRecord.getTotalAmount().subtract(privilegeAmount);
    if (StringHelper.leZero(actualReceivableAmount)) {
      throw new ClientServiceException("收费失败，应收金额合计为0，不能挂账！", OPERATION_NOT_ALLOW);
    }
    model.setPaymentModels(null);
    model.setPrepaymentAccountModels(null);
    model.setMemberAccountModels(null);
    model.setOutstandingAmount(actualReceivableAmount);
    return confirmCharge(model, (byte) 1);
  }

  /**
   * 确认收费
   *
   * @param model 收费参数
   * @param type 1-挂账，2-确认收费
   */
  @Transactional
  public TollConfirmVO confirmCharge(TreatTollModel model, byte type) {
    OrderRecord orderRecord = checkChargeParam(model);
    Integer orderRecordId = orderRecord.getId();
    Integer patientId = orderRecord.getPatientId();
    Byte discountType = model.getDiscountType();
    GeneralDiscountModel generalDiscount = model.getGeneralDiscountModel();

    BigDecimal privilegeAmount = calculatePrivilegeAmount(discountType, orderRecordId, patientId, generalDiscount);
    // 计算入账总额、应收总额
    BigDecimal[] totalAmount = calculateTotalCharge(model);
    BigDecimal totalCharge = totalAmount[0];
    BigDecimal actualReceivableAmount = orderRecord.getTotalAmount().subtract(privilegeAmount);
    // 比较实际应收与总入账金额
    BigDecimal outstandingAmount = model.getOutstandingAmount();
    if (discountType != 0) {
      checkTotalChargeAndDebtAmount(totalCharge, actualReceivableAmount, outstandingAmount);
    }
    // 生成账单记录
    BillRecord billRecord =
            generateBillRecord(orderRecord, model, totalCharge, actualReceivableAmount, privilegeAmount);
    // 保存订单项目收费明细
    saveOrderDetailPayRecord(
            totalCharge,
            billRecord,
            model);
    // 生成收费记录及其入账方式明细
    BillPayRecord billPayRecord = generalBillPayRecordWithDetail(billRecord, totalCharge, actualReceivableAmount, model);
    // 更新订单状态
    orderRecordBiz.updateOrderStatus(orderRecord.getId(), BusinessConstants.ORDER_FINISH_STATUS);
    // 更新就诊状态
    updateTreatmentRecordStatus(orderRecord.getTreatmentRecordId());
    // 异步处理收费次要流程
    asyncProcessCharge(billPayRecord, model, totalCharge, totalAmount[1], true);
    recordChargeLog(billPayRecord, model, totalCharge, totalAmount[1], type);
    redisUtils.delete(LOCK_ORDER_PROCESSING_CHARGE + orderRecordId);
    return TollConfirmVO.builder()
            .billNumber(billRecord.getBillNumber())
            .billPayRecordId(billPayRecord.getId()).build();
  }

  /**
   * 记录收费日志-用于异步任务（次要收费流程）失败时对其进行数据修复补充
   *
   * @param billPayRecord
   * @param model
   * @param totalCharge
   * @param totalPrincipal
   * @param type
   */
  private void recordChargeLog(BillPayRecord billPayRecord, TreatTollModel model, BigDecimal totalCharge, BigDecimal totalPrincipal, byte type) {
    BillPayRecordLog logEntity = new BillPayRecordLog();
    logEntity.setBillPayRecordId(billPayRecord.getId());
    logEntity.setOrderRecordId(billPayRecord.getOrderRecordId());
    logEntity.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    logEntity.setTotalCharge(totalCharge);
    logEntity.setTotalPrincipal(totalPrincipal);
    logEntity.setType(type);
    logEntity.setParam(JSONObject.toJSONString(model));
    logEntity.setStatus((byte) 1);
    logEntity.setCrtTime(BaseContextHandler.getCurTime());
    logEntity.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    logEntity.setCrtName(BaseContextHandler.getName());
    billPayRecordLogMapper.insertSelective(logEntity);
  }

  private void asyncProcessCharge(BillPayRecord billPayRecord, TreatTollModel model, BigDecimal totalCharge, BigDecimal totalPrincipal, boolean isMqTreatment) {
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      /**
       * 事务提交后回调该方法
       */
      @Override
      public void afterCommit() {
        minorChargeProcessBiz.asyncProcessCharge(billPayRecord, model, totalCharge, totalPrincipal, isMqTreatment);
        minorChargeProcessBiz.asyncPushWxExpendMsg(billPayRecord);
      }
    });
  }

  /**
   * 生成账单收费记录
   *
   * @param billRecord
   * @param totalCharge
   * @param actualReceivableAmount
   * @param model
   * @return
   */
  @Transactional
  public BillPayRecord generalBillPayRecordWithDetail(BillRecord billRecord, BigDecimal totalCharge, BigDecimal actualReceivableAmount, TreatTollModel model) {
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer billRecordId = billRecord.getId();
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    Date dateTime = billRecord.getCrtTime();
    BillPayRecord billPayRecord = new BillPayRecord();
    // 如果当前组织是公司，收费门诊则是开单门诊
    billPayRecord.setOrgId(COMPANY_ORGID.equals(orgId) ? billRecord.getOrgId() : orgId);
    billPayRecord.setPatientId(billRecord.getPatientId());
    billPayRecord.setTreatmentRecordId(billRecord.getTreatmentRecordId());
    billPayRecord.setOrderRecordId(billRecord.getOrderRecordId());
    billPayRecord.setBillRecordId(billRecordId);
    billPayRecord.setReceivedAmount(totalCharge);
    billPayRecord.setStillOweAmount(actualReceivableAmount.subtract(totalCharge));
    billPayRecord.setCrtId(userId);
    // 首次收费时间与账单时间保持一致
    billPayRecord.setCrtTime(dateTime);
    billPayRecord.setCrtName(name);
    billPayRecord.setUpdId(userId);
    billPayRecord.setUpdName(name);
    billPayRecord.setUpdTime(dateTime);
    billPayRecordMapper.insertSelective(billPayRecord);

    // 保存收费入账方式明细
    saveBillPayDetailRecord(billPayRecord,
            model.getPrepaymentAccountModels(),
            model.getMemberAccountModels(),
            model.getPaymentModels());
    return billPayRecord;
  }

  /**
   * 检查收费参数
   *
   * @param model
   * @return
   */
  private OrderRecord checkChargeParam(TreatTollModel model) {
    checkPrepayments(model.getPrepaymentAccountModels());
    return checkParam(model.getOrderRecordId(), model.getDiscountType(), model.getGeneralDiscountModel(), model.getInvoiceModel());
  }

  /**
   * 检查预付款账户
   *
   * @param prepayments
   */
  private void checkPrepayments(Set<PrepaymentAccountModel> prepayments) {
    if (StringHelper.isNotEmpty(prepayments)) {
      prepayments.forEach(payment->{
      if (!PatientDepositAccountTypeEnum.isRelTypeId(payment.getAccountItemId())) {
        throw new ClientServiceException("无效的预付款账户类型", PARAMETERS_IS_ILLEGAL);
      }
      });
    }
  }

  /**
   * 更新就诊记录状态
   *
   * @param treatmentRecordId 就诊记录id
   */
  private void updateTreatmentRecordStatus(Integer treatmentRecordId) {
    TreatmentRecord treatmentRecord = new TreatmentRecord();
    treatmentRecord.setId(treatmentRecordId);
    treatmentRecord.setStatus(BusinessConstants.TREATMENT_PROCESS_FINISH_STATUS);
    treatmentRecord.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
    treatmentRecord.setUpdName(BaseContextHandler.getName());
    treatmentRecord.setUpdTime(DateUtil.now());
    treatmentRecordMapper.updateByPrimaryKeySelective(treatmentRecord);
  }

  /**
   * 根据优惠类型保存订单明细收费记录
   *
   * @param totalCharge 入账总额
   * @param billRecord 优惠类型
   * @param model 订单记录ID
   */
  private void saveOrderDetailPayRecord(
      BigDecimal totalCharge,
      BillRecord billRecord,
      TreatTollModel model) {
    // 构建明细收费列表
//    Map<Integer, OrderDetailPayBenefitVO> discountMap = findBillDiscountCoupons(model, billRecord);
    Map<Integer, OrderDetailPayBenefitVO> discountMap = cacheOrderBenefitTotalAmount(billRecord.getOrderRecordId()).getDiscountMap();
    List<OrderDetailPayRecord> orderDetailPayRecords = Lists.newArrayList();
    OrderDetail orderDetail = new OrderDetail();
    Integer orderRecordId = billRecord.getOrderRecordId();
    orderDetail.setOrderRecordId(orderRecordId);
    List<OrderDetail> orderDetails = orderDetailBiz.selectList(orderDetail);
    if (StringHelper.isNotEmpty(orderDetails)) {
      for (OrderDetail detail : orderDetails) {
        Integer orderDetailId = detail.getId();
        OrderDetailPayRecord detailPayRecord = buildOrderDetailPayBaseRecord(detail, billRecord);
        BigDecimal receivableAmount = detail.getReceivableAmount();
        BigDecimal privilegeAmount = BigDecimal.ZERO;
        BigDecimal actualAmount = receivableAmount;
        BigDecimal couponWorkload = BigDecimal.ZERO;
        BigDecimal swipeWorkload = BigDecimal.ZERO;
        BigDecimal swipeCouponWorkload = BigDecimal.ZERO;
        OrderDetailPayBenefitVO benefitVO = discountMap.get(orderDetailId);
        if (StringHelper.isNotNull(benefitVO)) {
          privilegeAmount = benefitVO.getPrivilegeAmount();
          if (privilegeAmount.compareTo(actualAmount) > 0) {
            privilegeAmount = actualAmount;
          }
          actualAmount = receivableAmount.subtract(privilegeAmount);
          if (BigDecimal.ZERO.compareTo(actualAmount) > 0) {
            actualAmount = BigDecimal.ZERO;
          }
          // 获取补入工作量
          couponWorkload = benefitVO.getCouponWorkload();
          // 划扣项目的原价 - 划扣套餐价 = 划扣工作量
          BigDecimal swipeReceivableAmount = detail.getPrice().multiply(BigDecimal.valueOf(benefitVO.getSwipeQuantity()));
          swipeWorkload = swipeReceivableAmount.subtract(benefitVO.getPackageTotalAmount());
          swipeCouponWorkload = benefitVO.getSwipeCouponWorkload();
        }

        detailPayRecord.setReceivableAmount(receivableAmount);
        detailPayRecord.setSwipeCouponWorkload(swipeCouponWorkload);
        detailPayRecord.setSwipeWorkload(swipeWorkload);
        detailPayRecord.setPrivilegeAmount(privilegeAmount);
        detailPayRecord.setActualReceivable(actualAmount);
        detailPayRecord.setCouponWorkload(couponWorkload);
        // 设置已收
        totalCharge = totalItemCharge(totalCharge, detailPayRecord);
        orderDetailPayRecords.add(detailPayRecord);
      }
    }
    if (StringHelper.isNotEmpty(orderDetailPayRecords)) {
      orderDetailPayRecordBiz.batchInsert(orderDetailPayRecords);
    }
  }

  /**
   * 构建订单明细收费基础信息
   *
   * @param detail
   * @param billRecord
   * @return
   */
  private OrderDetailPayRecord buildOrderDetailPayBaseRecord(OrderDetail detail, BillRecord billRecord) {
    Date now = DateUtil.now();
    Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    OrderDetailPayRecord detailPayRecord = new OrderDetailPayRecord();
    detailPayRecord.setPatientId(billRecord.getPatientId());
    detailPayRecord.setTreatmentRecordId(billRecord.getTreatmentRecordId());
    detailPayRecord.setOrderRecordId(billRecord.getOrderRecordId());
    detailPayRecord.setBillRecordId(billRecord.getId());
    detailPayRecord.setOrderDetailId(detail.getId());
    detailPayRecord.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
    detailPayRecord.setCrtId(userId);
    detailPayRecord.setCrtName(name);
    detailPayRecord.setCrtTime(now);
    detailPayRecord.setUpdId(userId);
    detailPayRecord.setUpdName(name);
    detailPayRecord.setUpdTime(now);
    return detailPayRecord;
  }

  /**
   * 获取账单收费时的优惠明细
   *
   * @param model
   * @param billRecord
   * @return
   */
  private Map<Integer, OrderDetailPayBenefitVO> findBillDiscountCoupons(TreatTollModel model, BillRecord billRecord) {
    if (model.getDiscountType() != 0) {
      PatientOrderBenefitVo privilege = findGeneralPrivilege(billRecord.getOrderRecordId(),
              billRecord.getPatientId(), model.getGeneralDiscountModel());
      List<PatientItemBenefitVo> itemList = privilege.getItemList();
      List<DeductionItemBenefitVo> deductionList = privilege.getDeductionList();
      Map<Integer, OrderDetailPayBenefitVO> result = Maps.newHashMap();
      if (StringHelper.isNotEmpty(itemList)) {
        itemList.forEach(item->{
          Integer orderDetailId = item.getOrderDetailId();
          OrderDetailPayBenefitVO vo = result.computeIfAbsent(orderDetailId, v -> new OrderDetailPayBenefitVO());
          vo.setOrderDetailId(orderDetailId);
          vo.setPrivilegeAmount(item.getItemBenefitAmount());
          vo.setCouponWorkload(item.getSupplyWorkload());
        });
      }
      if (StringHelper.isNotEmpty(deductionList)) {
        deductionList.forEach(item->{
          Integer orderDetailId = item.getOrderDetailId();
          OrderDetailPayBenefitVO vo = result.computeIfAbsent(orderDetailId, v -> new OrderDetailPayBenefitVO());
          vo.setOrderDetailId(orderDetailId);
          BigDecimal itemBenefitAmount = item.getItemBenefitAmount();
          BigDecimal privilegeAmount = StringHelper.defaultBigDecimal(vo.getPrivilegeAmount()).add(itemBenefitAmount);
          vo.setPrivilegeAmount(privilegeAmount);
          // 划扣套餐价
          vo.setPackageTotalAmount(itemBenefitAmount);
          vo.setSwipeQuantity(item.getQuantity());
          vo.setSwipeCouponWorkload(item.getSupplyWorkload());
        });
      }
      return result;
    }
    return new HashMap<>();
  }

  /**
   * 统计项目已收（含免单）
   *
   * @param totalCharge 总收入
   * @param detailPayRecord
   */
  private BigDecimal totalItemCharge(BigDecimal totalCharge, OrderDetailPayRecord detailPayRecord) {
    BigDecimal actualAmount = detailPayRecord.getActualReceivable();
    BigDecimal receivedAmount = StringHelper.defaultBigDecimal(detailPayRecord.getReceivedAmount());
    if (StringHelper.gt(actualAmount, receivedAmount)) {
      // 项目缺口
      BigDecimal gap = actualAmount.subtract(receivedAmount);
      // 账单总实收 >= 项目应收 ? 项目应收 : 账单本次实收
      if (StringHelper.ge(totalCharge, gap)) {
        detailPayRecord.setReceivedAmount(actualAmount);
        totalCharge = totalCharge.subtract(gap);
      } else {
        detailPayRecord.setReceivedAmount(receivedAmount.add(totalCharge));
        totalCharge = BigDecimal.ZERO;
      }
    }
    return totalCharge;
  }

  public TreatOrderBenefitVO cacheOrderBenefitTotalAmount(Integer orderRecordId) {
    return cacheOrderBenefitTotalAmount(orderRecordId, null);
  }

  /**
   * 缓存or获取订单对应的使用优惠总额
   *
   * @param orderRecordId 订单id
   * @param order 非空为存储，否则为获取
   * @return
   */
  public TreatOrderBenefitVO cacheOrderBenefitTotalAmount(Integer orderRecordId, TreatOrderRecordVO order) {
    String key = buildLockCacheKey(BILL_BENEFIT_MATCH, orderRecordId);
    if (StringHelper.isNotNull(order)) {
      // 暂存10分钟
      TreatOrderBenefitVO result = orderConvertBenefit(order);
//    redisUtils.set(key, result, 600);
      redisUtils.set(key, result);
      return result;
    } else {
      // 获取缓存中的订单优惠总额
      return redisUtils.get(key, TreatOrderBenefitVO.class);
    }
  }

  private TreatOrderBenefitVO orderConvertBenefit(TreatOrderRecordVO order) {
    TreatOrderBenefitVO result = new TreatOrderBenefitVO();
    List<OrderDetailChargeVO> itemList = order.getItemList();
    List<OrderDetailChargeVO> deductionList = order.getSwipeItemList();
    Map<Integer, OrderDetailPayBenefitVO> discountMap = Maps.newHashMap();
    if (StringHelper.isNotEmpty(itemList)) {
      itemList.forEach(item->{
        Integer orderDetailId = item.getOrderDetailId();
        OrderDetailPayBenefitVO vo = discountMap.computeIfAbsent(orderDetailId, v -> new OrderDetailPayBenefitVO());
        vo.setOrderDetailId(orderDetailId);
        vo.setPrivilegeAmount(vo.getPrivilegeAmount().add(item.getPrivilegeAmount()));
        vo.setCouponWorkload(vo.getCouponWorkload().add(item.getCouponWorkload()));
      });
    }
    if (StringHelper.isNotEmpty(deductionList)) {
      deductionList.forEach(item->{
        Integer orderDetailId = item.getOrderDetailId();
        OrderDetailPayBenefitVO vo = discountMap.computeIfAbsent(orderDetailId, v -> new OrderDetailPayBenefitVO());
        vo.setOrderDetailId(orderDetailId);
        BigDecimal itemBenefitAmount = item.getPrivilegeAmount();
        BigDecimal privilegeAmount = StringHelper.defaultBigDecimal(vo.getPrivilegeAmount()).add(itemBenefitAmount);
        vo.setPrivilegeAmount(vo.getPrivilegeAmount().add(privilegeAmount));
        // 划扣套餐价
        vo.setPackageTotalAmount(vo.getPackageTotalAmount().add(itemBenefitAmount));
        vo.setSwipeQuantity(vo.getSwipeQuantity() + item.getQuantity());
        vo.setSwipeCouponWorkload(vo.getSwipeCouponWorkload().add(item.getCouponWorkload()));
      });
    }
    result.setBenefitTotalAmount(order.getBenefitTotalAmount());
    result.setDiscountMap(discountMap);
    return result;
  }

  /**
   * 计算优惠总额
   *
   * @param discountType 折扣类型
   * @param orderRecordId 订单ID
   * @param patientId 患者ID
   * @param generalDiscountModel 卡券优惠
   * @return
   */
  private BigDecimal calculatePrivilegeAmount(
      Byte discountType,
      Integer orderRecordId,
      Integer patientId,
      GeneralDiscountModel generalDiscountModel) {
    BigDecimal privilegeAmount = BigDecimal.ZERO;
    switch (discountType) {
      case 1:
//        privilegeAmount = calculateGeneralPrivilegeAmount(orderRecordId, patientId, generalDiscountModel);
        privilegeAmount = cacheOrderBenefitTotalAmount(orderRecordId).getBenefitTotalAmount();
        if (StringHelper.isNull(privilegeAmount)) {
          throw new ClientServiceException("收费失败，优惠信息不存在，请核对优惠信息是否正确！", PARAMETERS_IS_ILLEGAL);
        }
        if (StringHelper.leZero(privilegeAmount)) {
          throw new ClientServiceException("收费失败，优惠金额小于0，请核对优惠信息是否正确！", PARAMETERS_IS_ILLEGAL);
        }
        break;
      default:
        break;
    }
    return privilegeAmount;
  }

  /**
   * 计算使用卡券优惠总额
   *
   * @param orderRecordId 订单记录ID
   * @param generalDiscountModel 卡券列表
   * @return
   */
  private BigDecimal calculateGeneralPrivilegeAmount(
      Integer orderRecordId, Integer patientId, GeneralDiscountModel generalDiscountModel) {
    BigDecimal privilegeAmount = BigDecimal.ZERO;
    PatientOrderBenefitVo benefitVo = findGeneralPrivilege(orderRecordId, patientId, generalDiscountModel);
    if (StringHelper.isNotNull(benefitVo)) {
      privilegeAmount = benefitVo.getBenefitTotalAmount();
      if (BigDecimal.ZERO.compareTo(privilegeAmount) > 0) {
        throw new ClientServiceException("收费失败，优惠金额小于0，请核对优惠信息是否正确！", PARAMETERS_IS_ILLEGAL);
      }
    }
    return privilegeAmount;
  }

  /**
   * 查询一般优惠
   *
   * @param orderRecordId
   * @param patientId
   * @param generalDiscountModel
   * @return
   */
  private PatientOrderBenefitVo findGeneralPrivilege(Integer orderRecordId, Integer patientId, GeneralDiscountModel generalDiscountModel) {
    PatientChooseBenefitForm form = new PatientChooseBenefitForm();
    if (StringHelper.isNull(patientId)) {
      OrderRecord order = orderRecordBiz.selectById(orderRecordId);
      if (StringHelper.isNull(order)) {
        throw new ClientServiceException("订单信息不存在", DATA_NOT_EXIST);
      }
      patientId = order.getPatientId();
    }
    form.setPatientId(patientId);
    form.setOrderId(orderRecordId);
    form.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
    form.setMemberCardId(generalDiscountModel.getMemberTypeId());
    form.setDiscountId(generalDiscountModel.getDiscountCouponId());
    List<Integer> exchangeIds = Lists.newArrayList();
    List<Integer> voucherIds = Lists.newArrayList();
    List<Integer> packageIds = Lists.newArrayList();
    List<Integer> deductionIds = Lists.newArrayList();
    List<CouponDiscountInfoModel> models = generalDiscountModel.getCouponDiscountInfoModels();
    // 分类卡券列表
    minorChargeProcessBiz.setCouponListValue(models, voucherIds, exchangeIds, packageIds, deductionIds);
    form.setExchangeIds(exchangeIds);
    form.setPackageIds(packageIds);
    form.setVoucherIds(voucherIds);
    form.setDeductionIds(deductionIds);
    ResponseResult<PatientOrderBenefitVo> responseResult = discountFeign.choiceBenefit(form);
    PatientOrderBenefitVo resultData = responseResult.getData();
    // 卡券优惠为空
    if (StringHelper.isNull(resultData)) {
      throw new ClientServiceException(responseResult.getMsg(), responseResult.getStatus());
    }
    return resultData;
  }

  /**
   * 收费参数校验
   *
   * @param orderRecordId 订单记录ID
   * @param discountType 优惠类型
   * @param generalDiscountModel 卡券优惠
   * @param invoiceModel 发票
   * @return
   */
  private OrderRecord checkParam(
      Integer orderRecordId,
      Byte discountType,
      GeneralDiscountModel generalDiscountModel,
      InvoiceModel invoiceModel) {
    OrderRecord orderRecord = orderRecordBiz.selectById(orderRecordId);
    if (null == orderRecord) {
      throw new ClientServiceException("收费失败，当前未选择正确的就诊记录或传入参数有误！", PARAMETERS_IS_ILLEGAL);
    }
    Byte status = orderRecord.getStatus();
    // 检查收费订单状态
//    checkOrderRecordStatus(status);
    String resultRecordId = redisUtils.get(LOCK_ORDER_PROCESSING_UNLOCK + orderRecordId);
    if (StringHelper.isNotBlank(resultRecordId)) {
      throw new ClientServiceException("收费失败，当前账单已解锁！请联系开单人员提交账单！", PARAMETERS_IS_ILLEGAL);
    }
    // 校验优惠参数
    checkPrivilegeParam(discountType, generalDiscountModel);
    if (invoiceModel.getInvoice()) {
      if (StringHelper.isBlank(invoiceModel.getInvoiceNumber())) {
        throw new ClientServiceException("收费失败，未填写发票编号！", PARAMETERS_IS_ILLEGAL);
      }
    }
    return orderRecord;
  }

  /**
   * 校验订单状态
   *
   * @param status 订单状态
   */
  private void checkOrderRecordStatus(Byte status) {
    switch (status) {
      case 0:
        throw new ClientServiceException("收费失败，当前账单已解锁！", PARAMETERS_IS_ILLEGAL);
      case 2:
        throw new ClientServiceException("收费失败，当前账单已收费！", PARAMETERS_IS_ILLEGAL);
      case 3:
        throw new ClientServiceException("收费失败，当前账单正在收费！", PARAMETERS_IS_ILLEGAL);
      default:
        break;
    }
  }

  /**
   * 校验优惠参数
   *
   * @param discountType 优惠类型
   * @param generalDiscountModel 卡券优惠
   */
  private void checkPrivilegeParam(
      Byte discountType,
      GeneralDiscountModel generalDiscountModel) {
    switch (discountType) {
      case 1:
        checkCardDiscount(generalDiscountModel);
        break;
      default:
        break;
    }
  }

  private void checkCardDiscount(GeneralDiscountModel generalDiscountModel) {
    Integer memberTypeId = generalDiscountModel.getMemberTypeId();
    Integer discountCouponId = generalDiscountModel.getDiscountCouponId();
    List<CouponDiscountInfoModel> discountInfoModels =
            generalDiscountModel.getCouponDiscountInfoModels();
    if (null == memberTypeId
            && null == discountCouponId
            && StringHelper.isEmpty(discountInfoModels)) {
      throw new ClientServiceException("当前未选择任何卡券！", PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 计算入账总额
   *
   * @param tollModel 收费基础添加模型
   */
  private BigDecimal[] calculateTotalCharge(TreatTollModel tollModel) {
    Set<PrepaymentAccountModel> prepaymentAccounts = tollModel.getPrepaymentAccountModels();
    Set<MemberAccountModel> memberAccounts = tollModel.getMemberAccountModels();
    Set<PaymentModel> payments = tollModel.getPaymentModels();
    // 入账总额，本金总额
    BigDecimal[] totalAmount = {BigDecimal.ZERO, BigDecimal.ZERO};
    // 预付款入账总额
    if (StringHelper.isNotEmpty(prepaymentAccounts)) {
      for (PrepaymentAccountModel model : prepaymentAccounts) {
        totalAmount[0] = totalAmount[0].add(StringHelper.defaultBigDecimal(model.getAmount()));
        totalAmount[1] = totalAmount[1].add(StringHelper.defaultBigDecimal(model.getPrincipalAmount()));
      }
    }
    // 会员卡入账总额
    if (StringHelper.isNotEmpty(memberAccounts)) {
      for (MemberAccountModel model : memberAccounts) {
        totalAmount[0] = totalAmount[0].add(StringHelper.defaultBigDecimal(model.getAmount()));
        totalAmount[1] = totalAmount[1].add(StringHelper.defaultBigDecimal(model.getPrincipalAmount()));
      }
    }
    // 其他方式入账总额
    if (StringHelper.isNotEmpty(payments)) {
      for (PaymentModel model : payments) {
        totalAmount[0] = totalAmount[0].add(StringHelper.defaultBigDecimal(model.getAmount()));
        totalAmount[1] = totalAmount[1].add(StringHelper.defaultBigDecimal(model.getAmount()));
      }
    }
    return totalAmount;
  }

  /**
   * 保存账单入账明细记录
   *
   * @param billPayRecord 账单收费记录
   * @param prepaymentAccountModels 预付款账户列表
   * @param memberAccountModels 会员卡账户列表
   * @param paymentModels 其他入账方式列表
   */
  private void saveBillPayDetailRecord(
      BillPayRecord billPayRecord,
      Set<PrepaymentAccountModel> prepaymentAccountModels,
      Set<MemberAccountModel> memberAccountModels,
      Set<PaymentModel> paymentModels) {
    if (!CollectionUtils.isEmpty(prepaymentAccountModels)) {
      // 预付款
      prepaymentAccountModels.forEach(
          prepaymentAccountModel -> {
            if (prepaymentAccountModel.getAmount().compareTo(BigDecimal.ZERO) > 0) {
              BillPayDetailRecord billPayDetailRecord =
                  setBillPayRecordDetailValue(
                      billPayRecord,
                      prepaymentAccountModel.getAccountItemId(),
                      prepaymentAccountModel.getAmount(),
                      (byte) 0,
                      null);
              billPayDetailRecord.setRemark(prepaymentAccountModel.getPrepaymentNum());
              billPayDetailRecordMapper.insertSelective(billPayDetailRecord);
            }
          });
    }
    if (!CollectionUtils.isEmpty(memberAccountModels)) {
      // 会员卡
      memberAccountModels.forEach(
          memberAccountModel -> {
            if (memberAccountModel.getAmount().compareTo(BigDecimal.ZERO) > 0) {
              BillPayDetailRecord billPayDetailRecord =
                  setBillPayRecordDetailValue(
                      billPayRecord,
                      memberAccountModel.getAccountItemId(),
                      memberAccountModel.getAmount(),
                      (byte) 1,
                      null);
              billPayDetailRecord.setRemark(memberAccountModel.getMemberNum());
              billPayDetailRecordMapper.insertSelective(billPayDetailRecord);
            }
          });
    }
    if (!CollectionUtils.isEmpty(paymentModels)) {
      // 其他支付方式
      paymentModels.forEach(
          paymentModel -> {
            if (paymentModel.getAmount().compareTo(BigDecimal.ZERO) > 0) {
              BillPayDetailRecord billPayDetailRecord =
                  setBillPayRecordDetailValue(
                      billPayRecord,
                      paymentModel.getAccountItemId(),
                      paymentModel.getAmount(),
                      (byte) 2,
                      paymentModel.getRemarks());
              billPayDetailRecordMapper.insertSelective(billPayDetailRecord);
            }
          });
    }
  }

  /**
   * 设置账单支付明细记录字段属性
   *
   * @param billPayRecord 账单收费记录
   * @param accountItemId 支付方式ID
   * @param amount 支付金额
   * @param remarks 备注
   */
  private BillPayDetailRecord setBillPayRecordDetailValue(
      BillPayRecord billPayRecord,
      Integer accountItemId,
      BigDecimal amount,
      Byte type,
      String remarks) {
    BillPayDetailRecord billPayDetailRecord = new BillPayDetailRecord();
    BeanUtil.copyProperties(billPayRecord, billPayDetailRecord);
    billPayDetailRecord.setId(null);
    billPayDetailRecord.setBillPayRecordId(billPayRecord.getId());
    billPayDetailRecord.setAccountItemId(accountItemId);
    billPayDetailRecord.setAmount(amount);
    billPayDetailRecord.setType(type);
    billPayDetailRecord.setRemark(remarks);
    return billPayDetailRecord;
  }

  /**
   * 收欠费
   *
   * @param model 收费参数
   */
  @Transactional
  public TollConfirmVO collectDebt(TreatTollModel model) {
    checkPrepayments(model.getPrepaymentAccountModels());
    //    // TODO: bug3210 未收费走收欠费流程
    //    if(null == generalDiscount.getMemberTypeId() && null ==
    // generalDiscount.getDiscountCouponId() ) {
    //      if(null == generalDiscount.getCouponDiscountInfoModels() ||
    // generalDiscount.getCouponDiscountInfoModels().size() == 0) {
    //        generalDiscount = null;
    //      }
    //    }
    // 校验收欠费参数合法性
    BillRecord billRecord =
        checkCollectDebtParams(model);
    BigDecimal actualReceivableAmount = billRecord.getActualReceivableAmount();
    // 计算收欠费入账总额
    BigDecimal[] totalAmount = calculateTotalCharge(model);
    BigDecimal totalCharge = totalAmount[0];
    checkTotalChargeAndDebtAmount(totalCharge, actualReceivableAmount, model.getOutstandingAmount());
    billRecord.setReceivedAmount(billRecord.getReceivedAmount().add(totalCharge));
    billRecord.setDebtAmount(actualReceivableAmount.subtract(totalCharge));
    InvoiceModel invoiceModel = model.getInvoiceModel();
    if (StringHelper.isNotNull(invoiceModel)) {
      billRecord.setInvoice(invoiceModel.getInvoice());
      billRecord.setInvoiceNumber(invoiceModel.getInvoiceNumber());
    }
    billRecord.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
    billRecord.setUpdName(BaseContextHandler.getName());
    billRecord.setUpdTime(DateUtil.now());
    billRecordBiz.updateSelectiveById(billRecord);
    // 保存收费记录及其入账方式明细
    BillPayRecord billPayRecord = generalBillPayRecordWithDetail(billRecord, totalCharge, actualReceivableAmount, model);

    asyncProcessCharge(billPayRecord, model, totalCharge, totalAmount[1], false);
    recordChargeLog(billPayRecord, model, totalCharge, totalAmount[1], (byte) 3);
    return TollConfirmVO.builder()
            .billNumber(billRecord.getBillNumber())
            .billPayRecordId(billPayRecord.getId()).build();
  }

  /**
   * 构建账单记录
   *
   * @param orderRecord 订单记录
   * @param model 收费模型
   * @param totalCharge 入账总额
   * @param actualReceivableAmount 账单总应收
   * @param privilegeAmount 账单总优惠
   * @return BillRecord
   */
  public BillRecord generateBillRecord(OrderRecord orderRecord, TreatTollModel model, BigDecimal totalCharge, BigDecimal actualReceivableAmount, BigDecimal privilegeAmount) {
    log.info("开始生成就诊账单编号===>");
    Integer orgId = orderRecord.getOrgId();
    if (StringHelper.isNotNull(orgId)) {
      Date now = DateUtil.now();
      return redisUtils.lockedFunc("", k->{
         Byte discountType = model.getDiscountType();
         BillRecord billRecord = new BillRecord();
         billRecord.setOrgId(orgId);
         billRecord.setPatientId(orderRecord.getPatientId());
         billRecord.setTreatmentRecordId(orderRecord.getTreatmentRecordId());
         billRecord.setOrderRecordId(orderRecord.getId());
         billRecord.setPrivilegeType(discountType);
         if (0 != discountType) {
           billRecord.setFirstPrivilege(true);
           billRecord.setPrivilegeDate(now);
           billRecord.setPrivilegeOrgId(orgId);
         }
         Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
         String name = BaseContextHandler.getName();
         billRecord.setReceivableAmount(orderRecord.getTotalAmount());
         billRecord.setPrivilegeAmount(privilegeAmount);
         billRecord.setActualReceivableAmount(actualReceivableAmount);
         billRecord.setReceivedAmount(totalCharge);
         billRecord.setDebtAmount(actualReceivableAmount.subtract(totalCharge));
         billRecord.setInvoice(model.getInvoiceModel().getInvoice());
         billRecord.setInvoiceNumber(model.getInvoiceModel().getInvoiceNumber());
         billRecord.setCrtId(userId);
         billRecord.setCrtTime(now);
         billRecord.setCrtName(name);
         billRecord.setUpdTime(now);
         billRecord.setUpdId(userId);
         billRecord.setUpdName(name);
         billRecord.setBillNumber(billRecordBiz.generateBillNumber(orgId));
         billRecordBiz.insertBillRecord(billRecord);
         log.info("生成就诊账单编号结束===>");
         return billRecord;
      });
    } else {
      throw new ClientServiceException("【收费失败，创建账单失败】", OperationCodeConstants.DATA_EXIST);
    }
  }

  /**
   * 保存优惠明细
   *
   * @param generalDiscount 卡券优惠
   * @param accreditDiscount 授权折扣
   * @param discountType 优惠类型
   * @return discountType
   */
  private byte saveDiscountDetail(
      GeneralDiscountModel generalDiscount,
      AccreditDiscountModel accreditDiscount,
      byte discountType) {
    if (null != generalDiscount) {
      discountType = 1;
    }
    if (accreditDiscount != null) {
      discountType = 2;
    }
    return discountType;
  }

  /**
   * 校验订单是否能重新收费
   *
   * @param treatmentRecordId 就诊记录ID
   * @return OrderRecord
   */
  private OrderRecord checkOrderRecord(Integer treatmentRecordId) {
    OrderRecord orderRecord = new OrderRecord();
    orderRecord.setTreatmentRecordId(treatmentRecordId);
    orderRecord.setInservice(true);
    OrderRecord orderRecordResult = orderRecordBiz.selectOne(orderRecord);
    if (null == orderRecordResult) {
      throw new ClientServiceException("收欠费失败，当前就诊未查询到开单记录！", PARAMETERS_IS_ILLEGAL);
    }
    return orderRecordResult;
  }

  /**
   * 更新订单明细收费记录
   *
   * @param orderRecordId 订单ID
   * @param totalCharge 入账总额
   */
  private void updateOrderDetailPayRecordUnPrivilege(
      Integer orderRecordId, BigDecimal totalCharge) {
    OrderDetailPayRecord orderDetailPayRecord = new OrderDetailPayRecord();
    orderDetailPayRecord.setOrderRecordId(orderRecordId);
    orderDetailPayRecord.setInservice(true);
    List<OrderDetailPayRecord> detailPayRecords =
        orderDetailPayRecordBiz.selectList(orderDetailPayRecord);
    log.info(
        "↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓订单明细列表[detailPayRecords]↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    detailPayRecords.forEach(
        orderDetailPayRecord1 -> {
          log.info("==> {}", orderDetailPayRecord1);
        });
    log.info("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
    // 没有使用优惠情况
    for (OrderDetailPayRecord detailPayRecord : detailPayRecords) {
      BigDecimal actualReceivable = detailPayRecord.getActualReceivable();
      BigDecimal receivedAmount = detailPayRecord.getReceivedAmount();
      if (actualReceivable.compareTo(receivedAmount) > 0) {
        totalCharge = totalItemCharge(totalCharge, detailPayRecord);
        detailPayRecord.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        detailPayRecord.setUpdName(BaseContextHandler.getName());
        orderDetailPayRecordBiz.updateSelectiveById(detailPayRecord);
      }
    }
  }

  /**
   * 校验账单是否允许收欠费
   *
   * @param model 收欠费添加模型
   */
  private BillRecord checkCollectDebtParams(
          TreatTollModel model) {
    if (!StringHelper.eqZero(model.getOutstandingAmount())) {
      throw new ClientServiceException("收欠费失败，只能挂账一次", OPERATION_NOT_ALLOW);
    }
    Integer orderRecordId = model.getOrderRecordId();
    OrderRecord orderRecord = orderRecordBiz.selectById(orderRecordId);
    if (StringHelper.isNull(orderRecord)) {
      throw new ClientServiceException("收欠费失败，开单记录不存在！", PARAMETERS_IS_ILLEGAL);
    }
    // 只有收过费的订单才能收欠费
    BillRecord billRecord = new BillRecord();
    billRecord.setOrderRecordId(orderRecordId);
    Long count = billRecordBiz.selectCount(billRecord);
    if (count <= 0) {
      throw new ClientServiceException("收欠费失败，当前就诊还未收费，请到就诊列表进行收费！", QUERY_RESULT_INVALID);
    }
    // 未欠费的账单不能收欠费
    billRecord.setInservice(true);
    BillRecord record = billRecordBiz.selectOne(billRecord);
    if (StringHelper.isNotNull(record)) {
      BigDecimal debtAmount = record.getDebtAmount();
      if (StringHelper.leZero(debtAmount)) {
        throw new ClientServiceException("收欠费失败，当前账单不存在欠费！", QUERY_RESULT_INVALID);
      }
      BigDecimal receivedAmount = record.getReceivedAmount();
      BigDecimal actualReceivableAmount = record.getActualReceivableAmount();
      if (StringHelper.leZero(actualReceivableAmount)
              || !StringHelper.eqZero(receivedAmount)) {
        throw new ClientServiceException("收欠费失败，无需收欠费", PARAMETERS_IS_ILLEGAL);
      }
      // todo 校验发票
      return record;
    } else {
      throw new ClientServiceException("收欠费失败，当前账单不存在", DATA_NOT_EXIST);
    }
  }

  /**
   * 比较欠账总额与总入账金额
   *
   * @param totalCharge 总入账
   * @param debtAmount 欠费总额
   * @param outstandingAmount 挂帐金额
   */
  private void checkTotalChargeAndDebtAmount(
      BigDecimal totalCharge, BigDecimal debtAmount, BigDecimal outstandingAmount) {
    if (StringHelper.eq(debtAmount, outstandingAmount)) {
      log.info("本单挂账，收费为0");
      if (!StringHelper.eqZero(totalCharge)) {
        throw new ClientServiceException("仅支持整单挂账，不再支持部分挂账", PARAMETERS_IS_ILLEGAL);
      }
    }
    if (totalCharge.add(outstandingAmount).compareTo(debtAmount) != 0) {
      log.info(
          "========com.yunya.modules.treatment.biz.TollBiz.checkTotalChargeAndDebtAmount ================== ");
      log.info(
          "==> param:totalCharge={},debtAmount={},outstandingAmount={}",
          totalCharge,
          debtAmount,
          outstandingAmount);
      log.info("==> err_code:{}", PARAMETERS_IS_ILLEGAL);
      log.info("==> msg:入账方式金额与挂账金额之和不等于剩余应付金额合计！");
      log.info(
          "==================================================================================================");
      throw new ClientServiceException("入账方式金额与挂账金额之和不等于剩余应付金额合计！", PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 点击收费修改订单状态
   *
   * @param orderRecordId 开单记录ID
   */
  public void changeOrderRecordStatus(Integer orderRecordId) {
    OrderRecord orderRecord = orderRecordBiz.selectById(orderRecordId);
    if (null == orderRecord) {
      throw new ClientServiceException("账单不存在，请选择正确的就诊记录进行收费！", PARAMETERS_IS_ILLEGAL);
    }
    orderRecord.setId(orderRecordId);
    // 设置账单状态为收费中
    orderRecord.setStatus((byte) 3);
    orderRecordBiz.updateSelectiveById(orderRecord);
  }

  /**
   * 取消收费
   *
   * @param orderRecordId 开单记录ID
   */
  public void cancelCharge(Integer orderRecordId) {
    redisUtils.delete(LOCK_ORDER_PROCESSING_CHARGE + orderRecordId);
  }

  /**
   * 查询当前订单可用预付款支付金额
   *
   * @param orderRecordId 订单记录ID
   * @return
   */
  public BigDecimal currentOrderEnablePrepayment(Integer orderRecordId) {
    return this.orderRecordBiz.currentOrderEnablePrepayment(orderRecordId);
  }

  /**
   * 根据账单ID查询当前订单剩余可用预付款支付金额
   *
   * @param billRecordId 账单记录ID
   * @return BigDecimal
   */
  public BigDecimal findRestPrepaidAmount(Integer billRecordId) {
    BigDecimal amount = orderDetailPayRecordBiz.selectNoDiscountAmount(billRecordId);
    AccountItem accountItem = new AccountItem();
    accountItem.setName(ACCOUNT_ITEM_OF_PREPARE);
    AccountItem item = systemServiceFeign.findAccountItem(accountItem);
    BigDecimal receiptAmount =
        billPayDetailRecordMapper.selectReceiptAmountOfPrepaid(billRecordId, item.getId());
    return amount.subtract(receiptAmount);
  }

  public void clear(Integer orderId) {
    BillRecord record = new BillRecord();
    record.setOrderRecordId(orderId);
    billRecordBiz.delete(record);
    BillPayRecord payRecord = new BillPayRecord();
    payRecord.setOrderRecordId(orderId);
    billPayRecordMapper.delete(payRecord);
    BillPayDetailRecord detailRecord = new BillPayDetailRecord();
    detailRecord.setOrderRecordId(orderId);
    billPayDetailRecordMapper.delete(detailRecord);
    BillPayShareDetail shareDetail = new BillPayShareDetail();
    shareDetail.setOrderRecordId(orderId);
    billPayShareDetailMapper.delete(shareDetail);
    OrderDetailPayRecord orderPay = new OrderDetailPayRecord();
    orderPay.setOrderRecordId(orderId);
    orderDetailPayRecordBiz.delete(orderPay);
  }
}
