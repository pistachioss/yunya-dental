package com.yunya.models.middletable;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "base_coupon")
public class BaseCoupon {
    /**
     * 产品ID
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 产品名称
     */
    @Column(name = "coupon_name")
    private String couponName;

    /**
     * 产品分类ID
     */
    @Column(name = "product_type_id")
    private Integer productTypeId;

    /**
     * 产品分类名称
     */
    @Column(name = "product_type_name")
    private String productTypeName;

    /**
     * 卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）
     */
    @Column(name = "coupon_type")
    private Byte couponType;

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
     * 售出金额（本金）
     */
    @Column(name = "sold_amount")
    private BigDecimal soldAmount;

    /**
     * 赠金
     */
    private BigDecimal bonus;

    /**
     * 补入工作量折扣率
     */
    @Column(name = "workload_rate")
    private BigDecimal workloadRate;

    /**
     * 获取产品ID
     *
     * @return coupon_id - 产品ID
     */
    public Integer getCouponId() {
        return couponId;
    }

    /**
     * 设置产品ID
     *
     * @param couponId 产品ID
     */
    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    /**
     * 获取产品名称
     *
     * @return coupon_name - 产品名称
     */
    public String getCouponName() {
        return couponName;
    }

    /**
     * 设置产品名称
     *
     * @param couponName 产品名称
     */
    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    /**
     * 获取产品分类ID
     *
     * @return product_type_id - 产品分类ID
     */
    public Integer getProductTypeId() {
        return productTypeId;
    }

    /**
     * 设置产品分类ID
     *
     * @param productTypeId 产品分类ID
     */
    public void setProductTypeId(Integer productTypeId) {
        this.productTypeId = productTypeId;
    }

    /**
     * 获取产品分类名称
     *
     * @return product_type_name - 产品分类名称
     */
    public String getProductTypeName() {
        return productTypeName;
    }

    /**
     * 设置产品分类名称
     *
     * @param productTypeName 产品分类名称
     */
    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    /**
     * 获取卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）
     *
     * @return coupon_type - 卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）
     */
    public Byte getCouponType() {
        return couponType;
    }

    /**
     * 设置卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）
     *
     * @param couponType 卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）
     */
    public void setCouponType(Byte couponType) {
        this.couponType = couponType;
    }

    /**
     * 获取创建人ID
     *
     * @return crt_id - 创建人ID
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人ID
     *
     * @param crtId 创建人ID
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取创建时间
     *
     * @return crt_time - 创建时间
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建时间
     *
     * @param crtTime 创建时间
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * 获取售出金额（本金）
     *
     * @return sold_amount - 售出金额（本金）
     */
    public BigDecimal getSoldAmount() {
        return soldAmount;
    }

    /**
     * 设置售出金额（本金）
     *
     * @param soldAmount 售出金额（本金）
     */
    public void setSoldAmount(BigDecimal soldAmount) {
        this.soldAmount = soldAmount;
    }

    /**
     * 获取赠金
     *
     * @return bonus - 赠金
     */
    public BigDecimal getBonus() {
        return bonus;
    }

    /**
     * 设置赠金
     *
     * @param bonus 赠金
     */
    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    /**
     * 获取补入工作量折扣率
     *
     * @return workload_rate - 补入工作量折扣率
     */
    public BigDecimal getWorkloadRate() {
        return workloadRate;
    }

    /**
     * 设置补入工作量折扣率
     *
     * @param workloadRate 补入工作量折扣率
     */
    public void setWorkloadRate(BigDecimal workloadRate) {
        this.workloadRate = workloadRate;
    }
}