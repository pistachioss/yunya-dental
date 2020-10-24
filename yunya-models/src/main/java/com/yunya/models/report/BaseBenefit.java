package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

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
     * 获取订单id
     *
     * @return order_id - 订单id
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单id
     *
     * @param orderId 订单id
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取组织id
     *
     * @return org_id - 组织id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织id
     *
     * @param orgId 组织id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取优惠券id
     *
     * @return coupon_id - 优惠券id
     */
    public Integer getCouponId() {
        return couponId;
    }

    /**
     * 设置优惠券id
     *
     * @param couponId 优惠券id
     */
    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    /**
     * 获取卡券id
     *
     * @return card_id - 卡券id
     */
    public Integer getCardId() {
        return cardId;
    }

    /**
     * 设置卡券id
     *
     * @param cardId 卡券id
     */
    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    /**
     * 获取订单明细id
     *
     * @return order_detail_id - 订单明细id
     */
    public Integer getOrderDetailId() {
        return orderDetailId;
    }

    /**
     * 设置订单明细id
     *
     * @param orderDetailId 订单明细id
     */
    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    /**
     * 获取患者id（优惠券使用人）
     *
     * @return patient_id - 患者id（优惠券使用人）
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者id（优惠券使用人）
     *
     * @param patientId 患者id（优惠券使用人）
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取项目id
     *
     * @return item_id - 项目id
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 设置项目id
     *
     * @param itemId 项目id
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 获取项目类型（0-价目 1-商品）
     *
     * @return item_type - 项目类型（0-价目 1-商品）
     */
    public Byte getItemType() {
        return itemType;
    }

    /**
     * 设置项目类型（0-价目 1-商品）
     *
     * @param itemType 项目类型（0-价目 1-商品）
     */
    public void setItemType(Byte itemType) {
        this.itemType = itemType;
    }

    /**
     * 获取使用的优惠类型（0：会员卡  1：优惠券）
     *
     * @return benefit_type - 使用的优惠类型（0：会员卡  1：优惠券）
     */
    public Byte getBenefitType() {
        return benefitType;
    }

    /**
     * 设置使用的优惠类型（0：会员卡  1：优惠券）
     *
     * @param benefitType 使用的优惠类型（0：会员卡  1：优惠券）
     */
    public void setBenefitType(Byte benefitType) {
        this.benefitType = benefitType;
    }

    /**
     * 获取项目使用优惠下标（记录哪一个数量）
     *
     * @return item_index - 项目使用优惠下标（记录哪一个数量）
     */
    public Integer getItemIndex() {
        return itemIndex;
    }

    /**
     * 设置项目使用优惠下标（记录哪一个数量）
     *
     * @param itemIndex 项目使用优惠下标（记录哪一个数量）
     */
    public void setItemIndex(Integer itemIndex) {
        this.itemIndex = itemIndex;
    }

    /**
     * 获取优惠金额
     *
     * @return benefit_amount - 优惠金额
     */
    public BigDecimal getBenefitAmount() {
        return benefitAmount;
    }

    /**
     * 设置优惠金额
     *
     * @param benefitAmount 优惠金额
     */
    public void setBenefitAmount(BigDecimal benefitAmount) {
        this.benefitAmount = benefitAmount;
    }

    /**
     * 获取选择使用的优惠方式（0-卡券优惠 1-授权折扣）
     *
     * @return choice_benefit_type - 选择使用的优惠方式（0-卡券优惠 1-授权折扣）
     */
    public Byte getChoiceBenefitType() {
        return choiceBenefitType;
    }

    /**
     * 设置选择使用的优惠方式（0-卡券优惠 1-授权折扣）
     *
     * @param choiceBenefitType 选择使用的优惠方式（0-卡券优惠 1-授权折扣）
     */
    public void setChoiceBenefitType(Byte choiceBenefitType) {
        this.choiceBenefitType = choiceBenefitType;
    }
}