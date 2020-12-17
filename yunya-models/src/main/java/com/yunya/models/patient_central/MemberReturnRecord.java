package com.yunya.models.patient_central;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "member_return_record")
public class MemberReturnRecord {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 会员卡号
     */
    @Column(name = "member_id")
    private String memberId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 退还本金
     */
    @Column(name = "return_principal_amount")
    private BigDecimal returnPrincipalAmount;

    /**
     * 退还赠金
     */
    @Column(name = "return_gift_amount")
    private BigDecimal returnGiftAmount;

    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

    /**
     * 退费方式ID
     */
    @Column(name = "return_way_id")
    private Integer returnWayId;

    /**
     * 退费方式
     */
    @Column(name = "return_way_type")
    private String returnWayType;

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
     * 是否有效 是否有效
     */
    @Column(name = "inservice")
    private Boolean inservice;

    /**
     * 实际退还本金
     */
    @Column(name = "actual_return_amount")
    private BigDecimal actualReturnAmount;

}