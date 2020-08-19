package com.yunya.models.discount;

import java.util.Date;
import javax.persistence.*;

@Table(name = "coupon_allocate")
public class CouponAllocate {
    @Id
    private Integer id;

    /**
     * 优惠券Id
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 组织Id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 配给数量
     */
    @Column(name = "allocate_num")
    private Integer allocateNum;

    /**
     * 配给时间
     */
    @Column(name = "allocate_date")
    private Date allocateDate;

    /**
     * 分配人Id
     */
    @Column(name = "allocate_user_id")
    private Integer allocateUserId;

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
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取优惠券Id
     *
     * @return coupon_id - 优惠券Id
     */
    public Integer getCouponId() {
        return couponId;
    }

    /**
     * 设置优惠券Id
     *
     * @param couponId 优惠券Id
     */
    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    /**
     * 获取组织Id
     *
     * @return org_id - 组织Id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织Id
     *
     * @param orgId 组织Id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取配给数量
     *
     * @return allocate_num - 配给数量
     */
    public Integer getAllocateNum() {
        return allocateNum;
    }

    /**
     * 设置配给数量
     *
     * @param allocateNum 配给数量
     */
    public void setAllocateNum(Integer allocateNum) {
        this.allocateNum = allocateNum;
    }

    /**
     * 获取配给时间
     *
     * @return allocate_date - 配给时间
     */
    public Date getAllocateDate() {
        return allocateDate;
    }

    /**
     * 设置配给时间
     *
     * @param allocateDate 配给时间
     */
    public void setAllocateDate(Date allocateDate) {
        this.allocateDate = allocateDate;
    }

    /**
     * 获取分配人Id
     *
     * @return allocate_user_id - 分配人Id
     */
    public Integer getAllocateUserId() {
        return allocateUserId;
    }

    /**
     * 设置分配人Id
     *
     * @param allocateUserId 分配人Id
     */
    public void setAllocateUserId(Integer allocateUserId) {
        this.allocateUserId = allocateUserId;
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