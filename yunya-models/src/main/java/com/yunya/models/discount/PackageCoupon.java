package com.yunya.models.discount;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "package_coupon")
public class PackageCoupon {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 优惠券id
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 激活截至日期(产品有效期)
     */
    @Column(name = "activation_deadline")
    private Date activationDeadline;

    /**
     * 激活后有效期
     */
    @Column(name = "effective_days")
    private Integer effectiveDays;

    /**
     * 是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用
     */
    private Integer mixable;

    /**
     * 使用方式 0:一次使用 1:多次使用
     */
    @Column(name = "use_way")
    private Byte useWay;

    /**
     * 可使用门诊
     */
    @Column(name = "useable_clinic")
    private String useableClinic;

    /**
     * 是否可与他人共享
     */
    @Column(name = "is_share")
    private Boolean isShare;

    /**
     * 账单单次使用限制数量
     */
    @Column(name = "limit_count")
    private Integer limitCount;

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
     * 获取激活后有效期
     *
     * @return effective_days - 激活后有效期
     */
    public Integer getEffectiveDays() {
        return effectiveDays;
    }

    /**
     * 设置激活后有效期
     *
     * @param effectiveDays 激活后有效期
     */
    public void setEffectiveDays(Integer effectiveDays) {
        this.effectiveDays = effectiveDays;
    }

    /**
     * 获取是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用
     *
     * @return mixable - 是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用
     */
    public Integer getMixable() {
        return mixable;
    }

    /**
     * 设置是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用
     *
     * @param mixable 是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用
     */
    public void setMixable(Integer mixable) {
        this.mixable = mixable;
    }

    /**
     * 获取使用方式 0:一次使用 1:多次使用
     *
     * @return use_way - 使用方式 0:一次使用 1:多次使用
     */
    public Byte getUseWay() {
        return useWay;
    }

    /**
     * 设置使用方式 0:一次使用 1:多次使用
     *
     * @param useWay 使用方式 0:一次使用 1:多次使用
     */
    public void setUseWay(Byte useWay) {
        this.useWay = useWay;
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
     * 获取账单单次使用限制数量
     *
     * @return limit_count - 账单单次使用限制数量
     */
    public Integer getLimitCount() {
        return limitCount;
    }

    /**
     * 设置账单单次使用限制数量
     *
     * @param limitCount 账单单次使用限制数量
     */
    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
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