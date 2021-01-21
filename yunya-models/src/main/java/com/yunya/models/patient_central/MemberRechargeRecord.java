package com.yunya.models.patient_central;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "member_recharge_record")
public class MemberRechargeRecord {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 诊所ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 会员卡号
     */
    @Column(name = "member_id")
    private String memberId;

    /**
     * 充值本金
     */
    @Column(name = "recharge_principal")
    private BigDecimal rechargePrincipal;

    /**
     * 充值赠金
     */
    @Column(name = "recharge_bonus")
    private BigDecimal rechargeBonus;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人ID
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
     * 更新人ID
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
     * 充值后当前本金
     */
    @Column(name = "current_recharge_principal")
    private BigDecimal currentRechargePrincipal;

    /**
     * 充值后当前赠金
     */
    @Column(name = "current_recharge_bonus")
    private BigDecimal currentRechargeBonus;

    /**
     * 操作类型
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 订单id
     */
    @Column(name = "order_record_id")
    private Integer orderRecordId;

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

}