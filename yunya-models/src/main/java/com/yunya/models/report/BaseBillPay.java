package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "base_bill_pay")
public class BaseBillPay {
    /**
     * 订单收费记录ID
     */
    @Id
    @Column(name = "bill_pay_id")
    private Integer billPayId;

    /**
     * 订单ID
     */
    @Column(name = "bill_id")
    private Integer billId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 就诊ID
     */
    @Column(name = "treatment_id")
    private Integer treatmentId;

    /**
     * 收款人ID
     */
    @Column(name = "payee_user_id")
    private Integer payeeUserId;

    /**
     * 收款日期
     */
    @Column(name = "payee_date")
    private Date payeeDate;

    /**
     * 本次收费总额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 本次仍欠费总额
     */
    @Column(name = "still_owe_amount")
    private BigDecimal stillOweAmount;

    /**
     * 现金入账金额
     */
    @Column(name = "cash_amount")
    private BigDecimal cashAmount;

    /**
     * 支付宝入账金额
     */
    @Column(name = "alipay_amount")
    private BigDecimal alipayAmount;

    /**
     * 微信入账金额
     */
    @Column(name = "wechat_amount")
    private BigDecimal wechatAmount;

    /**
     * 会员卡本金入账金额
     */
    @Column(name = "member_principle_amount")
    private BigDecimal memberPrincipleAmount;

    /**
     * 会员卡赠金入账金额
     */
    @Column(name = "member_gift_amount")
    private BigDecimal memberGiftAmount;

    /**
     * 预付款本金入账金额
     */
    @Column(name = "prepaid_principle_amount")
    private BigDecimal prepaidPrincipleAmount;

    /**
     * 预付款赠金入账金额
     */
    @Column(name = "prepaid_gift_amount")
    private BigDecimal prepaidGiftAmount;

    /**
     * 保险入账金额
     */
    @Column(name = "insurance_amount")
    private BigDecimal insuranceAmount;

    /**
     * 银行卡入账金额
     */
    @Column(name = "bank_card_amount")
    private BigDecimal bankCardAmount;

    /**
     * 市医保
     */
    @Column(name = "city_medical_insurance_amount")
    private BigDecimal cityMedicalInsuranceAmount;

    /**
     * 省医保
     */
    @Column(name = "province_medical_insurance_amount")
    private BigDecimal provinceMedicalInsuranceAmount;

    /**
     * 获取订单收费记录ID
     *
     * @return bill_pay_id - 订单收费记录ID
     */
    public Integer getBillPayId() {
        return billPayId;
    }

    /**
     * 设置订单收费记录ID
     *
     * @param billPayId 订单收费记录ID
     */
    public void setBillPayId(Integer billPayId) {
        this.billPayId = billPayId;
    }

    /**
     * 获取订单ID
     *
     * @return bill_id - 订单ID
     */
    public Integer getBillId() {
        return billId;
    }

    /**
     * 设置订单ID
     *
     * @param billId 订单ID
     */
    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    /**
     * 获取组织ID
     *
     * @return org_id - 组织ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织ID
     *
     * @param orgId 组织ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取就诊ID
     *
     * @return treatment_id - 就诊ID
     */
    public Integer getTreatmentId() {
        return treatmentId;
    }

    /**
     * 设置就诊ID
     *
     * @param treatmentId 就诊ID
     */
    public void setTreatmentId(Integer treatmentId) {
        this.treatmentId = treatmentId;
    }

    /**
     * 获取收款人ID
     *
     * @return payee_user_id - 收款人ID
     */
    public Integer getPayeeUserId() {
        return payeeUserId;
    }

    /**
     * 设置收款人ID
     *
     * @param payeeUserId 收款人ID
     */
    public void setPayeeUserId(Integer payeeUserId) {
        this.payeeUserId = payeeUserId;
    }

    /**
     * 获取收款日期
     *
     * @return payee_date - 收款日期
     */
    public Date getPayeeDate() {
        return payeeDate;
    }

    /**
     * 设置收款日期
     *
     * @param payeeDate 收款日期
     */
    public void setPayeeDate(Date payeeDate) {
        this.payeeDate = payeeDate;
    }

    /**
     * 获取本次收费总额
     *
     * @return received_amount - 本次收费总额
     */
    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * 设置本次收费总额
     *
     * @param receivedAmount 本次收费总额
     */
    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    /**
     * 获取本次仍欠费总额
     *
     * @return still_owe_amount - 本次仍欠费总额
     */
    public BigDecimal getStillOweAmount() {
        return stillOweAmount;
    }

    /**
     * 设置本次仍欠费总额
     *
     * @param stillOweAmount 本次仍欠费总额
     */
    public void setStillOweAmount(BigDecimal stillOweAmount) {
        this.stillOweAmount = stillOweAmount;
    }

    /**
     * 获取现金入账金额
     *
     * @return cash_amount - 现金入账金额
     */
    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    /**
     * 设置现金入账金额
     *
     * @param cashAmount 现金入账金额
     */
    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }

    /**
     * 获取支付宝入账金额
     *
     * @return alipay_amount - 支付宝入账金额
     */
    public BigDecimal getAlipayAmount() {
        return alipayAmount;
    }

    /**
     * 设置支付宝入账金额
     *
     * @param alipayAmount 支付宝入账金额
     */
    public void setAlipayAmount(BigDecimal alipayAmount) {
        this.alipayAmount = alipayAmount;
    }

    /**
     * 获取微信入账金额
     *
     * @return wechat_amount - 微信入账金额
     */
    public BigDecimal getWechatAmount() {
        return wechatAmount;
    }

    /**
     * 设置微信入账金额
     *
     * @param wechatAmount 微信入账金额
     */
    public void setWechatAmount(BigDecimal wechatAmount) {
        this.wechatAmount = wechatAmount;
    }

    /**
     * 获取会员卡本金入账金额
     *
     * @return member_principle_amount - 会员卡本金入账金额
     */
    public BigDecimal getMemberPrincipleAmount() {
        return memberPrincipleAmount;
    }

    /**
     * 设置会员卡本金入账金额
     *
     * @param memberPrincipleAmount 会员卡本金入账金额
     */
    public void setMemberPrincipleAmount(BigDecimal memberPrincipleAmount) {
        this.memberPrincipleAmount = memberPrincipleAmount;
    }

    /**
     * 获取会员卡赠金入账金额
     *
     * @return member_gift_amount - 会员卡赠金入账金额
     */
    public BigDecimal getMemberGiftAmount() {
        return memberGiftAmount;
    }

    /**
     * 设置会员卡赠金入账金额
     *
     * @param memberGiftAmount 会员卡赠金入账金额
     */
    public void setMemberGiftAmount(BigDecimal memberGiftAmount) {
        this.memberGiftAmount = memberGiftAmount;
    }

    /**
     * 获取预付款本金入账金额
     *
     * @return prepaid_principle_amount - 预付款本金入账金额
     */
    public BigDecimal getPrepaidPrincipleAmount() {
        return prepaidPrincipleAmount;
    }

    /**
     * 设置预付款本金入账金额
     *
     * @param prepaidPrincipleAmount 预付款本金入账金额
     */
    public void setPrepaidPrincipleAmount(BigDecimal prepaidPrincipleAmount) {
        this.prepaidPrincipleAmount = prepaidPrincipleAmount;
    }

    /**
     * 获取预付款赠金入账金额
     *
     * @return prepaid_gift_amount - 预付款赠金入账金额
     */
    public BigDecimal getPrepaidGiftAmount() {
        return prepaidGiftAmount;
    }

    /**
     * 设置预付款赠金入账金额
     *
     * @param prepaidGiftAmount 预付款赠金入账金额
     */
    public void setPrepaidGiftAmount(BigDecimal prepaidGiftAmount) {
        this.prepaidGiftAmount = prepaidGiftAmount;
    }

    /**
     * 获取保险入账金额
     *
     * @return insurance_amount - 保险入账金额
     */
    public BigDecimal getInsuranceAmount() {
        return insuranceAmount;
    }

    /**
     * 设置保险入账金额
     *
     * @param insuranceAmount 保险入账金额
     */
    public void setInsuranceAmount(BigDecimal insuranceAmount) {
        this.insuranceAmount = insuranceAmount;
    }

    /**
     * 获取银行卡入账金额
     *
     * @return bank_card_amount - 银行卡入账金额
     */
    public BigDecimal getBankCardAmount() {
        return bankCardAmount;
    }

    /**
     * 设置银行卡入账金额
     *
     * @param bankCardAmount 银行卡入账金额
     */
    public void setBankCardAmount(BigDecimal bankCardAmount) {
        this.bankCardAmount = bankCardAmount;
    }

    /**
     * 获取市医保入账金额
     *
     * @return cityMedicalInsuranceAmount
     */
    public BigDecimal getCityMedicalInsuranceAmount() {
        return cityMedicalInsuranceAmount;
    }

    /**
     * 设置市医保入账金额
     *
     * @param cityMedicalInsuranceAmount 市医保入账金额
     */
    public void setCityMedicalInsuranceAmount(BigDecimal cityMedicalInsuranceAmount) {
        this.cityMedicalInsuranceAmount = cityMedicalInsuranceAmount;
    }

    /**
     * 获取省医保入账金额
     *
     * @return provinceMedicalInsuranceAmount
     */
    public BigDecimal getProvinceMedicalInsuranceAmount() {
        return provinceMedicalInsuranceAmount;
    }

    /**
     * 设置省医保入账金额
     *
     * @param provinceMedicalInsuranceAmount 省医保入账金额
     */
    public void setProvinceMedicalInsuranceAmount(BigDecimal provinceMedicalInsuranceAmount) {
        this.provinceMedicalInsuranceAmount = provinceMedicalInsuranceAmount;
    }
}