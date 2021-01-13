package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_bill_pay_detail")
public class BaseBillPayDetail {
    /**
     * 账单支付明细记录ID
     */
    @Id
    @Column(name = "bill_pay_detail_record_id")
    private Integer billPayDetailRecordId;

    /**
     * 账单ID
     */
    @Column(name = "bill_id")
    private Integer billId;

    /**
     * 账单收费记录ID
     */
    @Column(name = "bill_pay_id")
    private Integer billPayId;

    /**
     * 入账方式类型（0-预付款；1-会员卡；2-其他支付方式）
     */
    private Byte type;

    /**
     * 入账方式明细ID
     */
    @Column(name = "account_item_id")
    private Integer accountItemId;

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
     * 卡号
     */
    @Column(name = "card_num")
    private String cardNum;

    /**
     * 获取账单支付明细记录ID
     *
     * @return bill_pay_detail_record_id - 账单支付明细记录ID
     */
    public Integer getBillPayDetailRecordId() {
        return billPayDetailRecordId;
    }

    /**
     * 设置账单支付明细记录ID
     *
     * @param billPayDetailRecordId 账单支付明细记录ID
     */
    public void setBillPayDetailRecordId(Integer billPayDetailRecordId) {
        this.billPayDetailRecordId = billPayDetailRecordId;
    }

    /**
     * 获取账单ID
     *
     * @return bill_id - 账单ID
     */
    public Integer getBillId() {
        return billId;
    }

    /**
     * 设置账单ID
     *
     * @param billId 账单ID
     */
    public void setBillId(Integer billId) {
        this.billId = billId;
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

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }
}