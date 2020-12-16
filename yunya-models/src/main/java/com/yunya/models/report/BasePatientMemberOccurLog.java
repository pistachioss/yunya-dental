package com.yunya.models.report;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Data
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
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 充值方式
     */
    @Column(name = "recharge_method")
    private Byte rechargeMethod;

    /**
     * 是否启用
     */
    private Boolean inservice;

    /**
     * 入账金额
     */
    @Column(name = "credit_amount")
    private BigDecimal creditAmount;
}