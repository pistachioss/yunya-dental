package com.yunya.models.discount;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "discount_coupon")
public class DiscountCoupon {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 优惠券id
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 折扣率
     */
    @Column(name = "discount_rate")
    private BigDecimal discountRate;

    /**
     * 激活截至日期(产品有效期)
     */
    @Column(name = "activation_deadline")
    private Date activationDeadline;

    /**
     * 卡券激活后有效期
     */
    @Column(name = "effective_days")
    private Integer effectiveDays;

    /**
     * 是否可与他人共享
     */
    @Column(name = "is_share")
    private Boolean isShare;

    /**
     * 可使用门诊
     */
    @Column(name = "useable_clinic")
    private String useableClinic;

    /**
     * 工作量比例
     */
    @Column(name = "workload_rate")
    private BigDecimal workloadRate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
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

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
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
     * 获取折扣率
     *
     * @return discount_rate - 折扣率
     */
    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    /**
     * 设置折扣率
     *
     * @param discountRate 折扣率
     */
    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    /**
     * 获取激活截至日期(产品有效期)
     *
     * @return activation_deadline - 激活截至日期(产品有效期)
     */
    public Date getActivationDeadline() {
        return activationDeadline;
    }

    /**
     * 设置激活截至日期(产品有效期)
     *
     * @param activationDeadline 激活截至日期(产品有效期)
     */
    public void setActivationDeadline(Date activationDeadline) {
        this.activationDeadline = activationDeadline;
    }

    /**
     * 获取卡券激活后有效期
     *
     * @return effective_days - 卡券激活后有效期
     */
    public Integer getEffectiveDays() {
        return effectiveDays;
    }

    /**
     * 设置卡券激活后有效期
     *
     * @param effectiveDays 卡券激活后有效期
     */
    public void setEffectiveDays(Integer effectiveDays) {
        this.effectiveDays = effectiveDays;
    }

    /**
     * 获取是否可与他人共享
     *
     * @return is_share - 是否可与他人共享
     */
    public Boolean getIsShare() {
        return isShare;
    }

    /**
     * 设置是否可与他人共享
     *
     * @param isShare 是否可与他人共享
     */
    public void setIsShare(Boolean isShare) {
        this.isShare = isShare;
    }

    /**
     * 获取可使用门诊
     *
     * @return useable_clinic - 可使用门诊
     */
    public String getUseableClinic() {
        return useableClinic;
    }

    /**
     * 设置可使用门诊
     *
     * @param useableClinic 可使用门诊
     */
    public void setUseableClinic(String useableClinic) {
        this.useableClinic = useableClinic;
    }

    /**
     * 获取工作量比例
     *
     * @return workload_rate - 工作量比例
     */
    public BigDecimal getWorkloadRate() {
        return workloadRate;
    }

    /**
     * 设置工作量比例
     *
     * @param workloadRate 工作量比例
     */
    public void setWorkloadRate(BigDecimal workloadRate) {
        this.workloadRate = workloadRate;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取创建人
     *
     * @return crt_id - 创建人
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人
     *
     * @param crtId 创建人
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
     * 获取更新人
     *
     * @return upd_id - 更新人
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人
     *
     * @param updId 更新人
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取更新时间
     *
     * @return upd_time - 更新时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置更新时间
     *
     * @param updTime 更新时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}