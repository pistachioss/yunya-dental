package com.yunya.models.middletable;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "base_patient_member_occur_log")
public class BasePatientMemberOccurLog {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "occur_log_id")
    private Integer occurLogId;

    /**
     * 卡ID
     */
    @Column(name = "card_id")
    private Integer cardId;

    /**
     * 患者ID(产生)
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 卡类型(0：会员卡；１：预付款)
     */
    @Id
    private Byte type;

    /**
     * 发生类型(1充值2消费3退款4撤销收费)
     */
    @Id
    @Column(name = "occur_type")
    private Byte occurType;

    /**
     * 本金金额
     */
    @Column(name = "principal_amount")
    private BigDecimal principalAmount;

    /**
     * 赠金金额
     */
    @Column(name = "bonus_amount")
    private BigDecimal bonusAmount;

    /**
     * 发生点本金余额
     */
    @Column(name = "current_recharge_principal")
    private BigDecimal currentRechargePrincipal;

    /**
     * 发生点赠金余额
     */
    @Column(name = "current_recharge_bonus")
    private BigDecimal currentRechargeBonus;

    /**
     * 发生方式id
     */
    @Column(name = "payment_id")
    private Integer paymentId;

    /**
     * 发生方式名称(入账、退费，充值卡作为一种充值方式记录)
     */
    @Column(name = "payment_manner")
    private String paymentManner;

    /**
     * 操作人
     */
    @Column(name = "operator_user_id")
    private Integer operatorUserId;

    /**
     * 备注/原因
     */
    private String remarks;

    /**
     * 账单ID
     */
    @Column(name = "bill_id")
    private Integer billId;

    /**
     * 充值卡号
     */
    @Column(name = "recharge_card_number")
    private String rechargeCardNumber;

    /**
     * 发生日期
     */
    @Column(name = "occur_date")
    private Date occurDate;

    /**
     * 获取主键ID
     *
     * @return occur_log_id - 主键ID
     */
    public Integer getOccurLogId() {
        return occurLogId;
    }

    /**
     * 设置主键ID
     *
     * @param occurLogId 主键ID
     */
    public void setOccurLogId(Integer occurLogId) {
        this.occurLogId = occurLogId;
    }

    /**
     * 获取卡ID
     *
     * @return card_id - 卡ID
     */
    public Integer getCardId() {
        return cardId;
    }

    /**
     * 设置卡ID
     *
     * @param cardId 卡ID
     */
    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    /**
     * 获取患者ID(产生)
     *
     * @return patient_id - 患者ID(产生)
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者ID(产生)
     *
     * @param patientId 患者ID(产生)
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取卡类型(0：会员卡；１：预付款)
     *
     * @return type - 卡类型(0：会员卡；１：预付款)
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置卡类型(0：会员卡；１：预付款)
     *
     * @param type 卡类型(0：会员卡；１：预付款)
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取发生类型(1充值2消费3退款4撤销收费)
     *
     * @return occur_type - 发生类型(1充值2消费3退款4撤销收费)
     */
    public Byte getOccurType() {
        return occurType;
    }

    /**
     * 设置发生类型(1充值2消费3退款4撤销收费)
     *
     * @param occurType 发生类型(1充值2消费3退款4撤销收费)
     */
    public void setOccurType(Byte occurType) {
        this.occurType = occurType;
    }

    /**
     * 获取本金金额
     *
     * @return principal_amount - 本金金额
     */
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    /**
     * 设置本金金额
     *
     * @param principalAmount 本金金额
     */
    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    /**
     * 获取赠金金额
     *
     * @return bonus_amount - 赠金金额
     */
    public BigDecimal getBonusAmount() {
        return bonusAmount;
    }

    /**
     * 设置赠金金额
     *
     * @param bonusAmount 赠金金额
     */
    public void setBonusAmount(BigDecimal bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    /**
     * 获取发生点本金余额
     *
     * @return current_recharge_principal - 发生点本金余额
     */
    public BigDecimal getCurrentRechargePrincipal() {
        return currentRechargePrincipal;
    }

    /**
     * 设置发生点本金余额
     *
     * @param currentRechargePrincipal 发生点本金余额
     */
    public void setCurrentRechargePrincipal(BigDecimal currentRechargePrincipal) {
        this.currentRechargePrincipal = currentRechargePrincipal;
    }

    /**
     * 获取发生点赠金余额
     *
     * @return current_recharge_bonus - 发生点赠金余额
     */
    public BigDecimal getCurrentRechargeBonus() {
        return currentRechargeBonus;
    }

    /**
     * 设置发生点赠金余额
     *
     * @param currentRechargeBonus 发生点赠金余额
     */
    public void setCurrentRechargeBonus(BigDecimal currentRechargeBonus) {
        this.currentRechargeBonus = currentRechargeBonus;
    }

    /**
     * 获取发生方式id
     *
     * @return payment_id - 发生方式id
     */
    public Integer getPaymentId() {
        return paymentId;
    }

    /**
     * 设置发生方式id
     *
     * @param paymentId 发生方式id
     */
    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * 获取发生方式名称(入账、退费，充值卡作为一种充值方式记录)
     *
     * @return payment_manner - 发生方式名称(入账、退费，充值卡作为一种充值方式记录)
     */
    public String getPaymentManner() {
        return paymentManner;
    }

    /**
     * 设置发生方式名称(入账、退费，充值卡作为一种充值方式记录)
     *
     * @param paymentManner 发生方式名称(入账、退费，充值卡作为一种充值方式记录)
     */
    public void setPaymentManner(String paymentManner) {
        this.paymentManner = paymentManner;
    }

    /**
     * 获取操作人
     *
     * @return operator_user_id - 操作人
     */
    public Integer getOperatorUserId() {
        return operatorUserId;
    }

    /**
     * 设置操作人
     *
     * @param operatorUserId 操作人
     */
    public void setOperatorUserId(Integer operatorUserId) {
        this.operatorUserId = operatorUserId;
    }

    /**
     * 获取备注/原因
     *
     * @return remarks - 备注/原因
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注/原因
     *
     * @param remarks 备注/原因
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
     * 获取充值卡号
     *
     * @return recharge_card_number - 充值卡号
     */
    public String getRechargeCardNumber() {
        return rechargeCardNumber;
    }

    /**
     * 设置充值卡号
     *
     * @param rechargeCardNumber 充值卡号
     */
    public void setRechargeCardNumber(String rechargeCardNumber) {
        this.rechargeCardNumber = rechargeCardNumber;
    }

    /**
     * 获取发生日期
     *
     * @return occur_date - 发生日期
     */
    public Date getOccurDate() {
        return occurDate;
    }

    /**
     * 设置发生日期
     *
     * @param occurDate 发生日期
     */
    public void setOccurDate(Date occurDate) {
        this.occurDate = occurDate;
    }
}