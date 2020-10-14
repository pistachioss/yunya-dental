package com.yunya.models.middletable;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_benefit")
public class BaseBenefit {
    /**
     * 组织id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 优惠券id
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 卡券id
     */
    @Column(name = "card_id")
    private Integer cardId;

    /**
     * 订单id
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 订单明细id
     */
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    /**
     * 患者id（优惠券使用人）
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 项目id
     */
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 项目类型（0-价目 1-商品）
     */
    @Column(name = "item_type")
    private Integer itemType;

    /**
     * 使用的优惠类型（0：会员卡  1：优惠券）
     */
    @Column(name = "benefit_type")
    private Integer benefitType;

    /**
     * 项目使用优惠下标（记录哪一个数量）
     */
    @Column(name = "item_index")
    private Integer itemIndex;

    /**
     * 优惠金额
     */
    @Column(name = "benefit_amount")
    private BigDecimal benefitAmount;

}