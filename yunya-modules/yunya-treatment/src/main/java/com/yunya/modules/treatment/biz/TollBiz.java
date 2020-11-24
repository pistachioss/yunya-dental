package com.yunya.modules.treatment.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.model.AuthDiscountBenefitModel;
import com.yunya.feign.discount.domain.model.AuthItemBenefitModel;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.discount.domain.vo.ItemUseBenefitVo;
import com.yunya.feign.discount.domain.vo.PatientItemBenefitVo;
import com.yunya.feign.discount.domain.vo.PatientOrderBenefitVo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.model.MemberExpendRecordModel;
import com.yunya.feign.patient_central.domain.model.PrepaidExpendRecordModel;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.treatment.domain.model.AccreditDiscountDetailModel;
import com.yunya.feign.treatment.domain.model.AccreditDiscountModel;
import com.yunya.feign.treatment.domain.model.CouponDiscountInfoModel;
import com.yunya.feign.treatment.domain.model.GeneralDiscountModel;
import com.yunya.feign.treatment.domain.model.InvoiceModel;
import com.yunya.feign.treatment.domain.model.MemberAccountModel;
import com.yunya.feign.treatment.domain.model.PaymentModel;
import com.yunya.feign.treatment.domain.model.PrepaymentAccountModel;
import com.yunya.feign.treatment.domain.model.TollDebtModel;
import com.yunya.feign.treatment.domain.model.TollModel;
import com.yunya.feign.treatment.domain.query.OrderPrivilegeQuery;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.feign.treatment.domain.vo.PrivilegeCouponInfoVO;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment.BillPayDetailRecord;
import com.yunya.models.treatment.BillPayRecord;
import com.yunya.models.treatment.BillRecord;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderDetailPayRecord;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.mapper.TreatmentRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.*;

/**
 * 简介: 就诊收费业务层
 *
 * @author: chow
 * @date: 2020/8/21 20:53
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TollBiz {

  /** 缓存 */
  @Autowired private RedisUtils redisUtils;
  /** 系统关联服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 患者服务调用 */
  @Autowired private RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;
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
  @Autowired private BillPayRecordBiz billPayRecordBiz;
  /** 账单支付明细记录 */
  @Autowired private BillPayDetailRecordBiz billPayDetailRecordBiz;
  /** 就诊记录 */
  @Autowired private TreatmentRecordMapper treatmentRecordMapper;

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
    AccreditDiscountModel accreditDiscountModel = query.getAccreditDiscountModel();
    // 校验优惠参数
    checkPrivilegeParam(discountType, generalDiscountModel, accreditDiscountModel);
    switch (discountType) {
      case 1:
        // 匹配卡券优惠
        matchGeneralDiscountOrderDetailValue(orderRecordId, detailList, generalDiscountModel);
        break;
        // 匹配授权折扣
      case 2:
        matchAccreditDiscountOrderDetailValue(detailList, accreditDiscountModel);
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
    OrderRecord orderRecord = orderRecordBiz.selectById(orderRecordId);
    PatientChooseBenefitForm form = new PatientChooseBenefitForm();
    form.setPatientId(orderRecord.getPatientId());
    form.setOrderId(orderRecordId);
    form.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
    form.setMemberCardId(generalDiscountModel.getMemberTypeId());
    form.setDiscountId(generalDiscountModel.getDiscountCouponId());
    List<Integer> voucherIds = Lists.newArrayList();
    List<Integer> exchangeIds = Lists.newArrayList();
    List<Integer> packageIds = Lists.newArrayList();
    List<CouponDiscountInfoModel> discountInfoModels =
        generalDiscountModel.getCouponDiscountInfoModels();
    setCouponListValue(discountInfoModels, voucherIds, exchangeIds, packageIds);
    form.setVoucherIds(voucherIds);
    form.setExchangeIds(exchangeIds);
    form.setPackageIds(packageIds);
    ResponseResult<PatientOrderBenefitVo> responseResult = discountFeign.choiceBenefit(form);
    PatientOrderBenefitVo resultData = responseResult.getData();
    // 卡券优惠为空
    if (null == resultData) {
      throw new ClientServiceException(responseResult.getMsg(), responseResult.getStatus());
    }
    List<PatientItemBenefitVo> itemList = resultData.getItemList();
    for (OrderDetailChargeVO vo : detailList) {
      for (PatientItemBenefitVo benefitVo : itemList) {
        BigDecimal receivableAmount = vo.getReceivableAmount();
        BigDecimal actualAmount = vo.getActualAmount();
        List<PrivilegeCouponInfoVO> discountAppliesCoupon = vo.getDiscountAppliesCoupons();
        if (vo.getOrderDetailId().equals(benefitVo.getOrderDetailId())) {
          BigDecimal discountAmount = benefitVo.getItemBenefitAmount();
          actualAmount = actualAmount.subtract(discountAmount);
          // 设置折扣率
          vo.setDiscountRate(
              actualAmount
                  .divide(receivableAmount, 4, RoundingMode.HALF_UP)
                  .multiply(BigDecimal.valueOf(100)));
          // 设置订单明细卡券匹配信息
          List<ItemUseBenefitVo> benefitList = benefitVo.getItemBenefitList();
          if (StringHelper.isNotEmpty(benefitList)) {
            setPrivilegeCouponInfoValue(discountAppliesCoupon, benefitList);
          }
        }
        vo.setActualAmount(actualAmount);
        vo.setDiscountAppliesCoupons(discountAppliesCoupon);
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
          PrivilegeCouponInfoVO couponInfoVO = new PrivilegeCouponInfoVO();
          couponInfoVO.setBenefitId(benefitVo.getBenefitId());
          Integer benefitType = benefitVo.getBenefitType();
          couponInfoVO.setCouponType(
              0 == benefitType ? Integer.valueOf(99) : benefitVo.getCouponType());
          couponInfoVO.setBenefitName(benefitVo.getBenefitName());
          couponInfoVO.setBenefitAmount(benefitVo.getBenefitAmount());
          discountAppliesCoupon.add(couponInfoVO);
        });
  }

  /**
   * 匹配授权折扣订单详情信息
   *
   * @param detailList 订单详情列表
   * @param accreditDiscountModel 授权折扣信息
   */
  private void matchAccreditDiscountOrderDetailValue(
      List<OrderDetailChargeVO> detailList, AccreditDiscountModel accreditDiscountModel) {
    List<AccreditDiscountDetailModel> models =
        accreditDiscountModel.getAccreditDiscountDetailModels();
    detailList.stream()
        .<Consumer<? super AccreditDiscountDetailModel>>map(
            vo ->
                model -> {
                  List<PrivilegeCouponInfoVO> discountAppliesCoupon =
                      vo.getDiscountAppliesCoupons();
                  if (vo.getOrderDetailId().equals(model.getOrderDetailId())) {
                    BigDecimal actualAmount = model.getActualAmount();
                    vo.setActualAmount(actualAmount);
                    BigDecimal receivableAmount = vo.getReceivableAmount();
                    // 设置折扣率
                    vo.setDiscountRate(
                        actualAmount
                            .divide(receivableAmount, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)));
                    // 设置优惠匹配信息
                    if (receivableAmount.compareTo(actualAmount) != 0) {
                      PrivilegeCouponInfoVO couponInfoVO = new PrivilegeCouponInfoVO();
                      Integer warrantId = accreditDiscountModel.getWarrantId();
                      couponInfoVO.setBenefitId(warrantId);
                      couponInfoVO.setCouponType(5);
                      SysEmployee employee = systemServiceFeign.findSysEmployeeById(warrantId);
                      if (null != employee) {
                        couponInfoVO.setBenefitName(employee.getName());
                      }
                      couponInfoVO.setBenefitAmount(receivableAmount.subtract(actualAmount));
                      discountAppliesCoupon.add(couponInfoVO);
                    }
                  }
                  vo.setDiscountAppliesCoupons(discountAppliesCoupon);
                })
        .forEach(models::forEach);
  }

  /**
   * 确认收费
   *
   * @param model 收费参数
   */
  public void confirmCharge(TollModel model) {
    Integer orderRecordId = model.getOrderRecordId();
    Byte discountType = model.getDiscountType();
    GeneralDiscountModel generalDiscount = model.getGeneralDiscountModel();
    AccreditDiscountModel accreditDiscount = model.getAccreditDiscountModel();
    InvoiceModel invoiceModel = model.getInvoiceModel();
    // 收费参数校验
    OrderRecord orderRecord =
        checkParam(orderRecordId, discountType, generalDiscount, accreditDiscount, invoiceModel);
    // 计算优惠总额
    BigDecimal privilegeAmount =
        calculatePrivilegeAmount(discountType, orderRecordId, generalDiscount, accreditDiscount);
    Set<PrepaymentAccountModel> prepaymentAccounts = model.getPrepaymentAccountModels();
    Set<MemberAccountModel> memberAccounts = model.getMemberAccountModels();
    Set<PaymentModel> payments = model.getPaymentModels();
    // 计算入账总额
    BigDecimal totalCharge = calculateTotalCharge(prepaymentAccounts, memberAccounts, payments);
    BigDecimal totalAmount = orderRecord.getTotalAmount();
    BigDecimal actualReceivableAmount = totalAmount.subtract(privilegeAmount);
    // 比较实际应收与总入账金额
    BigDecimal outstandingAmount = model.getOutstandingAmount();
    checkTotalChargeAndDebtAmount(totalCharge, actualReceivableAmount, outstandingAmount);
    // 开始收费
    // redisUtils.set(LOCK_ORDER_PROCESSING_CHARGE + orderRecordId, orderRecordId, 300);
    Integer patientId = orderRecord.getPatientId();
    Integer treatmentRecordId = orderRecord.getTreatmentRecordId();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    // 保存账单信息
    BillRecord billRecord = new BillRecord();
    billRecord.setOrgId(orgId);
    billRecord.setPatientId(patientId);
    billRecord.setTreatmentRecordId(treatmentRecordId);
    billRecord.setOrderRecordId(orderRecordId);
    String billNum = billRecordBiz.generateBillNumber(orgId);
    billRecord.setBillNumber(billNum);
    billRecord.setPrivilegeType(discountType);
    billRecord.setReceivableAmount(totalAmount);
    billRecord.setPrivilegeAmount(privilegeAmount);
    billRecord.setActualReceivableAmount(actualReceivableAmount);
    billRecord.setReceivedAmount(totalCharge);
    billRecord.setDebtAmount(actualReceivableAmount.subtract(totalCharge));
    billRecord.setInvoice(model.getInvoiceModel().getInvoice());
    billRecord.setInvoiceNumber(model.getInvoiceModel().getInvoiceNumber());
    billRecord.setCrtId(userId);
    billRecord.setCrtName(name);
    billRecordBiz.insertSelective(billRecord);
    // 保存账单记录
    Integer billRecordId = billRecord.getId();
    BillPayRecord billPayRecord = new BillPayRecord();
    billPayRecord.setOrgId(orgId);
    billPayRecord.setPatientId(patientId);
    billPayRecord.setTreatmentRecordId(treatmentRecordId);
    billPayRecord.setOrderRecordId(orderRecordId);
    billPayRecord.setBillRecordId(billRecordId);
    if (totalCharge.compareTo(actualReceivableAmount) >= 0) {
      billPayRecord.setReceivedAmount(actualReceivableAmount);
      billPayRecord.setStillOweAmount(BigDecimal.valueOf(0));
    } else {
      billPayRecord.setReceivedAmount(totalCharge);
      billPayRecord.setStillOweAmount(actualReceivableAmount.subtract(totalCharge));
    }
    billPayRecord.setCrtId(userId);
    billPayRecord.setCrtName(name);
    billPayRecordBiz.insertSelective(billPayRecord);
    // 保存订单明细收费记录
    saveOrderDetailPayRecord(
        totalCharge, discountType, orderRecordId, billRecordId, generalDiscount, accreditDiscount);
    //  保存优惠明细
    savePrivilegeDetail(discountType, patientId, orderRecordId, generalDiscount, accreditDiscount);
    // 扣除预付款、会员卡余额
    Integer billPayRecordId = billPayRecord.getId();
    if (StringHelper.isNotEmpty(prepaymentAccounts)) {
      usePrepaymentAccount(
          prepaymentAccounts, patientId, treatmentRecordId, billRecordId, billPayRecordId);
    }
    if (StringHelper.isNotEmpty(memberAccounts)) {
      useMemberAccount(memberAccounts, patientId, treatmentRecordId, billRecordId, billPayRecordId);
    }
    // 保存收费明细
    saveBillPayDetailRecord(billPayRecordId, prepaymentAccounts, memberAccounts, payments);
    orderRecord.setStatus((byte) 2);
    orderRecordBiz.updateSelectiveById(orderRecord);
    TreatmentRecord treatmentRecord = treatmentRecordMapper.selectByPrimaryKey(treatmentRecordId);
    treatmentRecord.setStatus((byte) 3);
    treatmentRecordMapper.updateByPrimaryKeySelective(treatmentRecord);
    redisUtils.delete(LOCK_ORDER_PROCESSING_CHARGE + orderRecordId);
  }

  /**
   * 根据优惠类型保存订单明细收费记录
   *
   * @param totalCharge 入账总额
   * @param discountType 优惠类型
   * @param orderRecordId 订单记录ID
   * @param billRecordId 账单记录ID
   * @param generalDiscount 卡券优惠
   * @param accreditDiscount 授权折扣
   */
  private void saveOrderDetailPayRecord(
      BigDecimal totalCharge,
      Byte discountType,
      Integer orderRecordId,
      Integer billRecordId,
      GeneralDiscountModel generalDiscount,
      AccreditDiscountModel accreditDiscount) {
    switch (discountType) {
        // 不使用优惠
      case 0:
        saveOrderDetailPayRecordWithNoDiscount(totalCharge, orderRecordId, billRecordId);
        break;
        // 卡券优惠
      case 1:
        saveBillPayDetailRecordWithGeneralDiscount(
            totalCharge, orderRecordId, billRecordId, generalDiscount);
        break;
        // 授权折扣
      case 2:
        saveBillPayDetailRecordWithAccreditDiscount(
            totalCharge, orderRecordId, billRecordId, accreditDiscount);
        break;
      default:
        break;
    }
  }

  /**
   * 保存不使用优惠的订单明细优惠记录
   *
   * @param totalCharge 总入账金额
   * @param orderRecordId 订单记录ID
   * @param billRecordId 账单记录ID
   */
  private void saveOrderDetailPayRecordWithNoDiscount(
      BigDecimal totalCharge, Integer orderRecordId, Integer billRecordId) {
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    List<OrderDetail> orderDetails = orderDetailBiz.selectList(orderDetail);
    OrderRecord orderRecord = orderRecordBiz.selectById(orderRecordId);
    if (StringHelper.isNotEmpty(orderDetails)) {
      for (OrderDetail detail : orderDetails) {
        OrderDetailPayRecord detailPayRecord = new OrderDetailPayRecord();
        detailPayRecord.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
        detailPayRecord.setPatientId(orderRecord.getPatientId());
        detailPayRecord.setTreatmentRecordId(orderRecord.getTreatmentRecordId());
        detailPayRecord.setOrderRecordId(orderRecordId);
        detailPayRecord.setOrderDetailId(detail.getId());
        detailPayRecord.setBillRecordId(billRecordId);
        BigDecimal receivableAmount = detail.getReceivableAmount();
        detailPayRecord.setReceivableAmount(receivableAmount);
        detailPayRecord.setPrivilegeAmount(BigDecimal.valueOf(0));
        detailPayRecord.setActualReceivable(receivableAmount);
        // 设置已收
        if (totalCharge.compareTo(receivableAmount) >= 0) {
          detailPayRecord.setReceivedAmount(receivableAmount);
          totalCharge = totalCharge.subtract(receivableAmount);
        } else {
          detailPayRecord.setReceivedAmount(totalCharge);
          totalCharge = BigDecimal.valueOf(0);
        }
        detailPayRecord.setCouponWorkload(BigDecimal.valueOf(0));
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
      List<OrderDetail> orderDetails = orderDetailBiz.selectList(orderDetail);
      List<PatientItemBenefitVo> benefitVos = benefitVo.getItemList();
      if (StringHelper.isNotEmpty(benefitVos)) {
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
          for (PatientItemBenefitVo vo : benefitVos) {
            Integer orderDetailId = vo.getOrderDetailId();
            if (detailId.equals(orderDetailId)) {
              privilegeAmount = vo.getItemBenefitAmount();
              actualAmount = receivableAmount.subtract(privilegeAmount);
            }
          }
          detailPayRecord.setPrivilegeAmount(privilegeAmount);
          detailPayRecord.setActualReceivable(actualAmount);
          // 设置已收
          if (totalCharge.compareTo(actualAmount) >= 0) {
            detailPayRecord.setReceivedAmount(actualAmount);
            totalCharge = totalCharge.subtract(actualAmount);
          } else {
            detailPayRecord.setReceivedAmount(totalCharge);
            totalCharge = BigDecimal.valueOf(0);
          }
          detailPayRecord.setCouponWorkload(BigDecimal.valueOf(0));
          Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
          detailPayRecord.setCrtId(userId);
          String name = BaseContextHandler.getName();
          detailPayRecord.setCrtName(name);
          detailPayRecord.setUpdId(userId);
          detailPayRecord.setUpdName(name);
          orderDetailPayRecordBiz.insertSelective(detailPayRecord);
        }
      } else {
        throw new ClientServiceException("收费失败，当前选择卡券未匹配任何优惠！", PARAMETERS_IS_ILLEGAL);
      }
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
          Integer orderDetailId = discountDetailModel.getOrderDetailId();
          if (detailId.equals(orderDetailId)) {
            actualAmount = discountDetailModel.getActualAmount();
            privilegeAmount = receivableAmount.subtract(actualAmount);
          }
        }
        detailPayRecord.setPrivilegeAmount(privilegeAmount);
        detailPayRecord.setActualReceivable(actualAmount);
        detailPayRecord.setCouponWorkload(BigDecimal.valueOf(0));
        // 设置已收
        if (totalCharge.compareTo(actualAmount) >= 0) {
          detailPayRecord.setReceivedAmount(actualAmount);
          totalCharge = totalCharge.subtract(actualAmount);
        } else {
          detailPayRecord.setReceivedAmount(totalCharge);
          totalCharge = BigDecimal.valueOf(0);
        }
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
   * @param generalDiscountModel 卡券优惠
   * @param accreditDiscountModel 授权折扣明细
   * @return
   */
  private BigDecimal calculatePrivilegeAmount(
      Byte discountType,
      Integer orderRecordId,
      GeneralDiscountModel generalDiscountModel,
      AccreditDiscountModel accreditDiscountModel) {
    BigDecimal privilegeAmount = BigDecimal.valueOf(0);
    switch (discountType) {
      case 1:
        privilegeAmount = calculateGeneralPrivilegeAmount(orderRecordId, generalDiscountModel);
        break;
      case 2:
        List<AccreditDiscountDetailModel> accreditDiscountDetailModels =
            accreditDiscountModel.getAccreditDiscountDetailModels();
        privilegeAmount = calculateAccreditPrivilegeAmount(accreditDiscountDetailModels);
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
      Integer orderRecordId, GeneralDiscountModel generalDiscountModel) {
    OrderRecord orderRecord = orderRecordBiz.selectById(orderRecordId);
    BigDecimal privilegeAmount = BigDecimal.valueOf(0);
    PatientChooseBenefitForm form = new PatientChooseBenefitForm();
    form.setPatientId(orderRecord.getPatientId());
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
    ResponseResult<PatientOrderBenefitVo> choiceBenefit = discountFeign.choiceBenefit(form);
    PatientOrderBenefitVo benefitVo = choiceBenefit.getData();
    if (null != benefitVo) {
      privilegeAmount = benefitVo.getBenefitTotalAmount();
    }
    return privilegeAmount;
  }

  /**
   * 计算授权折扣优惠总额
   *
   * @param accreditDiscountDetailModels 授权折扣明细
   * @return privilegeAmount 优惠总额
   */
  private BigDecimal calculateAccreditPrivilegeAmount(
      List<AccreditDiscountDetailModel> accreditDiscountDetailModels) {
    BigDecimal privilegeAmount = BigDecimal.valueOf(0);
    for (AccreditDiscountDetailModel detailModel : accreditDiscountDetailModels) {
      Integer orderDetailId = detailModel.getOrderDetailId();
      OrderDetail orderDetail = orderDetailBiz.selectById(orderDetailId);
      if (null != orderDetail) {
        BigDecimal receivableAmount = orderDetail.getReceivableAmount();
        BigDecimal actualAmount = detailModel.getActualAmount();
        privilegeAmount = privilegeAmount.add(receivableAmount.subtract(actualAmount));
      }
    }
    return privilegeAmount;
  }

  /**
   * 收费参数校验
   *
   * @param orderRecordId 订单记录ID
   * @param discountType 优惠类型
   * @param generalDiscountModel 卡券优惠
   * @param accreditDiscountModel 授权折扣
   * @param invoiceModel 发票
   * @return
   */
  private OrderRecord checkParam(
      Integer orderRecordId,
      Byte discountType,
      GeneralDiscountModel generalDiscountModel,
      AccreditDiscountModel accreditDiscountModel,
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
      throw new ClientServiceException("收费失败，当前账单已解锁！", PARAMETERS_IS_ILLEGAL);
    }
    // 校验优惠参数
    checkPrivilegeParam(discountType, generalDiscountModel, accreditDiscountModel);
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
   * @param accreditDiscountModel 授权折扣
   */
  private void checkPrivilegeParam(
      Byte discountType,
      GeneralDiscountModel generalDiscountModel,
      AccreditDiscountModel accreditDiscountModel) {
    switch (discountType) {
      case 1:
        Integer memberTypeId = generalDiscountModel.getMemberTypeId();
        Integer discountCouponId = generalDiscountModel.getDiscountCouponId();
        List<CouponDiscountInfoModel> discountInfoModels =
            generalDiscountModel.getCouponDiscountInfoModels();
        if (null == memberTypeId
            && null == discountCouponId
            && StringHelper.isEmpty(discountInfoModels)) {
          throw new ClientServiceException("当前未选择任何卡券！", PARAMETERS_IS_ILLEGAL);
        }
        break;
      case 2:
        Integer warrantId = accreditDiscountModel.getWarrantId();
        SysEmployee employee = systemServiceFeign.findSysEmployeeById(warrantId);
        if (null != employee) {
          if (!employee.getDiscount()) {
            throw new ClientServiceException("您当前选择的授权人不具备授权折扣权限！", PARAMETERS_IS_ILLEGAL);
          }
          List<AccreditDiscountDetailModel> discountDetailModels =
              accreditDiscountModel.getAccreditDiscountDetailModels();
          if (StringHelper.isEmpty(discountDetailModels)) {
            throw new ClientServiceException("授权折扣订单列表不能为空！", PARAMETERS_IS_ILLEGAL);
          }
        } else {
          throw new ClientServiceException("授权人不存在！", PARAMETERS_IS_ILLEGAL);
        }
        break;
      default:
        break;
    }
  }

  /**
   * 计算入账总额
   *
   * @param prepaymentAccountModels 预付款账户信息
   * @param memberAccountModels 会员账户信息
   * @param paymentModels 其他入账方式
   */
  private BigDecimal calculateTotalCharge(
      Set<PrepaymentAccountModel> prepaymentAccountModels,
      Set<MemberAccountModel> memberAccountModels,
      Set<PaymentModel> paymentModels) {
    BigDecimal totalAmount = BigDecimal.valueOf(0);
    // 预付款入账总额
    if (StringHelper.isNotEmpty(prepaymentAccountModels)) {
      for (PrepaymentAccountModel prepaymentAccountModel : prepaymentAccountModels) {
        BigDecimal amount = prepaymentAccountModel.getAmount();
        if (null == amount) {
          amount = BigDecimal.valueOf(0);
        }
        totalAmount = totalAmount.add(amount);
      }
    }
    // 会员卡入账总额
    if (StringHelper.isNotEmpty(memberAccountModels)) {
      for (MemberAccountModel memberAccountModel : memberAccountModels) {
        BigDecimal amount = memberAccountModel.getAmount();
        if (null == amount) {
          amount = BigDecimal.valueOf(0);
        }
        totalAmount = totalAmount.add(amount);
      }
    }
    // 其他方式入账总额
    if (StringHelper.isNotEmpty(paymentModels)) {
      for (PaymentModel paymentModel : paymentModels) {
        BigDecimal amount = paymentModel.getAmount();
        if (null == amount) {
          amount = BigDecimal.valueOf(0);
        }
        totalAmount = totalAmount.add(amount);
      }
    }
    return totalAmount;
  }

  /**
   * 保存账单入账明细记录
   *
   * @param billPayRecordId 账单收费记录ID
   * @param prepaymentAccountModels 预付款账户列表
   * @param memberAccountModels 会员卡账户列表
   * @param paymentModels 其他入账方式列表
   */
  private void saveBillPayDetailRecord(
      Integer billPayRecordId,
      Set<PrepaymentAccountModel> prepaymentAccountModels,
      Set<MemberAccountModel> memberAccountModels,
      Set<PaymentModel> paymentModels) {
    if (StringHelper.isNotEmpty(prepaymentAccountModels)) {
      prepaymentAccountModels.forEach(
          prepaymentAccountModel -> {
            BillPayDetailRecord billPayDetailRecord =
                setBillPayRecordDetailValue(
                    billPayRecordId,
                    prepaymentAccountModel.getAccountItemId(),
                    prepaymentAccountModel.getAmount(),
                    (byte) 0);
            billPayDetailRecord.setRemark(prepaymentAccountModel.getPrepaymentNum());
            billPayDetailRecordBiz.insertSelective(billPayDetailRecord);
          });
    }
    if (StringHelper.isNotEmpty(memberAccountModels)) {
      memberAccountModels.forEach(
          memberAccountModel -> {
            BillPayDetailRecord billPayDetailRecord =
                setBillPayRecordDetailValue(
                    billPayRecordId,
                    memberAccountModel.getAccountItemId(),
                    memberAccountModel.getAmount(),
                    (byte) 1);
            billPayDetailRecord.setRemark(memberAccountModel.getMemberNum());
            billPayDetailRecordBiz.insertSelective(billPayDetailRecord);
          });
    }
    if (StringHelper.isNotEmpty(paymentModels)) {
      paymentModels.forEach(
          paymentModel -> {
            BillPayDetailRecord billPayDetailRecord =
                setBillPayRecordDetailValue(
                    billPayRecordId,
                    paymentModel.getAccountItemId(),
                    paymentModel.getAmount(),
                    (byte) 2);
            billPayDetailRecordBiz.insertSelective(billPayDetailRecord);
          });
    }
  }

  /**
   * 设置账单支付明细记录字段属性
   *
   * @param billPayRecordId 账单支付记录
   * @param accountItemId 支付方式ID
   * @param amount 支付金额
   */
  private BillPayDetailRecord setBillPayRecordDetailValue(
      Integer billPayRecordId, Integer accountItemId, BigDecimal amount, Byte type) {
    BillPayRecord billPayRecord = billPayRecordBiz.selectById(billPayRecordId);
    BillPayDetailRecord billPayDetailRecord = new BillPayDetailRecord();
    Integer patientId = billPayRecord.getPatientId();
    Integer treatmentRecordId = billPayRecord.getTreatmentRecordId();
    Integer orderRecordId = billPayRecord.getOrderRecordId();
    Integer billRecordId = billPayRecord.getBillRecordId();
    billPayDetailRecord.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
    billPayDetailRecord.setPatientId(patientId);
    billPayDetailRecord.setTreatmentRecordId(treatmentRecordId);
    billPayDetailRecord.setOrderRecordId(orderRecordId);
    billPayDetailRecord.setBillRecordId(billRecordId);
    billPayDetailRecord.setBillPayRecordId(billPayRecordId);
    billPayDetailRecord.setAccountItemId(accountItemId);
    billPayDetailRecord.setAmount(amount);
    billPayDetailRecord.setType(type);
    billPayDetailRecord.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    billPayDetailRecord.setCrtName(BaseContextHandler.getName());
    return billPayDetailRecord;
  }

  /**
   * 使用会员账户付款
   *
   * @param memberAccountModels 会员账户列表
   * @param patientId 患者ID
   * @param treatmentRecordId 就诊记录ID
   * @param billRecordId 账单记录ID
   * @param billPayRecordId 账单支付记录ID
   */
  private void useMemberAccount(
      Set<MemberAccountModel> memberAccountModels,
      Integer patientId,
      Integer treatmentRecordId,
      Integer billRecordId,
      Integer billPayRecordId) {
    MemberExpendRecordModel memberExpendRecordModel = new MemberExpendRecordModel();
    memberAccountModels.forEach(
        memberAccountModel -> {
          memberExpendRecordModel.setPatientId(patientId);
          memberExpendRecordModel.setMemberId(memberAccountModel.getMemberNum());
          memberExpendRecordModel.setExpendTotal(memberAccountModel.getAmount());
          memberExpendRecordModel.setTreatmentRecordId(treatmentRecordId);
          memberExpendRecordModel.setBillRecordId(billRecordId);
          memberExpendRecordModel.setBillPayRecordId(billPayRecordId);
          memberExpendRecordModel.setType(1);
          remotePatientCentralServiceFeign.expend(memberExpendRecordModel);
        });
  }

  /**
   * 使用预付款账户付款
   *
   * @param prepaymentAccountModels 预付款账户列表
   * @param patientId 患者ID
   * @param treatmentRecordId 就诊记录ID
   * @param billRecordId 账单ID
   * @param billPayRecordId 账单支付记录ID
   */
  private void usePrepaymentAccount(
      Set<PrepaymentAccountModel> prepaymentAccountModels,
      Integer patientId,
      Integer treatmentRecordId,
      Integer billRecordId,
      Integer billPayRecordId) {
    PrepaidExpendRecordModel prepaidExpendRecordModel = new PrepaidExpendRecordModel();
    prepaymentAccountModels.forEach(
        prepaymentAccountModel -> {
          prepaidExpendRecordModel.setPatientId(patientId);
          prepaidExpendRecordModel.setPrepaidId(prepaymentAccountModel.getPrepaymentNum());
          prepaidExpendRecordModel.setExpendTotal(prepaymentAccountModel.getAmount());
          prepaidExpendRecordModel.setTreatmentRecordId(treatmentRecordId);
          prepaidExpendRecordModel.setBillRecordId(billRecordId);
          prepaidExpendRecordModel.setBillPayRecordId(billPayRecordId);
          prepaidExpendRecordModel.setType(1);
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
   * @param generalDiscount 卡券列表
   * @param accreditDiscount 授权折扣信息
   */
  private void savePrivilegeDetail(
      Byte discountType,
      Integer patientId,
      Integer orderRecordId,
      GeneralDiscountModel generalDiscount,
      AccreditDiscountModel accreditDiscount) {
    switch (discountType) {
      case 1:
        saveCouponPrivilege(patientId, orderRecordId, generalDiscount);
        break;
      case 2:
        saveAccreditPrivilege(patientId, orderRecordId, accreditDiscount);
        break;
      default:
        break;
    }
  }

  /**
   * 保存授权折扣优惠明细
   *
   * @param patientId 患者ID
   * @param orderRecordId 订单记录ID
   * @param accreditDiscount 授权折扣信息
   */
  private void saveAccreditPrivilege(
      Integer patientId, Integer orderRecordId, AccreditDiscountModel accreditDiscount) {
    AuthDiscountBenefitModel model = new AuthDiscountBenefitModel();
    model.setOrderId(orderRecordId);
    model.setPatientId(patientId);
    model.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
    model.setAuthorizedId(accreditDiscount.getWarrantId());
    model.setRemark(accreditDiscount.getRemarks());
    List<AccreditDiscountDetailModel> models = accreditDiscount.getAccreditDiscountDetailModels();
    List<AuthItemBenefitModel> items = Lists.newArrayList();
    // 构建授权优惠参数列表
    models.forEach(
        detailModel -> {
          Integer orderDetailId = detailModel.getOrderDetailId();
          AuthItemBenefitModel benefitModel = new AuthItemBenefitModel();
          benefitModel.setOrderDetailId(orderDetailId);
          OrderDetail orderDetail = orderDetailBiz.selectById(orderDetailId);
          benefitModel.setItemId(orderDetail.getBillingItemId());
          benefitModel.setType(Integer.valueOf(orderDetail.getType()));
          BigDecimal receivableAmount = orderDetail.getReceivableAmount();
          BigDecimal actualAmount = detailModel.getActualAmount();
          BigDecimal privilegeDiscount = receivableAmount.subtract(actualAmount);
          benefitModel.setBenefitAmount(privilegeDiscount);
          items.add(benefitModel);
        });
    model.setItemBenefits(items);
    discountFeign.saveAuthBenefit(model);
  }

  /**
   * 保存优惠券使用优惠明细
   *
   * @param patientId 患者ID
   * @param orderRecordId 订单记录ID
   * @param generalDiscount 卡券列表
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
  @Transactional(rollbackFor = Exception.class)
  public void collectDebt(TollDebtModel model) {
    Integer treatmentId = model.getTreatmentRecordId();
    GeneralDiscountModel generalDiscount = model.getGeneralDiscountModel();
    AccreditDiscountModel accreditDiscount = model.getAccreditDiscountModel();
    // 校验收欠费参数合法性
    BillRecord billRecordResult =
        checkCollectDebtParams(treatmentId, generalDiscount, accreditDiscount);
    Set<PrepaymentAccountModel> prepaymentAccounts = model.getPrepaymentAccountModels();
    Set<MemberAccountModel> memberAccounts = model.getMemberAccountModels();
    Set<PaymentModel> paymentModels = model.getPaymentModels();
    // 计算并校验收欠费入账总额
    BigDecimal totalCharge =
        calculateAndCheckReceivedAmount(prepaymentAccounts, memberAccounts, paymentModels);
    BigDecimal outstandingAmount = model.getOutstandingAmount();
    InvoiceModel invoiceModel = model.getInvoiceModel();
    byte discountType = 0;
    Integer billRecordId;
    Integer patientId;
    Integer orderRecordId;
    BigDecimal debtAmount;
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    if (null != billRecordResult) {
      BigDecimal privilegeAmount = billRecordResult.getPrivilegeAmount();
      BigDecimal actualReceivableAmount = billRecordResult.getActualReceivableAmount();
      BigDecimal receivedAmount = billRecordResult.getReceivedAmount();
      patientId = billRecordResult.getPatientId();
      if (receivedAmount.compareTo(BigDecimal.valueOf(0)) > 0) {
        debtAmount = billRecordResult.getDebtAmount();
        checkTotalChargeAndDebtAmount(totalCharge, debtAmount, outstandingAmount);
        debtAmount = debtAmount.subtract(totalCharge);
      } else {
        // 账单未使用过优惠，重新使用优惠
        orderRecordId = billRecordResult.getOrderRecordId();
        discountType =
            saveDiscountDetail(
                generalDiscount, accreditDiscount, discountType, patientId, orderRecordId);
        privilegeAmount =
            calculatePrivilegeAmount(
                discountType, orderRecordId, generalDiscount, accreditDiscount);
        // 实际应收 = 实际应收 - 优惠
        actualReceivableAmount = actualReceivableAmount.subtract(privilegeAmount);
        // 比较实收与实际应收
        checkTotalChargeAndDebtAmount(totalCharge, actualReceivableAmount, outstandingAmount);
        debtAmount = actualReceivableAmount.subtract(totalCharge);
      }
      billRecordResult.setPrivilegeType(discountType);
      // 设置优惠总额
      billRecordResult.setPrivilegeAmount(privilegeAmount);
      // 设置实际应收
      billRecordResult.setActualReceivableAmount(actualReceivableAmount);
      // 设置已收
      billRecordResult.setReceivedAmount(receivedAmount.add(totalCharge));
      // 设置欠费总额
      billRecordResult.setDebtAmount(debtAmount);
      if (null != invoiceModel) {
        billRecordResult.setInvoice(invoiceModel.getInvoice());
        billRecordResult.setInvoiceNumber(invoiceModel.getInvoiceNumber());
      }
      billRecordResult.setUpdId(userId);
      billRecordResult.setUpdName(name);
      billRecordBiz.updateSelectiveById(billRecordResult);
      // 保存收费记录
      billRecordId = billRecordResult.getId();
      orderRecordId = billRecordResult.getOrderRecordId();
      // 更新订单明细收费记录
      updateOrderDetailPayRecord(orderRecordId, totalCharge);
      savePrivilegeDetail(discountType, patientId,orderRecordId,generalDiscount,accreditDiscount);
    } else {
      // 调整账单重新收费
      OrderRecord orderRecordResult = checkOrderRecord(treatmentId);
      // 获取开单总额
      BigDecimal totalAmount = orderRecordResult.getTotalAmount();
      orderRecordId = orderRecordResult.getId();
      patientId = orderRecordResult.getPatientId();
      discountType =
          saveDiscountDetail(
              generalDiscount, accreditDiscount, discountType, patientId, orderRecordId);
      // 计算优惠总额
      BigDecimal privilegeAmount =
          calculatePrivilegeAmount(discountType, orderRecordId, generalDiscount, accreditDiscount);
      BillRecord billRecord = new BillRecord();
      billRecord.setOrgId(orgId);
      billRecord.setPatientId(patientId);
      billRecord.setTreatmentRecordId(treatmentId);
      billRecord.setOrderRecordId(orderRecordId);
      String billNumber = billRecordBiz.generateBillNumber(orgId);
      billRecord.setBillNumber(billNumber);
      billRecord.setReceivableAmount(totalAmount);
      billRecord.setPrivilegeType(discountType);
      billRecord.setPrivilegeAmount(privilegeAmount);
      BigDecimal actualReceivableAmount = totalAmount.subtract(privilegeAmount);
      billRecord.setActualReceivableAmount(actualReceivableAmount);
      billRecord.setReceivedAmount(totalCharge);
      debtAmount = actualReceivableAmount.subtract(totalCharge);
      billRecord.setDebtAmount(debtAmount);
      if (null != invoiceModel) {
        billRecord.setInvoice(invoiceModel.getInvoice());
        billRecord.setInvoiceNumber(invoiceModel.getInvoiceNumber());
      }
      billRecord.setCrtId(userId);
      billRecord.setCrtName(name);
      billRecord.setUpdId(userId);
      billRecord.setUpdName(name);
      billRecordBiz.insertSelective(billRecord);
      billRecordId = billRecord.getId();
      // 更新订单为已收费
      orderRecordResult.setStatus((byte) 2);
      orderRecordBiz.updateSelectiveById(orderRecordResult);
      // 保存订单明细收费记录
      saveOrderDetailPayRecord(
          totalCharge,
          discountType,
          orderRecordId,
          billRecordId,
          generalDiscount,
          accreditDiscount);
      savePrivilegeDetail(discountType, patientId,orderRecordId,generalDiscount,accreditDiscount);
    }
    BillPayRecord billPayRecord = new BillPayRecord();
    billPayRecord.setOrgId(orgId);
    billPayRecord.setPatientId(patientId);
    billPayRecord.setTreatmentRecordId(treatmentId);
    billPayRecord.setOrderRecordId(orderRecordId);
    billPayRecord.setBillRecordId(billRecordId);
    billPayRecord.setReceivedAmount(totalCharge);
    billPayRecord.setStillOweAmount(debtAmount);
    billPayRecord.setCrtId(userId);
    billPayRecord.setCrtName(name);
    billPayRecord.setUpdId(userId);
    billPayRecord.setUpdName(name);
    billPayRecordBiz.insertSelective(billPayRecord);
    // 保存收费记录入账明细¬
    Integer billPayRecordId = billPayRecord.getId();
    if (StringHelper.isNotEmpty(prepaymentAccounts)) {
      usePrepaymentAccount(
          prepaymentAccounts, patientId, treatmentId, billRecordId, billPayRecordId);
    }
    if (StringHelper.isNotEmpty(memberAccounts)) {
      useMemberAccount(memberAccounts, patientId, treatmentId, billRecordId, billPayRecordId);
    }
    // 保存收费记录支付方式明细
    saveBillPayDetailRecord(billPayRecordId, prepaymentAccounts, memberAccounts, paymentModels);
  }

  /**
   * 保存优惠明细
   *
   * @param generalDiscount 卡券优惠
   * @param accreditDiscount 授权折扣
   * @param discountType 优惠类型
   * @param patientId 患者ID
   * @param orderRecordId 订单ID
   * @return discountType
   */
  private byte saveDiscountDetail(
      GeneralDiscountModel generalDiscount,
      AccreditDiscountModel accreditDiscount,
      byte discountType,
      Integer patientId,
      Integer orderRecordId) {
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
   * @param orderRecordId 开单记录ID
   * @param totalCharge 总入账金额
   */
  private void updateOrderDetailPayRecord(Integer orderRecordId, BigDecimal totalCharge) {
    OrderDetailPayRecord orderDetailPayRecord = new OrderDetailPayRecord();
    orderDetailPayRecord.setOrderRecordId(orderRecordId);
    orderDetailPayRecord.setInservice(true);
    List<OrderDetailPayRecord> detailPayRecords =
        orderDetailPayRecordBiz.selectList(orderDetailPayRecord);
    if (StringHelper.isNotEmpty(detailPayRecords)) {
      for (OrderDetailPayRecord detailPayRecord : detailPayRecords) {
        BigDecimal actualReceivable = detailPayRecord.getActualReceivable();
        BigDecimal receivedAmount = detailPayRecord.getReceivedAmount();
        if (actualReceivable.compareTo(receivedAmount) > 0) {
          // 本项目剩余应收
          BigDecimal restReceivedAmount = actualReceivable.subtract(receivedAmount);
          // 已收大于等于该项目剩余应收（实收=实际应收）；已收小于剩余应收，（实收=该项目已收+总的收款）
          if (totalCharge.compareTo(restReceivedAmount) >= 0) {
            detailPayRecord.setReceivedAmount(actualReceivable);
            totalCharge = totalCharge.subtract(restReceivedAmount);
          } else {
            detailPayRecord.setReceivedAmount(receivedAmount.add(totalCharge));
            totalCharge = BigDecimal.valueOf(0);
          }
          detailPayRecord.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
          detailPayRecord.setUpdName(BaseContextHandler.getName());
          orderDetailPayRecordBiz.updateSelectiveById(detailPayRecord);
        }
      }
    }
  }

  /**
   * 校验账单是否允许收欠费
   *
   * @param treatmentRecordId 就诊记录ID
   * @param generalDiscountModel 优惠
   * @param accreditDiscountModel 授权折扣
   */
  private BillRecord checkCollectDebtParams(
      Integer treatmentRecordId,
      GeneralDiscountModel generalDiscountModel,
      AccreditDiscountModel accreditDiscountModel) {
    TreatmentRecord treatmentRecord = treatmentRecordMapper.selectByPrimaryKey(treatmentRecordId);
    if (null == treatmentRecord) {
      throw new ClientServiceException("收欠费失败，就诊记录不存在！", PARAMETERS_IS_ILLEGAL);
    }
    // 只有收过费的订单才能收欠费
    BillRecord billRecord = new BillRecord();
    billRecord.setTreatmentRecordId(treatmentRecordId);
    Long count = billRecordBiz.selectCount(billRecord);
    if (count <= 0) {
      throw new ClientServiceException("收欠费失败，当前就诊还未收费，请到就诊列表进行收费！", QUERY_RESULT_INVALID);
    }
    // 未欠费的账单不能收欠费
    billRecord.setInservice(true);
    BillRecord record = billRecordBiz.selectOne(billRecord);
    if (null != record) {
      BigDecimal debtAmount = record.getDebtAmount();
      if (debtAmount.compareTo(BigDecimal.valueOf(0)) <= 0) {
        throw new ClientServiceException("收欠费失败，当前账单不存在欠费！", QUERY_RESULT_INVALID);
      }
      // 只能使用一种优惠
      Byte privilegeType = record.getPrivilegeType();
      if (0 != privilegeType && (null != generalDiscountModel || null != accreditDiscountModel)) {
        throw new ClientServiceException("收欠费失败，当前账单已使用优惠，不能继续使用优惠！", PARAMETERS_IS_ILLEGAL);
      }
      // todo 校验发票
      return record;
    }
    return null;
  }

  /**
   * 计算收欠费入账金额
   *
   * @param prepaymentAccountModels 预付款账户列表
   * @param memberAccountModels 会员账户列表
   * @param paymentModels 其他支付方式列表
   * @return
   */
  private BigDecimal calculateAndCheckReceivedAmount(
      Set<PrepaymentAccountModel> prepaymentAccountModels,
      Set<MemberAccountModel> memberAccountModels,
      Set<PaymentModel> paymentModels) {
    BigDecimal totalCharge =
        calculateTotalCharge(prepaymentAccountModels, memberAccountModels, paymentModels);
    if (BigDecimal.valueOf(0).compareTo(totalCharge) >= 0) {
      throw new ClientServiceException("收欠费失败，收欠费总额不能小于等于0！", PARAMETERS_IS_ILLEGAL);
    }
    return totalCharge;
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
    if (totalCharge.add(outstandingAmount).compareTo(debtAmount) != 0) {
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
}
