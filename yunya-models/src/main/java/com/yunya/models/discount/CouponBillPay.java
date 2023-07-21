package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Table(name = "coupon_bill_pay")
public class CouponBillPay {
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 组织id
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
     * 账单记录ID
     */
    @Column(name = "bill_id")
    private Integer billId;

    /**
     * 本次收费金额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 欠费金额
     */
    @Column(name = "owe_amount")
    private BigDecimal oweAmount;

    /**
     * 是否有效
     */
    private Boolean inservice;

    /**
     * 收款人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 收款时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;
}