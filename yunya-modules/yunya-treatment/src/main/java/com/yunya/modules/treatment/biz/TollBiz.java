package com.yunya.modules.treatment.biz;

import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.model.MemberExpendRecordModel;
import com.yunya.feign.patient_central.domain.model.PrepaidExpendRecordModel;
import com.yunya.feign.treatment.domain.model.*;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.treatment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

import static com.yunya.framework.common.constant.BusinessConstants.ORDER_LOCK_STATUS;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;
import static com.yunya.framework.common.constant.OperationCodeConstants.QUERY_RESULT_INVALID;
import static com.yunya.framework.common.constant.RedisConstants.LOCK_ORDER_PROCESSING_CHARGE;
import static com.yunya.framework.common.constant.RedisConstants.LOCK_ORDER_PROCESSING_UNLOCK;

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
  /** 患者服务调用 */
  @Autowired private PatientCentralServiceFeign patientCentralServiceFeign;
  /** 开单记录 */
  @Autowired private OrderRecordBiz orderRecordBiz;
  /** 开单明细 */
  @Autowired private OrderDetailBiz orderDetailBiz;
  /** 账单记录 */
  @Autowired private BillRecordBiz billRecordBiz;
  /** 账单支付记录 */
  @Autowired private BillPayRecordBiz billPayRecordBiz;
  /** 账单支付明细记录 */
  @Autowired private BillPayDetailRecordBiz billPayDetailRecordBiz;
  /** 就诊记录 */
  @Autowired private TreatmentRecordBiz treatmentRecordBiz;

  /**
   * 确认收费
   *
   * @param model 收费参数
   */
  public void confirmCharge(TollModel model) {
    OrderRecord orderRecord = checkParam(model);
    BigDecimal totalAmount = orderRecord.getTotalAmount();
    // todo 调用服务获取优惠总额
    BigDecimal privilegeAmount = BigDecimal.valueOf(0);
    BigDecimal outstandingAmount = model.getOutstandingAmount();
    Set<PrepaymentAccountModel> prepaymentAccountModels = model.getPrepaymentAccountModels();
    Set<MemberAccountModel> memberAccountModels = model.getMemberAccountModels();
    Set<PaymentModel> paymentModels = model.getPaymentModels();
    BigDecimal totalCharge =
        calculateTotalCharge(prepaymentAccountModels, memberAccountModels, paymentModels);
    BigDecimal actualReceivableAmount = totalAmount.subtract(privilegeAmount);
    checkTotalChargeWithActualReceivedAmount(
        totalCharge, actualReceivableAmount, outstandingAmount);
    Integer orderRecordId = model.getOrderRecordId();
    redisUtils.set(LOCK_ORDER_PROCESSING_CHARGE + orderRecordId, orderRecordId, 5);

    Integer patientId = orderRecord.getPatientId();
    Integer treatmentRecordId = orderRecord.getTreatmentRecordId();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    // todo 设置收费使用的优惠类型
    BillRecord billRecord = new BillRecord();
    billRecord.setOrgId(orgId);
    billRecord.setPatientId(patientId);
    billRecord.setTreatmentRecordId(treatmentRecordId);
    billRecord.setOrderRecordId(orderRecordId);
    String billNum = billRecordBiz.generateBillNumber(orgId);
    billRecord.setBillNumber(billNum);
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
    Integer billPayRecordId = billPayRecord.getId();
    if (StringHelper.isNotEmpty(prepaymentAccountModels)) {
      usePrepaymentAccount(
          prepaymentAccountModels, patientId, treatmentRecordId, billRecordId, billPayRecordId);
    }
    if (StringHelper.isNotEmpty(memberAccountModels)) {
      useMemberAccount(
          memberAccountModels, patientId, treatmentRecordId, billRecordId, billPayRecordId);
    }

    saveBillPayDetailRecord(
        billPayRecordId, prepaymentAccountModels, memberAccountModels, paymentModels);
    orderRecord.setStatus((byte) 2);
    orderRecordBiz.updateSelectiveById(orderRecord);
    TreatmentRecord treatmentRecord = treatmentRecordBiz.selectById(treatmentRecordId);
    treatmentRecord.setStatus((byte) 3);
    treatmentRecordBiz.updateSelectiveById(treatmentRecord);
    redisUtils.delete(LOCK_ORDER_PROCESSING_CHARGE + orderRecordId);
  }

  /**
   * 收费参数校验
   *
   * @param model 收费参数
   * @return
   */
  private OrderRecord checkParam(TollModel model) {
    Integer orderRecordId = model.getOrderRecordId();
    OrderRecord orderRecord = orderRecordBiz.selectById(orderRecordId);
    if (null == orderRecord) {
      throw new ClientServiceException("收费失败，当前未选择正确的就诊记录或传入参数有误！", PARAMETERS_IS_ILLEGAL);
    }
    Byte status = orderRecord.getStatus();
    if (!status.equals(ORDER_LOCK_STATUS)) {
      throw new ClientServiceException("收费失败，当前账单处于解锁状态或收费完成状态，暂不能进行收费！", PARAMETERS_IS_ILLEGAL);
    }
    String resultRecordId = redisUtils.get(LOCK_ORDER_PROCESSING_UNLOCK + orderRecordId);
    if (StringHelper.isNotBlank(resultRecordId)) {
      throw new ClientServiceException("收费失败，当前账单正在修改！", PARAMETERS_IS_ILLEGAL);
    }
    GeneralDiscountModel generalDiscountModel = model.getGeneralDiscountModel();
    AccreditDiscountModel accreditDiscountModel = model.getAccreditDiscountModel();
    if (null != generalDiscountModel && null != accreditDiscountModel) {
      throw new ClientServiceException("收费失败，优惠与授权折扣不能同时使用！", PARAMETERS_IS_ILLEGAL);
    }
    InvoiceModel invoiceModel = model.getInvoiceModel();
    if (invoiceModel.getInvoice()) {
      if (StringHelper.isBlank(invoiceModel.getInvoiceNumber())) {
        throw new ClientServiceException("收费失败，未填写发票编号！", PARAMETERS_IS_ILLEGAL);
      }
    }
    return orderRecord;
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
        totalAmount = totalAmount.add(prepaymentAccountModel.getAmount());
      }
    }
    // 会员卡入账总额
    if (StringHelper.isNotEmpty(memberAccountModels)) {
      for (MemberAccountModel memberAccountModel : memberAccountModels) {
        totalAmount = totalAmount.add(memberAccountModel.getAmount());
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
            billPayDetailRecord.setRemark(
                prepaymentAccountModel.getPrepaymentAccountId().toString());
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
            billPayDetailRecord.setRemark(memberAccountModel.getMemberAccountId().toString());
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
          memberExpendRecordModel.setMemberId(memberAccountModel.getMemberAccountId().toString());
          memberExpendRecordModel.setExpendTotal(memberAccountModel.getAmount());
          memberExpendRecordModel.setTreatmentRecordId(treatmentRecordId);
          memberExpendRecordModel.setBillRecordId(billRecordId);
          memberExpendRecordModel.setBillPayRecordId(billPayRecordId);
          memberExpendRecordModel.setType(1);
          patientCentralServiceFeign.expend(memberExpendRecordModel);
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
          prepaidExpendRecordModel.setPrepaidId(
              prepaymentAccountModel.getPrepaymentAccountId().toString());
          prepaidExpendRecordModel.setExpendTotal(prepaymentAccountModel.getAmount());
          prepaidExpendRecordModel.setTreatmentRecordId(treatmentRecordId);
          prepaidExpendRecordModel.setBillRecordId(billRecordId);
          prepaidExpendRecordModel.setBillPayRecordId(billPayRecordId);
          prepaidExpendRecordModel.setType(1);
          patientCentralServiceFeign.expend(prepaidExpendRecordModel);
        });
  }

  /**
   * 收欠费
   *
   * @param model 收费参数
   */
  public void collectDebt(TollDebtModel model) {
    Integer billRecordId = model.getBillRecordId();
    GeneralDiscountModel generalDiscountModel = model.getGeneralDiscountModel();
    AccreditDiscountModel accreditDiscountModel = model.getAccreditDiscountModel();
    BillRecord billRecordResult =
        checkCollectDebtParams(billRecordId, generalDiscountModel, accreditDiscountModel);
    Set<PrepaymentAccountModel> prepaymentAccountModels = model.getPrepaymentAccountModels();
    Set<MemberAccountModel> memberAccountModels = model.getMemberAccountModels();
    Set<PaymentModel> paymentModels = model.getPaymentModels();
    BigDecimal totalCharge =
        calculateAndCheckReceivedAmount(
            prepaymentAccountModels, memberAccountModels, paymentModels);
    BigDecimal outstandingAmount = model.getOutstandingAmount();
    Boolean inservice = billRecordResult.getInservice();
    Integer patientId = billRecordResult.getPatientId();
    Integer treatmentRecordId = billRecordResult.getTreatmentRecordId();
    Integer orderRecordId = billRecordResult.getOrderRecordId();
    if (inservice) {
      BigDecimal debtAmount = billRecordResult.getDebtAmount();
      checkTotalChargeWithActualReceivedAmount(totalCharge, debtAmount, outstandingAmount);
      checkCollectDebtAvailable(debtAmount);
      BigDecimal receivedAmount = billRecordResult.getReceivedAmount();
      if (receivedAmount.compareTo(BigDecimal.valueOf(0)) > 0) {
        checkDiscountAvailable(generalDiscountModel, accreditDiscountModel);
        billRecordResult.setReceivedAmount(receivedAmount.add(totalCharge));
        billRecordResult.setDebtAmount(debtAmount.subtract(totalCharge));
      } else {
        BigDecimal privilegeAmount =
            reDiscountOrderRecord(billRecordResult, generalDiscountModel, accreditDiscountModel);
        billRecordResult.setPrivilegeAmount(privilegeAmount);
        billRecordResult.setReceivableAmount(debtAmount);
        BigDecimal actualReceivableAmount = debtAmount.subtract(privilegeAmount);
        billRecordResult.setActualReceivableAmount(actualReceivableAmount);
        billRecordResult.setReceivedAmount(receivedAmount.add(totalCharge));
        billRecordResult.setDebtAmount(actualReceivableAmount.subtract(totalCharge));
      }
      BillPayRecord billPayRecord = setBillPayRecordValue(billRecordId, totalCharge, debtAmount);
      billPayRecordBiz.insertSelective(billPayRecord);
      Integer billPayRecordId = billPayRecord.getId();
      if (StringHelper.isNotEmpty(prepaymentAccountModels)) {
        usePrepaymentAccount(
            prepaymentAccountModels, patientId, treatmentRecordId, billRecordId, billPayRecordId);
      }
      if (StringHelper.isNotEmpty(memberAccountModels)) {
        useMemberAccount(
            memberAccountModels, patientId, treatmentRecordId, billRecordId, billPayRecordId);
      }
      saveBillPayDetailRecord(
          billPayRecordId, prepaymentAccountModels, memberAccountModels, paymentModels);
      billRecordResult.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
      billRecordResult.setUpdName(BaseContextHandler.getName());
      billRecordBiz.updateSelectiveById(billRecordResult);
    } else {
      OrderRecord orderRecord = new OrderRecord();
      orderRecord.setTreatmentRecordId(treatmentRecordId);
      orderRecord.setInservice(true);
      OrderRecord orderRecordResult = orderRecordBiz.selectOne(orderRecord);
      BigDecimal totalAmount = orderRecordResult.getTotalAmount();
      checkTotalChargeWithActualReceivedAmount(totalCharge, totalAmount, outstandingAmount);
      reChargeBillRecord(
          patientId,
          treatmentRecordId,
          orderRecordId,
          totalAmount,
          totalCharge,
          prepaymentAccountModels,
          memberAccountModels,
          paymentModels);
      orderRecordResult.setStatus((byte) 2);
      orderRecordBiz.updateSelectiveById(orderRecordResult);
    }
  }

  /**
   * 对订单进行重新优惠
   *
   * @param billRecordResult 账单记录
   * @param generalDiscountModel 优惠信息
   * @param accreditDiscountModel 折扣信息
   * @return
   */
  private BigDecimal reDiscountOrderRecord(
      BillRecord billRecordResult,
      GeneralDiscountModel generalDiscountModel,
      AccreditDiscountModel accreditDiscountModel) {
    BigDecimal privilegeAmount = BigDecimal.valueOf(0);

    // todo 根据优惠类型获取优惠金额
    if (null != generalDiscountModel) {
      billRecordResult.setPrivilegeType((byte) 1);
    }
    if (null != accreditDiscountModel) {
      billRecordResult.setPrivilegeType((byte) 2);
    }
    return privilegeAmount;
  }

  /**
   * 校验账单是否允许收欠费
   *
   * @param billRecordId 账单记录ID
   * @param generalDiscountModel 优惠
   * @param accreditDiscountModel 授权折扣
   */
  private BillRecord checkCollectDebtParams(
      Integer billRecordId,
      GeneralDiscountModel generalDiscountModel,
      AccreditDiscountModel accreditDiscountModel) {
    BillRecord billRecordResult = billRecordBiz.selectById(billRecordId);
    if (null == billRecordResult) {
      throw new ClientServiceException("当前账单不存在，请选择正确的账单进行收欠费操作！", QUERY_RESULT_INVALID);
    }
    if (null != generalDiscountModel && null != accreditDiscountModel) {
      throw new ClientServiceException("收欠费失败，优惠与授权折扣不能同时使用！", PARAMETERS_IS_ILLEGAL);
    }
    Byte privilegeType = billRecordResult.getPrivilegeType();
    if (null != generalDiscountModel || null != accreditDiscountModel) {
      if (0 != privilegeType) {
        throw new ClientServiceException("收欠费失败，当前账单已使用优惠，不能继续使用优惠！", PARAMETERS_IS_ILLEGAL);
      }
    }

    return billRecordResult;
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
      throw new ClientServiceException("收欠费失败，请输入正确的入账金额！", PARAMETERS_IS_ILLEGAL);
    }
    return totalCharge;
  }

  /**
   * 比较实际应收与总入账金额
   *
   * @param totalCharge 总入账
   * @param actualReceivedAmount 实际应收
   * @param outstandingAmount 挂帐金额
   */
  private void checkTotalChargeWithActualReceivedAmount(
      BigDecimal totalCharge, BigDecimal actualReceivedAmount, BigDecimal outstandingAmount) {
    if (totalCharge.add(outstandingAmount).compareTo(actualReceivedAmount) != 0) {
      throw new ClientServiceException("入账方式金额与挂账金额之和不等于剩余应付金额合计！", PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 检查是否能收欠费
   *
   * @param debtAmount 欠费金额
   */
  private void checkCollectDebtAvailable(BigDecimal debtAmount) {
    if (BigDecimal.valueOf(0).compareTo(debtAmount) >= 0) {
      throw new ClientServiceException("收欠费失败，当前账单已全部结清，无法进行收欠费操作！", PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 校验能否使用优惠
   *
   * @param generalDiscountModel 优惠信息
   * @param accreditDiscountModel 授权折扣信息
   */
  private void checkDiscountAvailable(
      GeneralDiscountModel generalDiscountModel, AccreditDiscountModel accreditDiscountModel) {
    if (null != generalDiscountModel || null != accreditDiscountModel) {
      throw new ClientServiceException("收欠费失败，已完成结算的账单，无法继续使用优惠", PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 对订单进行重新收费
   *
   * @param patientId 患者ID
   * @param treatmentRecordId 就诊记录ID
   * @param orderRecordId 订单记录ID
   * @param debtAmount 欠费总额
   * @param totalCharge 入账合计
   * @param prepaymentAccountModels 预付款账户列表
   * @param memberAccountModels 会员账户列表
   * @param paymentModels 其他入账方式列表
   */
  private void reChargeBillRecord(
      Integer patientId,
      Integer treatmentRecordId,
      Integer orderRecordId,
      BigDecimal debtAmount,
      BigDecimal totalCharge,
      Set<PrepaymentAccountModel> prepaymentAccountModels,
      Set<MemberAccountModel> memberAccountModels,
      Set<PaymentModel> paymentModels) {
    BillRecord billRecord = new BillRecord();
    BigDecimal privilegeAmount = BigDecimal.valueOf(0);
    // todo 确认优惠信息
    billRecord.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
    billRecord.setPatientId(patientId);
    billRecord.setTreatmentRecordId(treatmentRecordId);
    billRecord.setOrderRecordId(orderRecordId);
    billRecord.setReceivableAmount(debtAmount);
    BigDecimal actualReceivedAmount = debtAmount.subtract(privilegeAmount);
    billRecord.setActualReceivableAmount(actualReceivedAmount);
    String billNumber =
        billRecordBiz.generateBillNumber(Integer.valueOf(BaseContextHandler.getOrgId()));
    billRecord.setBillNumber(billNumber);
    billRecordBiz.insertSelective(billRecord);
    Integer billRecordId = billRecord.getId();
    BillPayRecord billPayRecord =
        setBillPayRecordValue(billRecordId, totalCharge, actualReceivedAmount);
    billPayRecordBiz.insertSelective(billPayRecord);
    Integer billPayRecordId = billPayRecord.getId();
    if (StringHelper.isNotEmpty(prepaymentAccountModels)) {
      usePrepaymentAccount(
          prepaymentAccountModels, patientId, treatmentRecordId, billRecordId, billPayRecordId);
    }
    if (StringHelper.isNotEmpty(memberAccountModels)) {
      useMemberAccount(
          memberAccountModels, patientId, treatmentRecordId, billRecordId, billPayRecordId);
    }
    saveBillPayDetailRecord(
        billPayRecordId, prepaymentAccountModels, memberAccountModels, paymentModels);
  }

  /**
   * 设置账单支付记录值
   *
   * @param billRecordId 账单记录ID
   * @param totalCharge 入账金额总额
   * @param debtAmount 欠费总额
   * @return
   */
  private BillPayRecord setBillPayRecordValue(
      Integer billRecordId, BigDecimal totalCharge, BigDecimal debtAmount) {
    BillRecord billRecord = billRecordBiz.selectById(billRecordId);
    BillPayRecord billPayRecord = new BillPayRecord();
    billPayRecord.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
    billPayRecord.setPatientId(billRecord.getPatientId());
    billPayRecord.setTreatmentRecordId(billRecord.getTreatmentRecordId());
    billPayRecord.setOrderRecordId(billRecord.getOrderRecordId());
    billPayRecord.setBillRecordId(billRecordId);
    billPayRecord.setReceivedAmount(totalCharge);
    billPayRecord.setStillOweAmount(debtAmount.subtract(totalCharge));
    billPayRecord.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    billPayRecord.setCrtName(BaseContextHandler.getName());
    return billPayRecord;
  }
}
