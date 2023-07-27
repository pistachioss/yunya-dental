package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Table(name = "coupon_refund_detail")
public class CouponRefundDetail {
    /**
     * 退费项目明细ID
     */
    @Id
    private Integer id;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 订单卡券id
     */
    @Column(name = "order_virtual_id")
    private Integer orderVirtualId;

    /**
     * 卡券id
     */
    @Column(name = "card_id")
    private Integer cardId;

    /**
     * 退费记录ID
     */
    @Column(name = "refund_id")
    private Integer refundId;

    /**
     * 退费金额
     */
    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

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