package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Table(name = "coupon_bill")
public class CouponBill {
    /**
     * 账单ID
     */
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 组织（门诊）id
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
    @Column(name = "order_record_id")
    private Integer orderRecordId;

    /**
     * 账单编号（ZD+门诊ID+时间戳）
     */
    @Column(name = "bill_number")
    private String billNumber;

    /**
     * 原价
     */
    private BigDecimal price;

    /**
     * 应收金额
     */
    @Column(name = "receivable_amount")
    private BigDecimal receivableAmount;

    /**
     * 已收金额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 欠费金额（本单欠费）
     */
    @Column(name = "debt_amount")
    private BigDecimal debtAmount;

    /**
     * 是否开发票
     */
    private Boolean invoice;

    /**
     * 发票编号
     */
    @Column(name = "invoice_number")
    private String invoiceNumber;

    /**
     * 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
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