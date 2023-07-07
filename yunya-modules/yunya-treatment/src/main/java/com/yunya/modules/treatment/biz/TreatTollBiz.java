package com.yunya.modules.treatment.biz;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.discount.domain.vo.ItemUseBenefitVo;
import com.yunya.feign.discount.domain.vo.OrderBenefitDetailVo;
import com.yunya.feign.discount.domain.vo.PatientItemBenefitVo;
import com.yunya.feign.discount.domain.vo.PatientOrderBenefitVo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.model.MemberExpendRecordModel;
import com.yunya.feign.patient_central.domain.model.PrepaidExpendRecordModel;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.treatment.domain.model.*;
import com.yunya.feign.treatment.domain.query.OrderPrivilegeQuery;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.feign.wechat.RemoteWechatServiceFeign;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya.feign.wechat.enums.TemplateEnum;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.PatientDepositAccountTypeEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.AccountItem;
import com.yunya.models.treatment.*;
import com.yunya.modules.treatment.mapper.BillPayDetailRecordMapper;
import com.yunya.modules.treatment.mapper.BillPayRecordMapper;
import com.yunya.modules.treatment.mapper.TreatmentRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.*;
import static com.yunya.framework.common.constant.BusinessConstants.ACCOUNT_ITEM_OF_PREPARE;
import static com.yunya.framework.common.constant.BusinessConstants.COMPANY_ORGID;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.LOCK_ORDER_PROCESSING_CHARGE;
import static com.yunya.framework.common.constant.RedisConstants.LOCK_ORDER_PROCESSING_UNLOCK;
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
  /** 患者服务调用 */
  @Autowired private RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;
  /** 优惠服务调用 */
  @Autowired private RemoteDiscountFeign discountFeign;
  /** 微信服务调用 */
  @Autowired private RemoteWechatServiceFeign weChatServiceFeign;
  /** 开单记录 */
  @Autowired private OrderRecordBiz orderRecordBiz;
  /** 开单明细 */
  @Autowired private OrderDetailBiz orderDetailBiz;
  /** 订单支付记录 */
  @Autowired private OrderDetailPayRecordBiz orderDetailPayRecordBiz;
  /** 账单记录 */
  @Autowired private BillRecordBiz billRecordBiz;
  /** 账单记录 */
  @Autowired private BaseOralTariffBiz oralTariffBiz;
  /** 账单记录 */
  @Autowired private BaseTariffBiz baseTariffBiz;
  /** 账单支付记录 */
  @Autowired private BillPayRecordMapper billPayRecordMapper;
  /** 账单支付明细记录 */
  @Autowired private BillPayDetailRecordMapper billPayDetailRecordMapper;
  /** 就诊记录 */
  @Autowired private TreatmentRecordMapper treatmentRecordMapper;
  /** 收费项目分摊明细 */
  @Autowired private BillPayShareDetailBiz billPayShareDetailBiz;

  /**
   * 根据优惠信息匹配订单优惠
   *
   * @param query 优惠条件
   * @return List<OrderDetailChargeVO>
   */
  public List<OrderDetailChargeVO> matchOrderTailPrivilege(OrderPrivilegeQuery query) {
    Integer orderRecordId = query.getOrderRecordId();
    List<OrderDetailChargeVO> detailList = orderDetailBiz.getChargeOrderDetailList(orderRecordId);
    if (StringHelper.isEmpty(detailList)) {
      throw new ClientServiceException("适用优惠失败，未查询到当前就诊开单数据！", PARAMETERS_IS_ILLEGAL);
    }
    Byte discountType = query.getDiscountType();
    GeneralDiscountModel generalDiscountModel = query.getGeneralDiscountModel();
    // 校验优惠参数
    checkPrivilegeParam(discountType, generalDiscountModel);
    switch (discountType) {
      case 1:
        // 匹配卡券优惠
        matchGeneralDiscountOrderDetailValue(orderRecordId, detailList, generalDiscountModel);
        break;
      default:
        break;
    }
    return detailList;
  }

  /**
   * 匹配普通折扣订单详情信息列表
   *
   * @param orderRecordId 订单记录ID
   * @param detailList 订单详情列表
   * @param generalDiscountModel 卡券列表
   */
  private void matchGeneralDiscountOrderDetailValue(
      Integer orderRecordId,
      List<OrderDetailChargeVO> detailList,
      GeneralDiscountModel generalDiscountModel) {
    ResponseResult<PatientOrderBenefitVo> responseResult = findGeneralPrivilege(orderRecordId, null, generalDiscountModel);
    PatientOrderBenefitVo resultData = responseResult.getData();
    // 卡券优惠为空
    if (null == resultData) {
      throw new ClientServiceException(responseResult.getMsg(), responseResult.getStatus());
    }
    matchGeneralDiscountOrderDetailValue(detailList, resultData.getItemList());
  }

  /**
   * 匹配普通折扣订单详情信息列表
   *
   * @param detailList 订单详情列表
   * @param itemList 优惠项目列表
   */
  private void matchGeneralDiscountOrderDetailValue(
          List<OrderDetailChargeVO> detailList,
          List<PatientItemBenefitVo> itemList) {
    for (OrderDetailChargeVO vo : detailList) {
      for (PatientItemBenefitVo benefitVo : itemList) {
        BigDecimal receivableAmount = vo.getReceivableAmount();
        BigDecimal actualAmount = vo.getActualAmount();
        List<PrivilegeCouponInfoVO> discountAppliesCoupon = vo.getDiscountAppliesCoupons();
        if (vo.getOrderDetailId().equals(benefitVo.getOrderDetailId())) {
          BigDecimal discountAmount = benefitVo.getItemBenefitAmount();
          actualAmount = actualAmount.subtract(discountAmount);
          BigDecimal discountRate = actualAmount.divide(receivableAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
          // 设置折扣率
          vo.setDiscountRate(discountRate);
          // 设置订单明细卡券匹配信息
          List<ItemUseBenefitVo> benefitList = benefitVo.getItemBenefitList();
          if (StringHelper.isNotEmpty(benefitList)) {
            setPrivilegeCouponInfoValue(discountAppliesCoupon, benefitList);
          }
          if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            vo.setHasDiscount(true);
          }
        }
        vo.setActualAmount(actualAmount);
        if (receivableAmount.compareTo(actualAmount) > 0) {
          vo.setDiscountAppliesCoupons(discountAppliesCoupon);
        }
      }
    }
  }

  /**
   * 设置订单卡券优惠信息
   *
   * @param discountAppliesCoupon 订单卡券优惠匹配信息
   * @param benefitList 匹配卡券列表
   */
  private void setPrivilegeCouponInfoValue(
      List<PrivilegeCouponInfoVO> discountAppliesCoupon, List<ItemUseBenefitVo> benefitList) {
    benefitList.forEach(
        benefitVo -> {
          Integer benefitType = benefitVo.getBenefitType();
            PrivilegeCouponInfoVO couponInfoVO = new PrivilegeCouponInfoVO();
            couponInfoVO.setBenefitId(benefitVo.getBenefitId());
            couponInfoVO.setCouponType(
                    0 == benefitType ? Integer.valueOf(99) : benefitVo.getCouponType());
            couponInfoVO.setBenefitName(benefitVo.getBenefitName());
            couponInfoVO.setBenefitAmount(benefitVo.getBenefitAmount());
            couponInfoVO.setCardNumber(benefitVo.getCardNumber());
            discountAppliesCoupon.add(couponInfoVO);
        });
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
    return confirmCharge(model);
  }

  /**
   * 确认收费
   *
   * @param model 收费参数
   */
  @Transactional
  public TollConfirmVO confirmCharge(TreatTollModel model) {
    OrderRecord orderRecord = checkChargeParam(model);
    Integer orderRecordId = orderRecord.getId();
    Integer patientId = orderRecord.getPatientId();
    Byte discountType = model.getDiscountType();
    GeneralDiscountModel generalDiscount = model.getGeneralDiscountModel();

    BigDecimal privilegeAmount = calculatePrivilegeAmount(discountType, orderRecordId, patientId, generalDiscount);
    // 计算入账总额、应收总额
    BigDecimal totalCharge = calculateTotalCharge(model);
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
    orderRecordBiz.updateOrderStatus(orderRecord.getId(),
            Integer.valueOf(BaseContextHandler.getUserID()),
            BaseContextHandler.getName(),
            billRecord.getCrtTime(),
            BusinessConstants.ORDER_FINISH_STATUS);
    // 更新就诊状态
    updateTreatmentRecordStatus(orderRecord.getTreatmentRecordId());

    asyncProcessCharge(billPayRecord, model, totalCharge, true);
    asyncPushWxExpendMsg(billPayRecord);
    redisUtils.delete(LOCK_ORDER_PROCESSING_CHARGE + orderRecordId);
    return TollConfirmVO.builder()
            .billNumber(billRecord.getBillNumber())
            .billPayRecordId(billPayRecord.getId()).build();
  }

  /**
   * 收费后同步中间表
   *
   * @param billPayRecord
   * @param isMqTreatment
   */
  private void chargedMQMiddleTable(BillPayRecord billPayRecord, Boolean isMqTreatment) {
    rabbitMqServiceFeign.sendMessage(billPayRecord.getOrderRecordId(), 1, BaseBill);
    rabbitMqServiceFeign.sendMessage(billPayRecord.getId(), 0, BaseBillPay);
    // 变更治疗计划详情的状态
    rabbitMqServiceFeign.sendMessage(billPayRecord.getTreatmentRecordId(), 0, TreatPlanDetail);
    if (isMqTreatment) {
      mqSendTreatMessage(billPayRecord.getTreatmentRecordId());
    }
  }

  /**
   * 同步BaseTreatmentProcess
   * @param treatmentRecordId
   */
  private void mqSendTreatMessage(Integer treatmentRecordId) {
    TreatmentRecord treatmentRecord = treatmentRecordMapper.selectByPrimaryKey(treatmentRecordId);
    Integer appointmentId = treatmentRecord.getAppointmentId();
    if (StringHelper.isNotNull(appointmentId)) {
      rabbitMqServiceFeign.sendMessage(appointmentId, 0, 1, BaseTreatmentProcess);
    } else {
      rabbitMqServiceFeign.sendMessage(
              treatmentRecord.getRegisteredId(), 1, 1, BaseTreatmentProcess);
    }
  }

  /**
   * 异步处理收费的次要流程
   *
   * @param billPayRecord 账单收费记录
   * @param model 收费基础参数模型
   * @param totalCharge 入账总额
   * @param isMqTreatment 是否同步中间表base_treatment_process
   */
  @Async("treatmentThreadPool")
  protected void asyncProcessCharge(BillPayRecord billPayRecord, TreatTollDebtModel model, BigDecimal totalCharge, Boolean isMqTreatment) {
    Integer orderRecordId = billPayRecord.getOrderRecordId();
    Set<PrepaymentAccountModel> prepaymentAccounts = model.getPrepaymentAccountModels();
    Set<MemberAccountModel> memberAccounts = model.getMemberAccountModels();
    Set<PaymentModel> payments = model.getPaymentModels();
    Byte discountType = model.getDiscountType();

    // 保存订单项目收费分摊明细
    billPayShareDetailBiz.saveItemPaySharedAmount(totalCharge, payments, billPayRecord);

    // 调用保存优惠明细接口
    savePrivilegeDetail(discountType, billPayRecord.getPatientId(), orderRecordId, model);

    // 调用会员卡消费接口
    if (StringHelper.isNotEmpty(prepaymentAccounts)) {
      usePrepaymentAccount(
              prepaymentAccounts,
              billPayRecord);
    }
    // 调用预付款消费接口
    if (StringHelper.isNotEmpty(memberAccounts)) {
      ResponseResult expend =
        useMemberAccount(
              memberAccounts,
              billPayRecord);
      if (expend.getStatus() > 0) {
        throw new ClientServiceException(expend.getMsg(), expend.getStatus());
      }
    }
    // 消费返点
    consumptionRebate(billPayRecord);

    // 发送MQ消息
    chargedMQMiddleTable(billPayRecord, isMqTreatment);
  }

  /**
   * 给初诊患者返点
   *
   * @param billPayRecord
   */
  private void firstVisitRebate(BillPayRecord billPayRecord) {
    TreatmentRecordVO treatment = treatmentRecordMapper.selectTreatmentInfoById(billPayRecord.getTreatmentRecordId());
    if (treatment.getFirstVisit() == 0) {

    }
  }

  /**
   * 给患者的推荐人返点
   *
   * @param billPayRecord
   */
  private void consumptionRebate(BillPayRecord billPayRecord) {

    // 给初诊患者返点
    firstVisitRebate(billPayRecord);
  }

  /**
   * 异步发送微信消费通知
   *
   * @param billPayRecord
   */
  @Async("treatmentThreadPool")
  public void asyncPushWxExpendMsg(BillPayRecord billPayRecord) {
      try {
        PatientBaseInfo patient = remotePatientCentralServiceFeign.findPatientInfoById(billPayRecord.getPatientId());
        String itemName = assembleItemName(billPayRecord.getOrderRecordId());
        WxTemplateMsgModel model = new WxTemplateMsgModel();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("keyword1", itemName);
        paramMap.put("keyword2", billPayRecord.getReceivedAmount());
        paramMap.put("keyword3", patient.getName());
        model.setPatientId(billPayRecord.getPatientId());
        model.setTemplateEnum(TemplateEnum.MEMBER_PAY);
        model.setParamMap(paramMap);
        this.weChatServiceFeign.pushTemplate(model);
      } catch (Exception var6) {
        log.error("TreatTollBiz aynsPushWxExpendMsg error: {}", var6);
      }
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
  public BillPayRecord generalBillPayRecordWithDetail(BillRecord billRecord, BigDecimal totalCharge, BigDecimal actualReceivableAmount, TreatTollDebtModel model) {
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
    prepayments.forEach(payment->{
      if (!PatientDepositAccountTypeEnum.isRelTypeId(payment.getAccountItemId())) {
        throw new ClientServiceException("无效的预付款账户类型", PARAMETERS_IS_ILLEGAL);
      }
    });
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
   * 查询并装配项目名称列表
   *
   * @param orderRecordId
   * @return
   */
  public String assembleItemName(Integer orderRecordId) {
    List<OrderDetailVO> orderDetails = orderDetailBiz.findOrderDetailList(orderRecordId, null);
    return orderDetails.stream()
        .filter(vo->StringHelper.isNotEmpty(vo.getBillingItemName()))
        .map(vo->vo.getBillingItemName())
        .collect(Collectors.joining("，"));
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
    Map<String, List<SwipeItemModel>> swipeMap = extractSwipeItemMap(model);
    // 构建明细收费列表
    Map<Integer, PatientItemBenefitVo> discountMap = findBillDiscountCoupons(model, billRecord);
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
        PatientItemBenefitVo benefitVO = discountMap.get(orderDetailId);
        if (StringHelper.isNotNull(benefitVO)) {
          privilegeAmount = benefitVO.getItemBenefitAmount();
          if (privilegeAmount.compareTo(actualAmount) > 0) {
            privilegeAmount = actualAmount;
          }
          actualAmount = receivableAmount.subtract(privilegeAmount);
          if (BigDecimal.ZERO.compareTo(actualAmount) > 0) {
            actualAmount = BigDecimal.ZERO;
          }
          // 获取补入工作量
          couponWorkload = benefitVO.getSupplyWorkload();
        }

        detailPayRecord.setReceivableAmount(receivableAmount);
        BigDecimal swipeWorkload = BigDecimal.ZERO;
        BigDecimal swipeCouponWorkload = BigDecimal.ZERO;
        String key = StringHelper.joinWith(",", detail.getType(), detail.getBillingItemId());
        List<SwipeItemModel> swipeItemModels = swipeMap.get(key);
        if (StringHelper.isNotEmpty(swipeItemModels)) {
          for (SwipeItemModel swipeItemModel : swipeItemModels) {
            BigDecimal quantity = BigDecimal.valueOf(swipeItemModel.getQuantity());
            swipeWorkload = swipeWorkload.add(swipeItemModel.getPackagePrice().multiply(quantity));
            swipeCouponWorkload = swipeCouponWorkload.add(swipeItemModel.getSupplyWorkload().multiply(quantity));
            swipeItemModel.setOrderDetailId(orderDetailId);
          }
        }
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

  private Map<String, List<SwipeItemModel>> extractSwipeItemMap(TreatTollModel model) {
    Map<String, List<SwipeItemModel>> result = new HashMap<>();
    List<SwipeItemModel> swipeItemModels = model.getSwipeItemModels();
    if (StringHelper.isNotEmpty(swipeItemModels)) {
      swipeItemModels.forEach(vo->{
        String key = StringHelper.joinWith(",", vo.getItemType(), vo.getItemId());
        List<SwipeItemModel> list = result.computeIfAbsent(key, k->new ArrayList<>());
        list.add(vo);
      });
    }
    return result;
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
    detailPayRecord.setOrderDetailId(detail.getOrderRecordId());
    detailPayRecord.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
    detailPayRecord.setCrtId(userId);
    detailPayRecord.setCrtName(name);
    detailPayRecord.setCrtTime(now);
    detailPayRecord.setUpdId(userId);
    detailPayRecord.setUpdName(name);
    detailPayRecord.setUpdTime(now);
    return detailPayRecord;
  }

  private void saveBillPayDetailRecordWithMixDiscount(BigDecimal totalCharge, Integer orderRecordId, Integer billRecordId, GeneralDiscountModel generalDiscount, AccreditDiscountModel accreditDiscount) {
    OrderRecord orderRecord = orderRecordBiz.selectById(orderRecordId);
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    orderDetail.setInservice(true);
    List<OrderDetail> orderDetails = orderDetailBiz.selectList(orderDetail);

    Integer patientId = orderRecord.getPatientId();
    Integer treatmentRecordId = orderRecord.getTreatmentRecordId();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    ResponseResult<PatientOrderBenefitVo> result = findGeneralPrivilege(orderRecordId, patientId, generalDiscount);
    PatientOrderBenefitVo benefitVo = result.getData();
    Map<Integer, OrderDetailPayRecord> detailPays = new HashMap<>(16);
    if (null != benefitVo) {
      List<PatientItemBenefitVo> benefitVos = benefitVo.getItemList();
      for (OrderDetail detail : orderDetails) {
        OrderDetailPayRecord detailPayRecord = new OrderDetailPayRecord();
        detailPayRecord.setOrgId(orgId);
        detailPayRecord.setPatientId(patientId);
        detailPayRecord.setTreatmentRecordId(treatmentRecordId);
        detailPayRecord.setOrderRecordId(orderRecordId);
        Integer detailId = detail.getId();
        detailPayRecord.setOrderDetailId(detailId);
        detailPayRecord.setBillRecordId(billRecordId);
        BigDecimal receivableAmount = detail.getReceivableAmount();
        detailPayRecord.setReceivableAmount(receivableAmount);
        BigDecimal privilegeAmount = BigDecimal.valueOf(0);
        BigDecimal actualAmount = receivableAmount;
        BigDecimal couponWorkload = BigDecimal.valueOf(0);
        for (PatientItemBenefitVo vo : benefitVos) {
          Integer orderDetailId = vo.getOrderDetailId();
          if (detailId.equals(orderDetailId)) {
            privilegeAmount = vo.getItemBenefitAmount();
            if (privilegeAmount.compareTo(actualAmount) > 0) {
              privilegeAmount = actualAmount;
            }
            actualAmount = receivableAmount.subtract(privilegeAmount);
            if (BigDecimal.ZERO.compareTo(actualAmount) > 0) {
              actualAmount = BigDecimal.ZERO;
            }
            // 获取补入工作量
            couponWorkload = vo.getSupplyWorkload();
          }
        }
        detailPayRecord.setPrivilegeAmount(privilegeAmount);
        detailPayRecord.setActualReceivable(actualAmount);
        // 设置已收
        totalCharge = totalItemCharge(totalCharge, detailPayRecord);
        detailPayRecord.setCouponWorkload(couponWorkload);
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        detailPayRecord.setCrtId(userId);
        String name = BaseContextHandler.getName();
        detailPayRecord.setCrtName(name);
        detailPayRecord.setUpdId(userId);
        detailPayRecord.setUpdName(name);
        detailPays.put(detailId, detailPayRecord);
      }
      //      } else {
      //        throw new ClientServiceException("收费失败，当前选择卡券未匹配任何优惠！", PARAMETERS_IS_ILLEGAL);
      //      }
    } else {
      throw new ClientServiceException(result.getMsg(), result.getStatus());
    }

    List<AccreditDiscountDetailModel> discountDetailModels =
            accreditDiscount.getAccreditDiscountDetailModels();
    if (StringHelper.isNotEmpty(orderDetails)) {
      for (OrderDetail detail : orderDetails) {
        Integer detailId = detail.getId();
        OrderDetailPayRecord detailPayRecord = detailPays.computeIfAbsent(detailId, k->new OrderDetailPayRecord());
        BigDecimal receivableAmount = detail.getReceivableAmount();
        BigDecimal privilegeAmount = BigDecimal.valueOf(0);
        BigDecimal actualAmount = receivableAmount;
        for (AccreditDiscountDetailModel discountDetailModel : discountDetailModels) {
          if (detail.getBillingItemId().equals(discountDetailModel.getBillingItemId())
                  && detail.getType().equals(discountDetailModel.getType())) {
            // 折扣单价
            BigDecimal discountPrice =
                    discountDetailModel
                            .getActualAmount()
                            .divide(
                                    BigDecimal.valueOf(discountDetailModel.getQuantity()),
                                    4,
                                    RoundingMode.HALF_UP);
            actualAmount = discountPrice.multiply(BigDecimal.valueOf(detail.getQuantity()));
            if (BigDecimal.ZERO.compareTo(actualAmount) > 0) {
              throw new ClientServiceException("实收金额不能小于0！", PARAMETERS_IS_ILLEGAL);
            }
            privilegeAmount = receivableAmount.subtract(actualAmount);
            if (BigDecimal.ZERO.compareTo(privilegeAmount) > 0) {
              throw new ClientServiceException("授权折扣的实收金额不能大于原价！", PARAMETERS_IS_ILLEGAL);
            }
          }
        }
        if (privilegeAmount.compareTo(BigDecimal.ZERO) > 0) {
          detailPayRecord.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
          detailPayRecord.setPatientId(orderRecord.getPatientId());
          detailPayRecord.setTreatmentRecordId(orderRecord.getTreatmentRecordId());
          detailPayRecord.setOrderRecordId(orderRecordId);
          detailPayRecord.setOrderDetailId(detailId);
          detailPayRecord.setBillRecordId(billRecordId);
          detailPayRecord.setReceivableAmount(receivableAmount);

          detailPayRecord.setPrivilegeAmount(privilegeAmount);
          detailPayRecord.setActualReceivable(actualAmount);
          detailPayRecord.setCouponWorkload(BigDecimal.valueOf(0));
          // 设置已收
          totalCharge = totalItemCharge(totalCharge, detailPayRecord);
          Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
          detailPayRecord.setCrtId(userId);
          String name = BaseContextHandler.getName();
          detailPayRecord.setCrtName(name);
          detailPayRecord.setUpdId(userId);
          detailPayRecord.setUpdName(name);
        }
      }
      if (StringHelper.isNotEmpty(detailPays)) {
        orderDetailPayRecordBiz.batchInsert(new ArrayList<>(detailPays.values()));
      }
    }
  }

  private Map<Integer, PatientItemBenefitVo> findBillDiscountCoupons(TreatTollModel model, BillRecord billRecord) {
    if (model.getDiscountType() != 0) {
      ResponseResult<PatientOrderBenefitVo> privileges = findGeneralPrivilege(billRecord.getOrderRecordId(), billRecord.getPatientId(), model.getGeneralDiscountModel());
      List<PatientItemBenefitVo> itemList = privileges.getData().getItemList();
      return itemList.stream().collect(toMap(PatientItemBenefitVo::getOrderDetailId, Function.identity()));
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

  /**
   * 保存使用卡券优惠的订单明细优惠记录
   *
   * @param totalCharge 总入账金额
   * @param orderRecordId 订单记录ID
   * @param billRecordId 账单记录ID
   * @param generalDiscount 卡券列表
   */
  private void saveBillPayDetailRecordWithGeneralDiscount(
      BigDecimal totalCharge,
      Integer orderRecordId,
      Integer billRecordId,
      GeneralDiscountModel generalDiscount) {
    OrderRecord orderRecord = orderRecordBiz.selectById(orderRecordId);
    Integer patientId = orderRecord.getPatientId();
    Integer treatmentRecordId = orderRecord.getTreatmentRecordId();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    PatientChooseBenefitForm paramForm = new PatientChooseBenefitForm();
    paramForm.setPatientId(patientId);
    paramForm.setOrderId(orderRecordId);
    paramForm.setOrgId(orgId);
    paramForm.setMemberCardId(generalDiscount.getMemberTypeId());
    paramForm.setDiscountId(generalDiscount.getDiscountCouponId());
    List<Integer> voucherIds = Lists.newArrayList();
    List<Integer> exchangeIds = Lists.newArrayList();
    List<Integer> packageIds = Lists.newArrayList();
    List<CouponDiscountInfoModel> discountInfoModels =
        generalDiscount.getCouponDiscountInfoModels();
    setCouponListValue(discountInfoModels, voucherIds, exchangeIds, packageIds);
    paramForm.setVoucherIds(voucherIds);
    paramForm.setExchangeIds(exchangeIds);
    paramForm.setPackageIds(packageIds);
    ResponseResult<PatientOrderBenefitVo> result = discountFeign.choiceBenefit(paramForm);
    PatientOrderBenefitVo benefitVo = result.getData();
    if (null != benefitVo) {
      OrderDetail orderDetail = new OrderDetail();
      orderDetail.setOrderRecordId(orderRecordId);
      orderDetail.setInservice(true);
      List<OrderDetail> orderDetails = orderDetailBiz.selectList(orderDetail);
      List<PatientItemBenefitVo> benefitVos = benefitVo.getItemList();
      // TODO: bug3210 要求去掉优惠判断
      //      if (StringHelper.isNotEmpty(benefitVos)) {
      for (OrderDetail detail : orderDetails) {
        OrderDetailPayRecord detailPayRecord = new OrderDetailPayRecord();
        detailPayRecord.setOrgId(orgId);
        detailPayRecord.setPatientId(patientId);
        detailPayRecord.setTreatmentRecordId(treatmentRecordId);
        detailPayRecord.setOrderRecordId(orderRecordId);
        Integer detailId = detail.getId();
        detailPayRecord.setOrderDetailId(detailId);
        detailPayRecord.setBillRecordId(billRecordId);
        BigDecimal receivableAmount = detail.getReceivableAmount();
        detailPayRecord.setReceivableAmount(receivableAmount);
        BigDecimal privilegeAmount = BigDecimal.valueOf(0);
        BigDecimal actualAmount = receivableAmount;
        BigDecimal couponWorkload = BigDecimal.valueOf(0);
        for (PatientItemBenefitVo vo : benefitVos) {
          Integer orderDetailId = vo.getOrderDetailId();
          if (detailId.equals(orderDetailId)) {
            privilegeAmount = vo.getItemBenefitAmount();
            if (privilegeAmount.compareTo(actualAmount) > 0) {
              privilegeAmount = actualAmount;
            }
            actualAmount = receivableAmount.subtract(privilegeAmount);
            if (BigDecimal.ZERO.compareTo(actualAmount) > 0) {
              actualAmount = BigDecimal.ZERO;
            }
            // 获取补入工作量
            couponWorkload = vo.getSupplyWorkload();
          }
        }
        detailPayRecord.setPrivilegeAmount(privilegeAmount);
        detailPayRecord.setActualReceivable(actualAmount);
        // 设置已收
        totalCharge = totalItemCharge(totalCharge, detailPayRecord);
        detailPayRecord.setCouponWorkload(couponWorkload);
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        detailPayRecord.setCrtId(userId);
        String name = BaseContextHandler.getName();
        detailPayRecord.setCrtName(name);
        detailPayRecord.setUpdId(userId);
        detailPayRecord.setUpdName(name);
        orderDetailPayRecordBiz.insertSelective(detailPayRecord);
      }
      //      } else {
      //        throw new ClientServiceException("收费失败，当前选择卡券未匹配任何优惠！", PARAMETERS_IS_ILLEGAL);
      //      }
    } else {
      throw new ClientServiceException(result.getMsg(), result.getStatus());
    }
  }

  /**
   * 将卡券列表进行分类
   *
   * @param coupons 优惠卡券
   * @param voucherIds 代金券
   * @param exchangeIds 兑换券
   * @param packageIds 套餐券
   */
  private void setCouponListValue(
      List<CouponDiscountInfoModel> coupons,
      List<Integer> voucherIds,
      List<Integer> exchangeIds,
      List<Integer> packageIds) {
    if (StringHelper.isNotEmpty(coupons)) {
      for (CouponDiscountInfoModel model : coupons) {
        Byte couponType = model.getCouponType();
        Integer couponCommonInfoId = model.getCouponCommonInfoId();
        switch (couponType) {
            // 代金券
          case 0:
            voucherIds.add(couponCommonInfoId);
            break;
            // 兑换券
          case 2:
            exchangeIds.add(couponCommonInfoId);
            break;
            // 套餐券
          case 3:
            packageIds.add(couponCommonInfoId);
            break;
          default:
            break;
        }
      }
    }
  }

  /**
   * 保存使用授权折扣的订单明细优惠记录
   *
   * @param totalCharge 总入账金额
   * @param orderRecordId 订单记录ID
   * @param billRecordId 账单记录ID
   * @param accreditDiscount 授权折扣列表
   */
  private void saveBillPayDetailRecordWithAccreditDiscount(
      BigDecimal totalCharge,
      Integer orderRecordId,
      Integer billRecordId,
      AccreditDiscountModel accreditDiscount) {
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    List<OrderDetail> orderDetails = orderDetailBiz.selectList(orderDetail);
    OrderRecord orderRecord = orderRecordBiz.selectById(orderRecordId);
    List<AccreditDiscountDetailModel> discountDetailModels =
        accreditDiscount.getAccreditDiscountDetailModels();
    if (StringHelper.isNotEmpty(orderDetails)) {
      for (OrderDetail detail : orderDetails) {
        OrderDetailPayRecord detailPayRecord = new OrderDetailPayRecord();
        detailPayRecord.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
        detailPayRecord.setPatientId(orderRecord.getPatientId());
        detailPayRecord.setTreatmentRecordId(orderRecord.getTreatmentRecordId());
        detailPayRecord.setOrderRecordId(orderRecordId);
        Integer detailId = detail.getId();
        detailPayRecord.setOrderDetailId(detailId);
        detailPayRecord.setBillRecordId(billRecordId);
        BigDecimal receivableAmount = detail.getReceivableAmount();
        detailPayRecord.setReceivableAmount(receivableAmount);
        BigDecimal privilegeAmount = BigDecimal.valueOf(0);
        BigDecimal actualAmount = receivableAmount;
        for (AccreditDiscountDetailModel discountDetailModel : discountDetailModels) {
          if (detail.getBillingItemId().equals(discountDetailModel.getBillingItemId())
              && detail.getType().equals(discountDetailModel.getType())) {
            // 折扣单价
            BigDecimal discountPrice =
                discountDetailModel
                    .getActualAmount()
                    .divide(
                        BigDecimal.valueOf(discountDetailModel.getQuantity()),
                        4,
                        RoundingMode.HALF_UP);
            actualAmount = discountPrice.multiply(BigDecimal.valueOf(detail.getQuantity()));
            if (BigDecimal.ZERO.compareTo(actualAmount) > 0) {
              throw new ClientServiceException("实收金额不能小于0！", PARAMETERS_IS_ILLEGAL);
            }
            privilegeAmount = receivableAmount.subtract(actualAmount);
            if (BigDecimal.ZERO.compareTo(privilegeAmount) > 0) {
              throw new ClientServiceException("授权折扣的实收金额不能大于原价！", PARAMETERS_IS_ILLEGAL);
            }
          }
        }
        detailPayRecord.setPrivilegeAmount(privilegeAmount);
        detailPayRecord.setActualReceivable(actualAmount);
        detailPayRecord.setCouponWorkload(BigDecimal.valueOf(0));
        // 设置已收
        totalCharge = totalItemCharge(totalCharge, detailPayRecord);
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        detailPayRecord.setCrtId(userId);
        String name = BaseContextHandler.getName();
        detailPayRecord.setCrtName(name);
        detailPayRecord.setUpdId(userId);
        detailPayRecord.setUpdName(name);
        orderDetailPayRecordBiz.insertSelective(detailPayRecord);
      }
    }
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
        privilegeAmount = calculateGeneralPrivilegeAmount(orderRecordId, patientId, generalDiscountModel);
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
    PatientOrderBenefitVo benefitVo = findGeneralPrivilege(orderRecordId, patientId, generalDiscountModel).getData();
    if (StringHelper.isNotNull(benefitVo)) {
      privilegeAmount = benefitVo.getBenefitTotalAmount();
      if (BigDecimal.ZERO.compareTo(privilegeAmount) > 0) {
        throw new ClientServiceException("收费失败，优惠金额小于0，请核对优惠信息是否正确！", PARAMETERS_IS_ILLEGAL);
      }
    }
    return privilegeAmount;
  }

  private ResponseResult<PatientOrderBenefitVo> findGeneralPrivilege(Integer orderRecordId, Integer patientId, GeneralDiscountModel generalDiscountModel) {
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
    List<CouponDiscountInfoModel> models = generalDiscountModel.getCouponDiscountInfoModels();
    // 分类卡券列表
    setCouponListValue(models, voucherIds, exchangeIds, packageIds);
    form.setExchangeIds(exchangeIds);
    form.setPackageIds(packageIds);
    form.setVoucherIds(voucherIds);
    return  discountFeign.choiceBenefit(form);
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
    checkOrderRecordStatus(status);
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
  private BigDecimal calculateTotalCharge(TreatTollDebtModel tollModel) {
    Set<PrepaymentAccountModel> prepaymentAccounts = tollModel.getPrepaymentAccountModels();
    Set<MemberAccountModel> memberAccounts = tollModel.getMemberAccountModels();
    Set<PaymentModel> payments = tollModel.getPaymentModels();
    BigDecimal totalAmount = BigDecimal.ZERO;
    // 预付款入账总额
    if (StringHelper.isNotEmpty(prepaymentAccounts)) {
      for (PrepaymentAccountModel model : prepaymentAccounts) {
        totalAmount = totalAmount.add(StringHelper.defaultBigDecimal(model.getAmount()));
      }
    }
    // 会员卡入账总额
    if (StringHelper.isNotEmpty(memberAccounts)) {
      for (MemberAccountModel model : memberAccounts) {
        totalAmount = totalAmount.add(StringHelper.defaultBigDecimal(model.getAmount()));
      }
    }
    // 其他方式入账总额
    if (StringHelper.isNotEmpty(payments)) {
      for (PaymentModel model : payments) {
        totalAmount = totalAmount.add(StringHelper.defaultBigDecimal(model.getAmount()));
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
    billPayDetailRecord.setBillPayRecordId(billPayRecord.getId());
    billPayDetailRecord.setAccountItemId(accountItemId);
    billPayDetailRecord.setAmount(amount);
    billPayDetailRecord.setType(type);
    billPayDetailRecord.setRemark(remarks);
    return billPayDetailRecord;
  }

  /**
   * 使用会员账户付款
   *
   * @param memberAccountModels 会员账户列表
   * @param billPayRecord 账单收费记录
   * @return 处理结果
   */
  private ResponseResult useMemberAccount(
      Set<MemberAccountModel> memberAccountModels,
      BillPayRecord billPayRecord) {
    MemberExpendRecordModel memberExpendRecordModel = new MemberExpendRecordModel();
    Iterator<MemberAccountModel> iterator = memberAccountModels.iterator();
    ResponseResult responseResult = null;
    while (iterator.hasNext()) {
      MemberAccountModel memberAccountModel = iterator.next();
      memberExpendRecordModel.setPatientId(billPayRecord.getPatientId());
      memberExpendRecordModel.setMemberId(memberAccountModel.getMemberNum());
      memberExpendRecordModel.setExpendTotal(memberAccountModel.getAmount());
      memberExpendRecordModel.setTreatmentRecordId(billPayRecord.getTreatmentRecordId());
      memberExpendRecordModel.setOrderRecordId(billPayRecord.getOrderRecordId());
      memberExpendRecordModel.setBillRecordId(billPayRecord.getBillRecordId());
      memberExpendRecordModel.setBillPayRecordId(billPayRecord.getId());
      memberExpendRecordModel.setPrincipalAmount(memberAccountModel.getPrincipalAmount());
      memberExpendRecordModel.setBonusAmount(memberAccountModel.getBonusAmount());
      ResponseResult expend = remotePatientCentralServiceFeign.expend(memberExpendRecordModel);
      // 服务调用成功返回0，否则返回大于0的状态码
      if (expend.getStatus() > 0) {
        responseResult = expend;
        break;
      }
    }
    return responseResult != null ? responseResult : ResponseUtil.success();
  }

  /**
   * 使用预付款账户付款
   *
   * @param prepaymentAccountModels 预付款账户列表
   * @param billPayRecord 账单收费记录
   */
  private void usePrepaymentAccount(
      Set<PrepaymentAccountModel> prepaymentAccountModels,
      BillPayRecord billPayRecord) {
    PrepaidExpendRecordModel prepaidExpendRecordModel = new PrepaidExpendRecordModel();
    prepaymentAccountModels.forEach(
        prepaymentAccountModel -> {
          prepaidExpendRecordModel.setPatientId(billPayRecord.getPatientId());
          prepaidExpendRecordModel.setPrepaidId(prepaymentAccountModel.getPrepaymentNum());
          prepaidExpendRecordModel.setExpendTotal(prepaymentAccountModel.getAmount());
          prepaidExpendRecordModel.setTreatmentRecordId(billPayRecord.getTreatmentRecordId());
          prepaidExpendRecordModel.setOrderRecordId(billPayRecord.getOrderRecordId());
          prepaidExpendRecordModel.setBillRecordId(billPayRecord.getBillRecordId());
          prepaidExpendRecordModel.setBillPayRecordId(billPayRecord.getId());
          prepaidExpendRecordModel.setPrincipalAmount(prepaidExpendRecordModel.getPrincipalAmount());
          prepaidExpendRecordModel.setBonusAmount(prepaymentAccountModel.getBonusAmount());
          ResponseResult result = remotePatientCentralServiceFeign.expend(prepaidExpendRecordModel);
          if (!result.getStatus().equals(0)) {
            throw new ClientServiceException(result.getMsg(), result.hashCode());
          }
        });
  }

  /**
   * 保存优惠明细
   *
   * @param discountType 优惠类型
   * @param patientId 患者ID
   * @param orderRecordId 订单记录ID
   * @param model 收费添加模型
   */
  private void savePrivilegeDetail(
      Byte discountType,
      Integer patientId,
      Integer orderRecordId,
      TreatTollDebtModel model) {
    switch (discountType) {
      case 1:
        GeneralDiscountModel generalDiscount = model.getGeneralDiscountModel();
        if (StringHelper.isNotNull(generalDiscount)) {
          saveCouponPrivilege(patientId, orderRecordId, generalDiscount);
        }
        break;
      default:
        break;
    }
  }

  /**
   * 保存优惠券使用优惠明细
   *
   * @param patientId 患者ID
   * @param orderRecordId 订单记录ID
   * @param generalDiscount 优惠列表
   */
  private void saveCouponPrivilege(
      Integer patientId, Integer orderRecordId, GeneralDiscountModel generalDiscount) {
    PatientOrderBenefitModel benefitModel = new PatientOrderBenefitModel();
    benefitModel.setOrderId(orderRecordId);
    benefitModel.setPatientId(patientId);
    benefitModel.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
    benefitModel.setMemberCardId(generalDiscount.getMemberTypeId());
    benefitModel.setDiscountId(generalDiscount.getDiscountCouponId());
    List<CouponDiscountInfoModel> coupons = generalDiscount.getCouponDiscountInfoModels();
    List<Integer> voucherIds = Lists.newArrayList();
    List<Integer> exchangeIds = Lists.newArrayList();
    List<Integer> packageIds = Lists.newArrayList();
    setCouponListValue(coupons, voucherIds, exchangeIds, packageIds);
    benefitModel.setExchangeIds(exchangeIds);
    benefitModel.setPackageIds(packageIds);
    benefitModel.setVoucherIds(voucherIds);
    discountFeign.saveCardBenefit(benefitModel);
  }

  /**
   * 收欠费
   *
   * @param model 收费参数
   */
  @Transactional
  public TollConfirmVO collectDebt(TreatTollDebtModel model) {
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
    BigDecimal totalCharge = calculateTotalCharge(model);
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

    asyncProcessCharge(billPayRecord, model, totalCharge, false);
    asyncPushWxExpendMsg(billPayRecord);
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
   * 更新订单明细收费记录
   *
   * @param orderRecordId 开单记录ID
   * @param totalCharge 总入账金额
   */
  private void updateOrderDetailPayRecordWithPrivilege(
      Integer orderRecordId, BigDecimal totalCharge) {
    OrderDetailPayRecord orderDetailPayRecord = new OrderDetailPayRecord();
    orderDetailPayRecord.setOrderRecordId(orderRecordId);
    orderDetailPayRecord.setInservice(true);
    List<OrderDetailPayRecord> detailPayRecords =
        orderDetailPayRecordBiz.selectList(orderDetailPayRecord);
    // 根据订单号查询优惠列表
    List<OrderBenefitDetailVo> orderBenefitD = discountFeign.getOrderBenefitD(orderRecordId);
    log.info(
        "↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓订单号查询优惠列表[orderBenefitD]↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    orderBenefitD.forEach(
        orderBenefitDetailVo -> {
          log.info("==> {}", orderBenefitDetailVo);
        });
    log.info("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");

    log.info(
        "↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓订单明细列表[detailPayRecords]↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
    detailPayRecords.forEach(
        orderDetailPayRecord1 -> {
          log.info("==> {}", orderDetailPayRecord1);
        });
    log.info("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
    for (OrderDetailPayRecord detail : detailPayRecords) {
      BigDecimal receivableAmount = detail.getReceivableAmount();
      BigDecimal privilegeAmount = BigDecimal.valueOf(0);
      BigDecimal actualAmount = receivableAmount;
      for (OrderBenefitDetailVo vo : orderBenefitD) {
        Integer orderDetailId = vo.getOrderDetailId();
        if (detail.getOrderDetailId().equals(orderDetailId)) {
          privilegeAmount = vo.getItemBenefitAmount();
          actualAmount = receivableAmount.subtract(privilegeAmount);
          if (BigDecimal.ZERO.compareTo(actualAmount) > 0) {
            actualAmount = BigDecimal.ZERO;
            privilegeAmount = receivableAmount;
          }
          // 补入工作量
          detail.setCouponWorkload(vo.getSupplyWorkload());
        }
      }
      detail.setPrivilegeAmount(privilegeAmount);
      detail.setActualReceivable(actualAmount);
      // 设置已收
      totalCharge = totalItemCharge(totalCharge, detail);

      Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
      String name = BaseContextHandler.getName();
      detail.setUpdId(userId);
      detail.setUpdName(name);

      log.info(
          "↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓订单明细列表[detailPayRecords]↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
      detailPayRecords.forEach(
          orderDetailPayRecord1 -> {
            log.info("==> {}", orderDetailPayRecord1);
          });
      log.info(
          "↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
      orderDetailPayRecordBiz.updateSelectiveById(detail);
    }
  }

  /**
   * 校验账单是否允许收欠费
   *
   * @param model 收欠费添加模型
   */
  private BillRecord checkCollectDebtParams(
          TreatTollDebtModel model) {
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
}
