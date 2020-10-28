package com.yunya.models.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table(name = "base_benefit")
public class BaseBenefit {
    /**
     * 订单id
     */
    @Id
    @Column(name = "order_id")
    private Integer orderId;

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
    private Byte itemType;

    /**
     * 使用的优惠类型（0：会员卡  1：优惠券）
     */
    @Column(name = "benefit_type")
    private Byte benefitType;

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

    /**
     * 选择使用的优惠方式（0-卡券优惠 1-授权折扣）
     */
    @Column(name = "choice_benefit_type")
    private Byte choiceBenefitType;

    /**
     * 优惠操作人
     */
    @Column(name = "operate_user_id")
    private Integer operateUserId;

    /**
     * 优惠操作人
     */
    @Column(name = "use_date")
    private LocalDateTime useDate;
}