package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_coupon_refund_pay")
public class BaseCouponRefundPay {
    /**
     * 账单退费支付明细ID
     */
    @Id
    @Column(name = "bill_refund_pay_id")
    private Integer billRefundPayId;

    /**
     * 账单退费记录ID
     */
    @Column(name = "refund_id")
    private Integer refundId;

    /**
     * 支付方式ID
     */
    @Column(name = "account_item_id")
    private Integer accountItemId;

    @Column(name = "account_item_name")
    private String accountItemName;

    /**
     * 预付款号或会员卡号或支付方式id
     */
    @Column(name = "patient_number")
    private String patientNumber;

    /**
     * 该明细退款总额
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 退款本金
     */
    @Column(name = "principal_amount")
    private BigDecimal principalAmount;

    /**
     * 退款赠金
     */
    @Column(name = "bonus_amount")
    private BigDecimal bonusAmount;

    /**
     * 获取账单退费支付明细ID
     *
     * @return bill_refund_pay_id - 账单退费支付明细ID
     */
    public Integer getBillRefundPayId() {
        return billRefundPayId;
    }

    /**
     * 设置账单退费支付明细ID
     *
     * @param billRefundPayId 账单退费支付明细ID
     */
    public void setBillRefundPayId(Integer billRefundPayId) {
        this.billRefundPayId = billRefundPayId;
    }

    /**
     * 获取账单退费记录ID
     *
     * @return refund_id - 账单退费记录ID
     */
    public Integer getRefundId() {
        return refundId;
    }

    /**
     * 设置账单退费记录ID
     *
     * @param refundId 账单退费记录ID
     */
    public void setRefundId(Integer refundId) {
        this.refundId = refundId;
    }

    /**
     * 获取支付方式ID
     *
     * @return account_item_id - 支付方式ID
     */
    public Integer getAccountItemId() {
        return accountItemId;
    }

    /**
     * 设置支付方式ID
     *
     * @param accountItemId 支付方式ID
     */
    public void setAccountItemId(Integer accountItemId) {
        this.accountItemId = accountItemId;
    }

    /**
     * @return account_item_name
     */
    public String getAccountItemName() {
        return accountItemName;
    }

    /**
     * @param accountItemName
     */
    public void setAccountItemName(String accountItemName) {
        this.accountItemName = accountItemName;
    }

    /**
     * 获取预付款号或会员卡号或支付方式id
     *
     * @return patient_number - 预付款号或会员卡号或支付方式id
     */
    public String getPatientNumber() {
        return patientNumber;
    }

    /**
     * 设置预付款号或会员卡号或支付方式id
     *
     * @param patientNumber 预付款号或会员卡号或支付方式id
     */
    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    /**
     * 获取该明细退款总额
     *
     * @return total_amount - 该明细退款总额
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置该明细退款总额
     *
     * @param totalAmount 该明细退款总额
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取退款本金
     *
     * @return principal_amount - 退款本金
     */
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    /**
     * 设置退款本金
     *
     * @param principalAmount 退款本金
     */
    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    /**
     * 获取退款赠金
     *
     * @return bonus_amount - 退款赠金
     */
    public BigDecimal getBonusAmount() {
        return bonusAmount;
    }

    /**
     * 设置退款赠金
     *
     * @param bonusAmount 退款赠金
     */
    public void setBonusAmount(BigDecimal bonusAmount) {
        this.bonusAmount = bonusAmount;
    }
}