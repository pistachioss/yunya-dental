package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_refund_pay_detail")
public class BaseRefundPayDetail {
    /**
     * 账单退费支付明细ID
     */
    @Id
    @Column(name = "bill_refund_pay_detail_record_id")
    private Integer billRefundPayDetailRecordId;

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
     * @return bill_refund_pay_detail_record_id - 账单退费支付明细ID
     */
    public Integer getBillRefundPayDetailRecordId() {
        return billRefundPayDetailRecordId;
    }

    /**
     * 设置账单退费支付明细ID
     *
     * @param billRefundPayDetailRecordId 账单退费支付明细ID
     */
    public void setBillRefundPayDetailRecordId(Integer billRefundPayDetailRecordId) {
        this.billRefundPayDetailRecordId = billRefundPayDetailRecordId;
    }

    /**
     * 获取账单退费记录ID
     *
     * @return refundId - 账单退费记录ID
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