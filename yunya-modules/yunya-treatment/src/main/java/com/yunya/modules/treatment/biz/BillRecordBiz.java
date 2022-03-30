package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.vo.ItemUseBenefitVo;
import com.yunya.feign.discount.domain.vo.OrderBenefitDetailVo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.model.MemberBillRechargeModel;
import com.yunya.feign.patient_central.domain.model.PrepaidBillRechargeModel;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.domain.query.BillOfReceivableQuery;
import com.yunya.feign.report.domain.query.StatementStatisticQuery;
import com.yunya.feign.report.domain.vo.BillRestReceivableAmountVO;
import com.yunya.feign.report.domain.vo.CurrentMonthBillStatisticVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.model.*;
import com.yunya.feign.treatment.domain.query.CompletedWorkGoalQuery;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.PageUtl;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.treatment.*;
import com.yunya.modules.treatment.mapper.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseBill;
import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseRefund;
import static com.yunya.framework.common.constant.BusinessConstants.FREE_PAYMENT_ID;
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
  /** 账单 */
  @Autowired private OrderRecordMapper orderRecordMapper;
  /** 缓存 */
  @Autowired private RedisUtils redisUtils;
  /** 账单收费记录 */
  @Autowired private BillPayRecordBiz billPayRecordBiz;

  /**
   * 生成账单编号
   *
   * @param orgId 组织ID
   * @return String 账单编号
   */
  public synchronized String generateBillNumber(Integer orgId) {
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
    BillRecord billRecord = new BillRecord();
    billRecord.setOrderRecordId(orderRecordId);
    billRecord.setInservice(true);
    BillRecord billRecordAll = mapper.selectOne(billRecord);

    OrderRecord orderRecord = orderRecordMapper.selectByPrimaryKey(orderRecordId);
    if (orderRecord == null) {
      throw new ClientServiceException("订单记录不存在", PARAMETERS_IS_ILLEGAL);
    }
    BillDetailGroupVO resultData = new BillDetailGroupVO();
    // 获取开单优惠详情
    List<OrderDetailChargeVO> orderDetails = getOrderDetailCharges(orderRecordId);
    resultData.setOrderDetails(orderDetails);
    // 账单收费记录
    List<BillPayRecordVO> billPayRecords = mapper.selectBillPayRecord(orderRecordId);
    if (StringHelper.isNotEmpty(billPayRecords)) {
      billPayRecords.forEach(
          billPayRecord -> {
            Integer orgId = billPayRecord.getOrgId();
            // 从缓存中查询诊所信息
            OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
            if (null != orgInfo) {
              billPayRecord.setOrgName(orgInfo.getAbbreviation());
            }
            Integer billPayRecordId = billPayRecord.getBillPayRecordId();
            List<BillPayDetailRecordVO> billPayDetailRecords =
                billPayDetailRecordBiz.findBillPayDetailRecordByBillPayRecordId(billPayRecordId);
            billPayRecord.setBillPayDetailRecords(billPayDetailRecords);
            billPayRecord.setBillNumber(billRecordAll.getBillNumber());
          });
    } else {
      billPayRecords = new ArrayList<>();
    }
    resultData.setBillPayRecords(billPayRecords);
    // 账单异常记录
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
              // 从缓存中查询诊所信息
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
              if (BigDecimal.ZERO.compareTo(actualAmount) > 0) {
                actualAmount = BigDecimal.valueOf(0);
              }
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
        orderRecordId,
        billRefundRecordId,
        memberRefundModel,
        prepaymentRefundModel,
        refundPaymentModels);
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
   * @param orderRecordId 订单记录ID
   * @param billRefundRecordId 退费记录ID
   * @param memberRefundModel 会员卡退费
   * @param prepaymentRefundModel 预付款踢飞
   * @param refundPaymentModels 其他方式退费
   */
  private void saveBillRefundPayDetailRecord(
      Integer orderRecordId,
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
      memberModel.setOrderRecordId(orderRecordId);
      memberModel.setMemberId(memberNum);
      memberModel.setRechargePrincipal(principalAmount);
      memberModel.setRechargeBonus(giftAmount);
      memberModel.setBillPayRecordId(billRefundRecordId);
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
      prepaidModel.setOrderRecordId(orderRecordId);
      prepaidModel.setPrepaidId(prepaymentNum);
      prepaidModel.setRechargePrincipal(principalAmount);
      prepaidModel.setRechargeBonus(giftAmount);
      prepaidModel.setBillPayRecordId(billRefundRecordId);
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
   * 校验账单能否退费
   *
   * @param treatmentRecordId 就诊记录ID
   * @param orgId 组织ID
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
    return mapper.selectPatientBillStatistics(patientId, FREE_PAYMENT_ID);
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

  /**
   * 患者档案-账单详情-编辑备注提交
   *
   * @param orderDetails 账单明细
   * @return Integer
   */
  public ResponseResult editRemarks(List<OrderDetailChargeVO> orderDetails) {
    if (StringHelper.isEmpty(orderDetails)) {
      return ResponseUtil.success("账单详情不能为空！", null);
    }
    String userID = BaseContextHandler.getUserID();
    String name = BaseContextHandler.getName();
    OrderDetailChargeVO vo = orderDetails.get(0);
    orderDetails.forEach(
        entity -> {
          String remarks = entity.getRemarks();
          OrderDetail orderDetail = new OrderDetail();
          orderDetail.setId(entity.getOrderDetailId());
          orderDetail.setRemarks(remarks);
          orderDetail.setUpdId(Integer.valueOf(userID));
          orderDetail.setUpdTime(new Date(System.currentTimeMillis()));
          orderDetail.setUptName(name);
          orderDetailBiz.updateOrderDetail(orderDetail);
        });
    OrderDetail orderDetail = orderDetailBiz.selectById(vo.getOrderDetailId());
    if (!ObjectUtils.isEmpty(orderDetail)) {
      rabbitMqServiceFeign.sendMessage(orderDetail.getOrderRecordId(), 1, BaseBill);
    }
    return ResponseUtil.success();
  }

  /**
   * 根据条件查询门诊应收账款余额表
   *
   * @param query 查询条件
   * @return list
   */
  public PageInfo<BillRestReceivableAmountVO> findDebtList(BillOfReceivableQuery query) {
    // 查询时间节点前欠费患者列表
    List<BillRestReceivableAmountVO> resultList = mapper.selectDebtList(query);
    // 查询时间节点后门诊被调整的应收账款余额列表
    List<BillRestReceivableAmountVO> adjustedList =
        billExceptionHandleRecordMapper.selectFollowUpBillAdjustList(query);
    resultList.addAll(adjustedList);
    // 查询时间节点后的撤销收费ID列表
    List<BillRestReceivableAmountVO> revokes =
        billExceptionHandleRecordMapper.selectBeforeRevokeBillPayIds(query);
    resultList.forEach(
        vo -> {
          Integer orgId = vo.getOrgId();
          Integer billId = vo.getBillId();
          BigDecimal billReceivableAmount = vo.getBillReceivableAmount();
          revokes.forEach(
              revoke -> {
                if (revoke.getBillId().equals(billId) && revoke.getOrgId().equals(orgId)) {
                  vo.setBillReceivableAmount(
                      billReceivableAmount.subtract(revoke.getBillReceivableAmount()));
                }
              });
        });
    resultList =
        resultList.stream()
            .filter(vo -> vo.getBillReceivableAmount().compareTo(BigDecimal.ZERO) > 0)
            //            .filter(vo-> DateUtil.compareDate(vo.getBillDate(),"2017-10-01")>=0)
            .sorted(
                (vo1, vo2) -> {
                  int result = DateUtil.compareDate(vo2.getBillDate(), vo1.getBillDate());
                  if (result == 0) {
                    result =
                        vo2.getBillReceivableAmount()
                            .subtract(vo1.getBillReceivableAmount())
                            .intValue();
                  }
                  return result;
                })
            .collect(Collectors.toList());
    PageInfo<BillRestReceivableAmountVO> pageInfo = new PageInfo<>(resultList);
    if (query.getWhetherPage()) {
      pageInfo = PageUtl.doPage(query.getPageNum(), query.getPageSize(), resultList);
    }
    findPatientAndRegDetist(pageInfo);
    return pageInfo;
  }

  /**
   * 查找患者信息和挂号医生信息
   *
   * @param pageInfo
   */
  private void findPatientAndRegDetist(PageInfo<BillRestReceivableAmountVO> pageInfo) {
    List<BillRestReceivableAmountVO> result = pageInfo.getList();
    List<Integer> patientIds =
        result.stream().map(BillRestReceivableAmountVO::getPatientId).collect(Collectors.toList());
    List<PatientBaseInfoVo> patients = patientCentralServiceFeign.findPatientInfoByIds(patientIds);
    Map<Integer, PatientBaseInfoVo> patientMap =
        patients.stream().collect(Collectors.toMap(PatientBaseInfoVo::getId, Function.identity()));
    result.forEach(
        vo -> {
          Integer patientId = vo.getPatientId();
          PatientBaseInfoVo patient = patientMap.get(patientId);
          if (patient != null) {
            vo.setPatientName(patient.getName());
            vo.setMobile(patient.getMobile());
          }
        });
    List<Integer> userIds =
        result.stream()
            .map(BillRestReceivableAmountVO::getRegDentistId)
            .collect(Collectors.toList());
    List<SysUserInfoDetail> users = systemServiceFeign.findSysUserEmployeeInfoByUserIds(userIds);
    Map<Integer, String> userMap =
        users.stream()
            .collect(Collectors.toMap(SysUserInfoDetail::getUserId, SysUserInfoDetail::getName));
    result.forEach(
        vo -> {
          Integer regDentistId = vo.getRegDentistId();
          vo.setRegDentistName(userMap.get(regDentistId));
          OrganizationInfo org = systemServiceFeign.findOrgInfoByOrgId(vo.getOrgId());
          if (!ObjectUtils.isEmpty(org)) {
            vo.setAbbreviation(org.getAbbreviation());
          }
        });
  }

  /**
   * 根据条件导出门诊应收账款余额表
   *
   * @param query 查询条件
   */
  public void exportDebtList(BillOfReceivableQuery query, HttpServletResponse response)
      throws IOException {
    query.setWhetherPage(false);
    List<BillRestReceivableAmountVO> resultList = findDebtList(query).getList();
    ExcelUtil<BillRestReceivableAmountVO> excelUtil =
        new ExcelUtil<>(BillRestReceivableAmountVO.class);
    String fileName = query.getQueryDate() + "应收款余额表";
    List<Integer> orgIds = query.getOrgIds();
    if (orgIds.size() == 1) {
      OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgIds.get(0));
      if (null != orgInfo) {
        fileName = orgInfo.getAbbreviation() + fileName;
      }
    }
    excelUtil.exportExcel(response, resultList, "应收款余额表", fileName);
  }

  /**
   * 根据条件查询本月金额合计
   *
   * @param query 查询条件
   * @return list
   */
  public CurrentMonthBillStatisticVO findCurrentMonthStatementStatistic(
      StatementStatisticQuery query) {
    CurrentMonthBillStatisticVO result = new CurrentMonthBillStatisticVO();
    // 撤销优惠合计
    BigDecimal adjustDiscountAmount = mapper.selectBillAdjustDiscountAmount(query);
    // 优惠合计
    BigDecimal discountAmount = mapper.selectBillDiscountAmount(query);
    discountAmount = discountAmount.subtract(adjustDiscountAmount);
    // 调整原价合计
    BigDecimal adjustOriginalAmount = mapper.selectBillAdjustOriginalAmount(query);
    // 原价合计
    BigDecimal originalAmount = mapper.selectBillOriginalAmount(query);
    originalAmount = originalAmount.subtract(adjustOriginalAmount).subtract(discountAmount);
    // 撤销实收合计
    BigDecimal revokeReceivedAmount = mapper.selectBillRevokeReceivedAmount(query);
    // 实收合计
    BigDecimal receivedAmount = mapper.selectBillReceivedAmount(query);
    // 免单合计
    BigDecimal freePayAmount = mapper.selectBillFreePayAmount(query);
    // 撤销收费记录ID
    List<Integer> payIds = billPayRecordBiz.selectRevokePayIds(query);
    // 调整收费方式
    payIds.addAll(billPayRecordBiz.selectAdjustPayIds(query));
    if (StringHelper.isNotEmpty(payIds)) {
      // 撤销或调整免单合计
      BigDecimal subtractFreePayAmount =
          billPayDetailRecordBiz.selectDeductionFreePayAmount(payIds);
      freePayAmount = freePayAmount.add(subtractFreePayAmount);
    }
    receivedAmount = receivedAmount.subtract(revokeReceivedAmount);
    result.setCurrentMonth(query.getQueryDate());
    result.setOrgId(query.getOrgId());
    result.setCurrentMonthTotalActualAmount(originalAmount);
    result.setCurrentMonthTotalDiscountAmount(discountAmount);
    result.setCurrentMonthTotalReceivedAmount(receivedAmount);
    result.setCurrentMonthTotalFreePayAmount(freePayAmount);
    result.setCurrentMonthTotalDebtAmount(originalAmount.subtract(receivedAmount));
    return result;
  }

  public void insertBillRecord(BillRecord billRecord) {
    mapper.insertSelective(billRecord);
  }
}
