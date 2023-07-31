package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Table(name = "coupon_refund_pay")
public class CouponRefundPay {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 退费记录ID
     */
    @Column(name = "refund_id")
    private Integer refundId;

    /**
     * 退费支付方式ID
     */
    @Column(name = "account_item_id")
    private Integer accountItemId;

    @Column(name = "account_item_name")
    private String accountItemName;

    /**
     * 退费付款合计金额
     */
    @Column(name = "refund_pay_amount")
    private BigDecimal refundPayAmount;

    /**
     * 该明细退款总额
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 退款本金
     */
    @Column(name = "principal_amount")
    private BigDecimal principalAmount;

    /**
     * 退款赠金
     */
    @Column(name = "gift_amount")
    private BigDecimal giftAmount;

    /**
     * 预付款号或会员卡号或支付方式id
     */
    @Column(name = "patient_number")
    private String patientNumber;

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