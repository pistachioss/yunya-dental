package com.yunya.models.patient_central;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Data
@Table(name = "prepaid_expend_record")
public class PrepaidExpendRecord {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 预付款id
     */
    @Column(name = "prepaid_id")
    private String prepaidId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 消费本金
     */
    @Column(name = "expend_principal")
    private BigDecimal expendPrincipal;

    /**
     * 消费赠金
     */
    @Column(name = "expend_gift")
    private BigDecimal expendGift;

    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 更新人姓名
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 就诊id
     */
    @Column(name = "treatment_record_id")
    private Integer treatmentRecordId;

    /**
     * 账单id
     */
    @Column(name = "bill_record_id")
    private Integer billRecordId;

    /**
     * 账单付款记录id
     */
    @Column(name = "bill_pay_record_id")
    private Integer billPayRecordId;

    /**
     * 类型（0：撤销消费 1：消费 ）
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 消费后当前本金
     */
    @Column(name = "current_recharge_principal")
    private BigDecimal currentPrincipal;

    /**
     * 消费后当前赠金
     */
    @Column(name = "current_recharge_bonus")
    private BigDecimal currentBonus;

    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;


    /**
     * 订单记录id
     */
    @Column(name = "order_record_id")
    private Integer orderRecordId;
}