package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_coupon_bill_pay_detail")
public class BaseCouponBillPayDetail {
    /**
     * 账单支付明细记录ID
     */
    @Id
    @Column(name = "bill_pay_detail_id")
    private Integer billPayDetailId;

    /**
     * 订单id
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 账单收费记录ID
     */
    @Column(name = "bill_pay_id")
    private Integer billPayId;

    /**
     * 入账方式明细ID
     */
    @Column(name = "account_item_id")
    private Integer accountItemId;

    /**
     * 入账方式明细
     */
    @Column(name = "account_item_name")
    private String accountItemName;

    /**
     * 入账方式类型（0-预付款；1-会员卡；2-其他支付方式）
     */
    private Byte type;

    /**
     * 入账金额
     */
    private BigDecimal amount;

    /**
     * 消费本金
     */
    @Column(name = "principal_amount")
    private BigDecimal principalAmount;

    /**
     * 消费赠金
     */
    @Column(name = "bonus_amount")
    private BigDecimal bonusAmount;

    /**
     * 会员卡号或预付款卡号
     */
    @Column(name = "patient_num")
    private String patientNum;

    /**
     * 获取账单支付明细记录ID
     *
     * @return bill_pay_detail_id - 账单支付明细记录ID
     */
    public Integer getBillPayDetailId() {
        return billPayDetailId;
    }

    /**
     * 设置账单支付明细记录ID
     *
     * @param billPayDetailId 账单支付明细记录ID
     */
    public void setBillPayDetailId(Integer billPayDetailId) {
        this.billPayDetailId = billPayDetailId;
    }

    /**
     * 获取订单id
     *
     * @return order_id - 订单id
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单id
     *
     * @param orderId 订单id
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取账单收费记录ID
     *
     * @return bill_pay_id - 账单收费记录ID
     */
    public Integer getBillPayId() {
        return billPayId;
    }

    /**
     * 设置账单收费记录ID
     *
     * @param billPayId 账单收费记录ID
     */
    public void setBillPayId(Integer billPayId) {
        this.billPayId = billPayId;
    }

    /**
     * 获取入账方式明细ID
     *
     * @return account_item_id - 入账方式明细ID
     */
    public Integer getAccountItemId() {
        return accountItemId;
    }

    /**
     * 设置入账方式明细ID
     *
     * @param accountItemId 入账方式明细ID
     */
    public void setAccountItemId(Integer accountItemId) {
        this.accountItemId = accountItemId;
    }

    /**
     * 获取入账方式明细
     *
     * @return account_item_name - 入账方式明细
     */
    public String getAccountItemName() {
        return accountItemName;
    }

    /**
     * 设置入账方式明细
     *
     * @param accountItemName 入账方式明细
     */
    public void setAccountItemName(String accountItemName) {
        this.accountItemName = accountItemName;
    }

    /**
     * 获取入账方式类型（0-预付款；1-会员卡；2-其他支付方式）
     *
     * @return type - 入账方式类型（0-预付款；1-会员卡；2-其他支付方式）
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置入账方式类型（0-预付款；1-会员卡；2-其他支付方式）
     *
     * @param type 入账方式类型（0-预付款；1-会员卡；2-其他支付方式）
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取入账金额
     *
     * @return amount - 入账金额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置入账金额
     *
     * @param amount 入账金额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 获取消费本金
     *
     * @return principal_amount - 消费本金
     */
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    /**
     * 设置消费本金
     *
     * @param principalAmount 消费本金
     */
    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    /**
     * 获取消费赠金
     *
     * @return bonus_amount - 消费赠金
     */
    public BigDecimal getBonusAmount() {
        return bonusAmount;
    }

    /**
     * 设置消费赠金
     *
     * @param bonusAmount 消费赠金
     */
    public void setBonusAmount(BigDecimal bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    /**
     * 获取会员卡号或预付款卡号
     *
     * @return patient_num - 会员卡号或预付款卡号
     */
    public String getPatientNum() {
        return patientNum;
    }

    /**
     * 设置会员卡号或预付款卡号
     *
     * @param patientNum 会员卡号或预付款卡号
     */
    public void setPatientNum(String patientNum) {
        this.patientNum = patientNum;
    }
}