package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Table(name = "coupon_order")
public class CouponOrder {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 组织（门诊）ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 订单编号
     */
    @Column(name = "order_record_num")
    private String orderRecordNum;

    /**
     * 状态 （0-已下单；1-已收费；2-已退款）
     */
    private Integer status;

    /**
     * 订单总额
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 应收金额（实际支付金额）
     */
    @Column(name = "receivable_amount")
    private BigDecimal receivableAmount;

    /**
     * 已收金额（本单收费总额）
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 备注
     */
    private String remarks;

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
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

}