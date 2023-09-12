package com.yunya.models.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_coupon_refund_detail")
@Data
public class BaseCouponRefundDetail {
    /**
     * 退费明细记录ID
     */
    @Id
    @Column(name = "refund_detail_id")
    private Integer refundDetailId;

    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 退费记录ID
     */
    @Column(name = "refund_id")
    private Integer refundId;

    /**
     * 订单明细ID
     */
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    @Column(name = "order_virtual_id")
    private Integer orderVirtualId;

    /**
     * 卡券id
     */
    @Column(name = "card_id")
    private Integer cardId;

    /**
     * 退费金额
     */
    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    /**
     * 执行人id
     */
    @Column(name = "executor_id")
    private Integer executorId;

    /**
     * 咨询死ID
     */
    @Column(name = "consulter_id")
    private Integer consulterId;

    private Integer type;


    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "coupon_name")
    private String couponName;

}