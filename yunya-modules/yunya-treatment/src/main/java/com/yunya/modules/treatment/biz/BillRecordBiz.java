package com.yunya.modules.treatment.biz;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.vo.ItemUseBenefitVo;
import com.yunya.feign.discount.domain.vo.OrderBenefitDetailVo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.model.MemberBillRechargeModel;
import com.yunya.feign.patient_central.domain.model.PrepaidBillRechargeModel;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.domain.model.*;
import com.yunya.feign.treatment.domain.query.CompletedWorkGoalQuery;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.*;
import com.yunya.modules.treatment.mapper.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseRefund;
import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_NOT_EXIST;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 简介: 账单记录业务层
 *
 * @author: chow
 * @date: 2020/8/27 20:43
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillRecordBiz extends BaseBiz<BillRecordMapper, BillRecord> {

  /** 消息中间件调用 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 患者服务调用 */
  @Autowired private RemotePatientCentralServiceFeign patientCentralServiceFeign;
  /** 优惠服务调用 */
  @Autowired private RemoteDiscountFeign discountFeign;
  /** 开单详情 */
  @Autowired private OrderDetailBiz orderDetailBiz;
  /** 账单支付明细 */
  @Autowired private BillPayDetailRecordBiz billPayDetailRecordBiz;
  /** 账单明细收费记录 */
  @Autowired private OrderDetailPayRecordMapper orderDetailPayRecordMapper;
  /** 账单异常处理记录 */
  @Autowired private BillExceptionHandleRecordMapper billExceptionHandleRecordMapper;
  /** 账单异常处理详情记录 */
  @Autowired private BillExceptionHandleDetailRecordMapper billExceptionHandleDetailRecordMapper;
  /** 账单退费记录 */
  @Autowired private BillRefundRecordMapper billRefundRecordMapper;
  /** 账单退费开单明细 */
  @Autowired private BillRefundOrderDetailMapper billRefundOrderDetailMapper;
  /** 账单退费付款明细记录 */
  @Autowired private BillRefundPayDetailRecordMapper billRefundPayDetailRecordMapper;
  /** 商品明细表 */
  @Autowired private BaseOralTariffBiz baseOralTariffBiz;
  /** 价目明细表 */
  @Autowired private BaseTariffBiz baseTariffBiz;
  /** 就诊 */
  @Autowired private TreatmentRecordMapper treatmentRecordMapper;
  /* 账单 */
  @Autowired private OrderRecordMapper orderRecordMapper;

  /**
   * 生成账单编号
   *
   * @param orgId 组织ID
   * @return String 账单编号
   */
  public String generateBillNumber(Integer orgId) {
    String number = mapper.selectBillNumberByOrgId(orgId, new Date(System.currentTimeMillis()));
    String suffix = String.format("%04d", Integer.parseInt(number) + 1);
    return String.format(
        "ZD%s%s%s", String.format("%04d", orgId), new DateTime().toString("yyMMdd"), suffix);
  }

  /**
   * 根据就诊记录查询账单详情信息
   *
   * @param orderRecordId 就诊记录ID
   * @return resultData 账单详情信息
   */
  public BillDetailGroupVO findOrderDetailAndBillDetail(Integer orderRecordId) {
    BillDetailGroupVO resultData = new BillDetailGroupVO();
    // 获取开单优惠详情
    List<OrderDetailChargeVO> orderDetails = getOrderDetailCharges(orderRecordId);
    resultData.setOrderDetails(orderDetails);

    List<BillPayRecordVO> billPayRecords = mapper.selectBillPayRecord(orderRecordId);
    if (StringHelper.isNotEmpty(billPayRecords)) {
      billPayRecords.forEach(
          billPayRecord -> {
            Integer orgId = billPayRecord.getOrgId();
            // todo 从缓存中查询诊所信息
            OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
            if (null != orgInfo) {
              billPayRecord.setOrgName(orgInfo.getAbbreviation());
            }
            Integer billPayRecordId = billPayRecord.getBillPayRecordId();
            List<BillPayDetailRecordVO> billPayDetailRecords =
                billPayDetailRecordBiz.findBillPayDetailRecordByBillPayRecordId(billPayRecordId);
            billPayRecord.setBillPayDetailRecords(billPayDetailRecords);
          });
    } else {
      billPayRecords = new ArrayList<>();
    }
    resultData.setBillPayRecords(billPayRecords);
    // 账单异常记录
    /*BillRecord entity = new BillRecord();
    entity.setOrderRecordId(orderRecordId);
    entity.setInservice(true);
    BillRecord billRecord = mapper.selectOne(entity);*/
    OrderRecord orderRecord = orderRecordMapper.selectByPrimaryKey(orderRecordId);
    if (orderRecord == null) {
      throw new ClientServiceException("开单记录不存在", PARAMETERS_IS_ILLEGAL);
    }
    Integer treatmentRecordId = orderRecord.getTreatmentRecordId();
    BillRecord entity = new BillRecord();
    entity.setTreatmentRecordId(treatmentRecordId);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      List<BillHandleRecordVO> billHandleRecords =
          billExceptionHandleRecordMapper.selectBillExceptionHandleRecord(treatmentRecordId);
      if (StringHelper.isNotEmpty(billHandleRecords)) {
        billHandleRecords.forEach(
            handleRecord -> {
              Integer orgId = handleRecord.getOrgId();
              // todo 从缓存中查询诊所信息
              OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
              if (null != orgInfo) {
                handleRecord.setOrgName(orgInfo.getAbbreviation());
              }
            });
      } else {
        billHandleRecords = new ArrayList<>();
      }
      resultData.setBillHandleRecords(billHandleRecords);
    }
    return resultData;
  }

  /**
   * 获取账单开单优惠详情
   *
   * @param orderRecordId 开单详情
   * @return
   */
  public List<OrderDetailChargeVO> getOrderDetailCharges(Integer orderRecordId) {
    List<OrderDetailChargeVO> orderDetails = orderDetailBiz.getChargeOrderDetailList(orderRecordId);
    if (StringHelper.isEmpty(orderDetails)) {
      orderDetails = new ArrayList<>();
    } else {
      List<OrderBenefitDetailVo> orderBenefitD = discountFeign.getOrderBenefitD(orderRecordId);
      if (StringHelper.isNotEmpty(orderBenefitD)) {
        for (OrderDetailChargeVO orderDetail : orderDetails) {
          for (OrderBenefitDetailVo benefitDetailVo : orderBenefitD) {
            if (orderDetail.getOrderDetailId().equals(benefitDetailVo.getOrderDetailId())) {
              BigDecimal receivableAmount = orderDetail.getReceivableAmount();
              BigDecimal actualAmount = orderDetail.getActualAmount();
              BigDecimal itemBenefitAmount = benefitDetailVo.getItemBenefitAmount();
              actualAmount = actualAmount.subtract(itemBenefitAmount);
              orderDetail.setActualAmount(actualAmount);
              if (receivableAmount.compareTo(new BigDecimal(0)) != 0) {
                orderDetail.setDiscountRate(
                    actualAmount
                        .divide(receivableAmount, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)));
              }
              List<ItemUseBenefitVo> benefitList = benefitDetailVo.getItemBenefitList();
              setPrivilegeCouponInfo(orderDetail, benefitList);
            }
          }
        }
      }
    }
    return orderDetails;
  }

  /**
   * 设置订单优惠信息
   *
   * @param orderDetail 订单详情
   * @param benefitList 优惠信息
   */
  private void setPrivilegeCouponInfo(
      OrderDetailChargeVO orderDetail, List<ItemUseBenefitVo> benefitList) {
    List<PrivilegeCouponInfoVO> couponInfo = Lists.newArrayList();
    if (StringHelper.isNotEmpty(benefitList)) {
      benefitList.forEach(
          vo -> {
            PrivilegeCouponInfoVO couponInfoVO = new PrivilegeCouponInfoVO();
            couponInfoVO.setBenefitId(vo.getBenefitId());
            Integer benefitType = vo.getBenefitType();
            switch (benefitType) {
              case 0:
                couponInfoVO.setCouponType(99);
                break;
              case 1:
                couponInfoVO.setCouponType(vo.getCouponType());
                break;
              case 2:
                couponInfoVO.setCouponType(5);
                break;
              default:
                break;
            }
            couponInfoVO.setBenefitName(vo.getBenefitName());
            couponInfoVO.setBenefitAmount(vo.getBenefitAmount());
            couponInfo.add(couponInfoVO);
          });
      orderDetail.setDiscountAppliesCoupons(couponInfo);
    }
  }

  /**
   * 账单退费
   *
   * @param model 退费参数
   */
  public void refund(BillRefundModel model) {
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer treatmentRecordId = model.getTreatmentRecordId();
    // 检查就诊是否收费
    BillRecord billRecord = checkBillRecord(treatmentRecordId, orgId);
    List<RefundOrderDetailModel> refundOrderDetailModels = model.getRefundOrderDetailModels();
    MemberRefundModel memberRefundModel = model.getMemberRefundModel();
    PrepaymentRefundModel prepaymentRefundModel = model.getPrepaymentRefundModel();
    List<PaymentModel> refundPaymentModels = model.getRefundPaymentModels();
    String refundReason = model.getRefundReason();
    List<String> refundAnnex = model.getRefundAnnex();
    Integer billRecordId = billRecord.getId();
    Integer patientId = billRecord.getPatientId();
    Integer orderRecordId = billRecord.getOrderRecordId();
    // 计算退费开单总额
    BigDecimal refundOrderDetailAmount = calculateRefundOrderDetailAmount(refundOrderDetailModels);
    // 计算退费总额
    BigDecimal refundTotalAmount =
        calculateRefundAmount(memberRefundModel, prepaymentRefundModel, refundPaymentModels);
    if (refundOrderDetailAmount.compareTo(refundTotalAmount) != 0) {
      throw new ClientServiceException("账单退费失败，退费总额与退费项目金额总和不相等！", PARAMETERS_IS_ILLEGAL);
    }
    String name = BaseContextHandler.getName();
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    // 保存退费记录
    BillRefundRecord billRefundRecord = new BillRefundRecord();
    billRefundRecord.setPatientId(patientId);
    billRefundRecord.setOrgId(orgId);
    billRefundRecord.setTreatmentRecordId(treatmentRecordId);
    billRefundRecord.setOrderRecordId(orderRecordId);
    billRefundRecord.setReason(refundReason);
    billRefundRecord.setTotalRefundAmount(refundTotalAmount);
    Joiner joiner = Joiner.on(",");
    if (StringHelper.isNotEmpty(refundAnnex)) {
      billRefundRecord.setRefundCertificate(joiner.join(refundAnnex));
    }
    billRefundRecord.setCrtId(userId);
    billRefundRecord.setCrtName(name);
    int result = billRefundRecordMapper.insertSelective(billRefundRecord);
    // 保存退费开单明细
    Integer billRefundRecordId = billRefundRecord.getId();
    BillRefundOrderDetail refundOrderDetail = new BillRefundOrderDetail();
    refundOrderDetail.setCrtId(userId);
    refundOrderDetail.setCrtName(name);
    refundOrderDetail.setOrgId(orgId);
    refundOrderDetailModels.forEach(
        detailModel -> {
          refundOrderDetail.setOrderDetailId(detailModel.getOrderDetailId());
          refundOrderDetail.setRefundAmount(detailModel.getRefundAmount());
          refundOrderDetail.setBillRefundRecordId(billRefundRecordId);
          billRefundOrderDetailMapper.insertSelective(refundOrderDetail);
        });
    // 保存账单退费付款明细记录
    saveBillRefundPayDetailRecord(
        billRefundRecordId, memberRefundModel, prepaymentRefundModel, refundPaymentModels);
    // 保存账单退费异常处理记录
    BillExceptionHandleRecord exceptionHandleRecord = new BillExceptionHandleRecord();
    exceptionHandleRecord.setOrgId(orgId);
    exceptionHandleRecord.setPatientId(patientId);
    exceptionHandleRecord.setTreatmentRecordId(treatmentRecordId);
    exceptionHandleRecord.setHandledRecordId(billRecordId);
    exceptionHandleRecord.setOperateType((byte) 3);
    exceptionHandleRecord.setCrtId(userId);
    exceptionHandleRecord.setCrtName(name);
    billExceptionHandleRecordMapper.insertSelective(exceptionHandleRecord);
    // 保存账单退费异常处理明细
    Integer exceptionHandleRecordId = exceptionHandleRecord.getId();
    BillExceptionHandleDetailRecord handleDetailRecord = new BillExceptionHandleDetailRecord();
    handleDetailRecord.setBillHandleRecordId(exceptionHandleRecordId);
    handleDetailRecord.setAssociateRecordId(billRefundRecordId);
    handleDetailRecord.setCrtId(userId);
    handleDetailRecord.setCrtName(name);
    billExceptionHandleDetailRecordMapper.insertSelective(handleDetailRecord);
    // 发送消息同步退费
    if (result > 0) {
      rabbitMqServiceFeign.sendMessage(billRefundRecordId, 0, BaseRefund);
    }
  }

  /**
   * 保存账单退费付款明细记录
   *
   * @param billRefundRecordId 退费记录ID
   * @param memberRefundModel 会员卡退费
   * @param prepaymentRefundModel 预付款踢飞
   * @param refundPaymentModels 其他方式退费
   */
  private void saveBillRefundPayDetailRecord(
      Integer billRefundRecordId,
      MemberRefundModel memberRefundModel,
      PrepaymentRefundModel prepaymentRefundModel,
      List<PaymentModel> refundPaymentModels) {
    BillRefundPayDetailRecord refundPayDetailRecord = new BillRefundPayDetailRecord();
    refundPayDetailRecord.setBillRefundRecordId(billRefundRecordId);
    refundPayDetailRecord.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    refundPayDetailRecord.setCrtName(BaseContextHandler.getName());
    // 会员费退费
    if (null != memberRefundModel) {
      refundPayDetailRecord.setAccountItemId(memberRefundModel.getAccountItemId());
      String memberNum = memberRefundModel.getMemberAccountId();
      refundPayDetailRecord.setRemark(memberNum);
      BigDecimal principalAmount = memberRefundModel.getPrincipalAmount();
      BigDecimal giftAmount = memberRefundModel.getGiftAmount();
      if (principalAmount == null) {
        principalAmount = new BigDecimal(0);
      }
      if (giftAmount == null) {
        giftAmount = new BigDecimal(0);
      }
      refundPayDetailRecord.setRefundPayAmount(principalAmount.add(giftAmount));
      refundPayDetailRecord.setPrincipalAmount(principalAmount);
      refundPayDetailRecord.setGiftAmount(giftAmount);
      billRefundPayDetailRecordMapper.insertSelective(refundPayDetailRecord);
      // 会员卡退费金额返还
      MemberBillRechargeModel memberModel = new MemberBillRechargeModel();
      memberModel.setMemberId(memberNum);
      memberModel.setRechargePrincipal(principalAmount);
      memberModel.setRechargeBonus(giftAmount);
      memberModel.setRemarks(billRefundRecordId.toString());
      patientCentralServiceFeign.billRefund(memberModel);
    }
    // 预付款退费
    if (null != prepaymentRefundModel) {
      refundPayDetailRecord.setAccountItemId(prepaymentRefundModel.getAccountItemId());
      String prepaymentNum = prepaymentRefundModel.getPrepaymentAccountId();
      refundPayDetailRecord.setRemark(prepaymentNum);
      BigDecimal principalAmount = prepaymentRefundModel.getPrincipalAmount();
      BigDecimal giftAmount = prepaymentRefundModel.getGiftAmount();
      if (principalAmount == null) {
        principalAmount = new BigDecimal(0);
      }
      if (giftAmount == null) {
        giftAmount = new BigDecimal(0);
      }
      refundPayDetailRecord.setRefundPayAmount(principalAmount.add(giftAmount));
      refundPayDetailRecord.setPrincipalAmount(principalAmount);
      refundPayDetailRecord.setGiftAmount(giftAmount);
      billRefundPayDetailRecordMapper.insertSelective(refundPayDetailRecord);
      // 预付款退费金额返还
      PrepaidBillRechargeModel prepaidModel = new PrepaidBillRechargeModel();
      prepaidModel.setPrepaidId(prepaymentNum);
      prepaidModel.setRechargePrincipal(principalAmount);
      prepaidModel.setRechargeBonus(giftAmount);
      prepaidModel.setRemarks(billRefundRecordId.toString());
      patientCentralServiceFeign.billRefund(prepaidModel);
    }
    // 其它方式退款
    if (StringHelper.isNotEmpty(refundPaymentModels)) {
      refundPaymentModels.forEach(
          paymentModel -> {
            refundPayDetailRecord.setAccountItemId(paymentModel.getAccountItemId());
            refundPayDetailRecord.setRemark(paymentModel.getAccountItemId().toString());
            BigDecimal amount = paymentModel.getAmount();
            refundPayDetailRecord.setRefundPayAmount(amount);
            refundPayDetailRecord.setPrincipalAmount(amount);
            refundPayDetailRecord.setGiftAmount(null);
            billRefundPayDetailRecordMapper.insertSelective(refundPayDetailRecord);
          });
    }
  }

  /**
   * 根据就诊是否是否
   *
   * @param treatmentRecordId 就诊记录ID
   * @param orgId
   * @return
   */
  private BillRecord checkBillRecord(Integer treatmentRecordId, Integer orgId) {
    BillRecord entity = new BillRecord();
    entity.setTreatmentRecordId(treatmentRecordId);
    entity.setInservice(true);
    BillRecord billRecord = mapper.selectOne(entity);
    if (null == billRecord) {
      throw new ClientServiceException("账单退费失败，当前就诊账单已修改或未收费！", DATA_NOT_EXIST);
    }
    if (!billRecord.getOrgId().equals(orgId)) {
      throw new ClientServiceException("账单退费失败，只能对本门诊账单退费！", DATA_NOT_EXIST);
    }
    return billRecord;
  }

  /**
   * 计算退费订单明细总额
   *
   * @param refundOrderDetailModels 退费订单明细列表
   * @return refundOrderDetailAmount 退费订单明细总额
   */
  private BigDecimal calculateRefundOrderDetailAmount(
      List<RefundOrderDetailModel> refundOrderDetailModels) {
    BigDecimal refundOrderDetailAmount = BigDecimal.valueOf(0);
    if (StringHelper.isNotEmpty(refundOrderDetailModels)) {
      OrderDetailPayRecord orderDetailPayrecord = new OrderDetailPayRecord();
      for (RefundOrderDetailModel model : refundOrderDetailModels) {
        Integer orderDetailId = model.getOrderDetailId();
        OrderDetail orderDetail = orderDetailBiz.selectById(orderDetailId);
        if (null == orderDetail) {
          throw new ClientServiceException("账单退费失败，请选择正确的订单明细进行操作！", PARAMETERS_IS_ILLEGAL);
        }
        // 比较退费金额与订单入账金额
        orderDetailPayrecord.setOrderDetailId(orderDetailId);
        orderDetailPayrecord.setInservice(true);
        OrderDetailPayRecord detailPayRecord =
            orderDetailPayRecordMapper.selectOne(orderDetailPayrecord);
        BigDecimal refundAmount = model.getRefundAmount();
        if (null != detailPayRecord) {
          BigDecimal receivedAmount = detailPayRecord.getReceivedAmount();
          if (refundAmount.compareTo(receivedAmount) > 0) {
            throw new ClientServiceException("账单退费失败，项目退费金额不能超过该项目实收金额！", PARAMETERS_IS_ILLEGAL);
          }
        }
        refundOrderDetailAmount = refundOrderDetailAmount.add(refundAmount);
      }
    }
    return refundOrderDetailAmount;
  }

  /**
   * 计算退费总额
   *
   * @param memberAccountModel 会员退费
   * @param prepaymentAccountModel 预付款退费
   * @param refundPaymentModels 其他方式退费
   * @return totalAmount 退费总额
   */
  private BigDecimal calculateRefundAmount(
      MemberRefundModel memberAccountModel,
      PrepaymentRefundModel prepaymentAccountModel,
      List<PaymentModel> refundPaymentModels) {
    BigDecimal totalAmount = BigDecimal.valueOf(0);
    if (null != memberAccountModel) {
      BigDecimal principalAmount = memberAccountModel.getPrincipalAmount();
      if (null == principalAmount) {
        principalAmount = new BigDecimal(0);
      }
      BigDecimal giftAmount = memberAccountModel.getGiftAmount();
      if (null == giftAmount) {
        giftAmount = new BigDecimal(0);
      }
      totalAmount = totalAmount.add(principalAmount).add(giftAmount);
    }
    if (null != prepaymentAccountModel) {
      BigDecimal principalAmount = prepaymentAccountModel.getPrincipalAmount();
      BigDecimal giftAmount = prepaymentAccountModel.getGiftAmount();
      if (principalAmount == null) {
        principalAmount = new BigDecimal(0);
      }
      if (giftAmount == null) {
        giftAmount = new BigDecimal(0);
      }
      totalAmount = totalAmount.add(principalAmount).add(giftAmount);
    }
    if (StringHelper.isNotEmpty(refundPaymentModels)) {
      for (PaymentModel model : refundPaymentModels) {
        totalAmount = totalAmount.add(model.getAmount());
      }
    }
    return totalAmount;
  }

  /**
   * 通过患者ID批量查询患者欠费总额
   *
   * @param patientIds 患者ID
   * @return 返回患者欠费集合
   */
  public List<DebtAmountModel> selectDebtAmountList(List<Integer> patientIds) {
    return mapper.selectPatientDebtAmountList(patientIds);
  }

  /**
   * 根据患者ID查询患者账单消费信息
   *
   * @param patientId 患者ID
   * @return
   */
  public PatientBillStatistics statisticsBill(Integer patientId) {
    PatientBillStatistics patientBillStatistics = mapper.selectPatientBillStatistics(patientId);
    return patientBillStatistics;
  }

  /**
   * 根据就诊ID查询账单和订单信息
   *
   * @param treatmentId 就诊ID
   * @return 返回实体
   */
  public OrderBill4AppVO findOrderAndBill4App(Integer treatmentId) {
    OrderBill4AppVO orderBill4AppVO = mapper.findOrderAndBill4App(treatmentId);
    if (null != orderBill4AppVO) {
      orderBill4AppVO
          .getBillItems()
          .forEach(
              treatmentOrderInfo4AppVO -> {
                Integer type = treatmentOrderInfo4AppVO.getType();
                Integer billingItemId = treatmentOrderInfo4AppVO.getBillingItemId();
                if (type == 0) {
                  // 查询价目表
                  BaseTariffInfoVO baseTariffInfoById =
                      baseTariffBiz.findBaseTariffInfoById(billingItemId);
                  if (null != baseTariffInfoById) {
                    treatmentOrderInfo4AppVO.setBillingItemName(baseTariffInfoById.getName());
                  }
                } else if (type == 1) {
                  // 查询商品表
                  BaseOralTariffInfoVO baseOralTariffInfoById =
                      baseOralTariffBiz.findBaseOralTariffInfoById(billingItemId);
                  if (null != baseOralTariffInfoById) {
                    treatmentOrderInfo4AppVO.setBillingItemName(baseOralTariffInfoById.getName());
                  }
                }
              });
      return orderBill4AppVO;
    }
    return new OrderBill4AppVO();
  }

  /**
   * 查询完成的业务目标
   *
   * @param query 查询条件
   * @return CompletedBusinessWorkGoalVO
   */
  public BusinessCompletedWorkGoalVO findClinicCompletedBusinessWorkGoal(
      CompletedWorkGoalQuery query) {
    BusinessCompletedWorkGoalVO resultData = new BusinessCompletedWorkGoalVO();
    BigDecimal completedActualReceivedAmount = mapper.selectCompletedActualReceivedAmount(query);
    resultData.setActualReceivedAmountCompleted(completedActualReceivedAmount);
    BigDecimal completedWorkloadAmount = mapper.selectCompletedWorkloadAmount(query);
    resultData.setWorkloadAmountCompleted(completedWorkloadAmount);
    Integer completedFirstTreatPerNum =
        treatmentRecordMapper.selectCompletedFirstTreatPerNum(query);
    resultData.setFirstTreatPerNumCompleted(completedFirstTreatPerNum);
    Integer completedTreatPerTimes = treatmentRecordMapper.selectCompletedTreatPerTimes(query);
    resultData.setTreatPerTimesCompleted(completedTreatPerTimes);
    return resultData;
  }

  /**
   * 根据条件查询营业收入完成情况
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  public BigDecimal findBusinessIncomeCompletedCount(BusinessGoalCompletedInfoQuery query) {
    return mapper.selectBusinessIncomeCompletedCount(query);
  }
}
