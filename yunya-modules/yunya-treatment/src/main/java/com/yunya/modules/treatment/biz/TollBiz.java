package com.yunya.modules.treatment.biz;

import com.yunya.feign.treatment.domain.model.*;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
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
  public void confirm(TollModel model) {
    // 参数校验
    OrderRecord orderRecord = checkParam(model);

    Integer orderRecordId = model.getOrderRecordId();
    redisUtils.set(RedisConstants.LOCK_ORDER_PROCESSING_CHARGE + orderRecordId, orderRecordId, 5);

    // 开单总额
    BigDecimal totalAmount = orderRecord.getTotalAmount();
    // todo 调用服务获取优惠总额
    BigDecimal discountAmount = BigDecimal.valueOf(0);
    BigDecimal outstandingAmount = model.getOutstandingAmount();
    Set<PrepaymentAccountModel> prepaymentAccountModels = model.getPrepaymentAccountModels();
    Set<MemberAccountModel> memberAccountModels = model.getMemberAccountModels();
    Set<PaymentModel> paymentModels = model.getPaymentModels();
    // 已收金额 = 所有入账金额之和
    BigDecimal totalCharge =
        calculateTotalCharge(prepaymentAccountModels, memberAccountModels, paymentModels);
    // 实际应收 = 开单总额 - 优惠总额
    BigDecimal actualReceivableAmount = totalAmount.subtract(discountAmount);
    if (totalCharge.add(outstandingAmount).compareTo(actualReceivableAmount) != 0) {
      throw new ClientServiceException(
          "收费失败，挂账金额与入账方式金额之和不等于实收金额合计！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    Integer patientId = orderRecord.getPatientId();
    Integer treatmentRecordId = orderRecord.getTreatmentRecordId();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();

    // todo 调用服务扣除对应预付款、会员卡账户余额

    BillRecord billRecord = new BillRecord();
    billRecord.setOrgId(orgId);
    billRecord.setPatientId(patientId);
    billRecord.setTreatmentRecordId(treatmentRecordId);
    billRecord.setOrderRecordId(orderRecordId);
    String billNum = billRecordBiz.generateBillNumber(orgId);
    billRecord.setBillNumber(billNum);
    billRecord.setReceivableAmount(totalAmount);
    billRecord.setPrivilegeAmount(discountAmount);
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
      billPayRecord.setStillOwe(BigDecimal.valueOf(0));
    } else {
      billPayRecord.setReceivedAmount(totalCharge);
      billPayRecord.setStillOwe(actualReceivableAmount.subtract(totalCharge));
    }
    billPayRecord.setCrtId(userId);
    billPayRecord.setCrtName(name);
    billPayRecordBiz.insertSelective(billPayRecord);

    Integer billPayRecordId = billPayRecord.getId();
    saveBillPayDetailRecord(
        billPayRecordId, prepaymentAccountModels, memberAccountModels, paymentModels);

    orderRecord.setStatus((byte) 2);
    orderRecordBiz.updateSelectiveById(orderRecord);

    TreatmentRecord treatmentRecord = treatmentRecordBiz.selectById(treatmentRecordId);
    treatmentRecord.setStatus((byte) 4);
    treatmentRecordBiz.updateSelectiveById(treatmentRecord);

    redisUtils.delete(RedisConstants.LOCK_ORDER_PROCESSING_CHARGE + orderRecordId);
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
    BillPayRecord billPayRecord = billPayRecordBiz.selectById(billPayRecordId);
    Integer patientId = billPayRecord.getPatientId();
    Integer treatmentRecordId = billPayRecord.getTreatmentRecordId();
    Integer orderRecordId = billPayRecord.getOrderRecordId();
    Integer billRecordId = billPayRecord.getBillRecordId();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    if (StringHelper.isNotEmpty(prepaymentAccountModels)) {
      BillPayDetailRecord billPayDetailRecord = new BillPayDetailRecord();
      prepaymentAccountModels.forEach(
          prepaymentAccountModel -> {
            billPayDetailRecord.setOrgId(orgId);
            billPayDetailRecord.setPatientId(patientId);
            billPayDetailRecord.setTreatmentRecordId(treatmentRecordId);
            billPayDetailRecord.setOrderRecordId(orderRecordId);
            billPayDetailRecord.setBillRecordId(billRecordId);
            billPayDetailRecord.setBillPayRecordId(billPayRecordId);
            billPayDetailRecord.setAccountItemId(prepaymentAccountModel.getAccountItemId());
            billPayDetailRecord.setAmount(prepaymentAccountModel.getAmount());
            billPayDetailRecord.setType((byte) 0);
            billPayDetailRecord.setCrtId(userId);
            billPayDetailRecord.setCrtName(name);
            billPayDetailRecordBiz.insertSelective(billPayDetailRecord);
          });
    }
    if (StringHelper.isNotEmpty(memberAccountModels)) {
      BillPayDetailRecord billPayDetailRecord = new BillPayDetailRecord();
      memberAccountModels.forEach(
          memberAccountModel -> {
            billPayDetailRecord.setOrgId(orgId);
            billPayDetailRecord.setPatientId(patientId);
            billPayDetailRecord.setTreatmentRecordId(treatmentRecordId);
            billPayDetailRecord.setOrderRecordId(orderRecordId);
            billPayDetailRecord.setBillRecordId(billRecordId);
            billPayDetailRecord.setBillPayRecordId(billPayRecordId);
            billPayDetailRecord.setAccountItemId(memberAccountModel.getAccountItemId());
            billPayDetailRecord.setAmount(memberAccountModel.getAmount());
            billPayDetailRecord.setType((byte) 1);
            billPayDetailRecord.setCrtId(userId);
            billPayDetailRecord.setCrtName(name);
            billPayDetailRecordBiz.insertSelective(billPayDetailRecord);
          });
    }
    if (StringHelper.isNotEmpty(paymentModels)) {
      BillPayDetailRecord billPayDetailRecord = new BillPayDetailRecord();
      paymentModels.forEach(
          paymentModel -> {
            billPayDetailRecord.setOrgId(orgId);
            billPayDetailRecord.setPatientId(patientId);
            billPayDetailRecord.setTreatmentRecordId(treatmentRecordId);
            billPayDetailRecord.setOrderRecordId(orderRecordId);
            billPayDetailRecord.setBillRecordId(billRecordId);
            billPayDetailRecord.setBillPayRecordId(billPayRecordId);
            billPayDetailRecord.setAccountItemId(paymentModel.getAccountItemId());
            billPayDetailRecord.setAmount(paymentModel.getAmount());
            billPayDetailRecord.setType((byte) 2);
            billPayDetailRecord.setCrtId(userId);
            billPayDetailRecord.setCrtName(name);
            billPayDetailRecordBiz.insertSelective(billPayDetailRecord);
          });
    }
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
      throw new ClientServiceException(
          "收费失败，当前未选择正确的就诊记录或传入参数有误！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    Byte status = orderRecord.getStatus();
    if (!status.equals(BusinessConstants.ORDER_LOCK_STATUS)) {
      throw new ClientServiceException(
          "收费失败，当前账单处于解锁状态或收费完成状态，暂不能进行收费！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    String resultRecordId =
        redisUtils.get(RedisConstants.LOCK_ORDER_PROCESSING_UNLOCK + orderRecordId);
    if (StringHelper.isNotBlank(resultRecordId)) {
      throw new ClientServiceException(
          "收费失败，当前账单正在修改！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    GeneralDiscountModel generalDiscountModel = model.getGeneralDiscountModel();
    AccreditDiscountModel accreditDiscountModel = model.getAccreditDiscountModel();
    if (null != generalDiscountModel && null != accreditDiscountModel) {
      throw new ClientServiceException(
          "收费失败，优惠与授权折扣不能同时使用！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    InvoiceModel invoiceModel = model.getInvoiceModel();
    if (invoiceModel.getInvoice()) {
      if (StringHelper.isBlank(invoiceModel.getInvoiceNumber())) {
        throw new ClientServiceException(
            "收费失败，未填写发票编号！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
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
        totalAmount = totalAmount.add(paymentModel.getAmount());
      }
    }
    return totalAmount;
  }
}
