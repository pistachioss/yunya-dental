package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Table(name = "coupon_bill_pay_detail")
public class CouponBillPayDetail {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 组织（诊所）id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 开单记录id
     */
    @Column(name = "order_id")
    private Integer orderId;

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
     * 入账方式明细ID
     */
    @Column(name = "account_item_name")
    private String accountItemName;

    /**
     * 入账金额
     */
    private BigDecimal amount;

    @Column(name = "principal_amount")
    private BigDecimal principalAmount;

    @Column(name = "bonus_amount")
    private BigDecimal bonusAmount;


    /**
     * 备注
     */
    private String remark;

    @Column(name = "patient_num")
    private String patientNum;

    /**
     * 是否有效
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
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

}