package com.yunya.models.discount;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "recharge_card")
public class RechargeCard {
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
     * 面值
     */
    @Column(name = "face_value")
    private BigDecimal faceValue;

    /**
     * 赠金
     */
    private BigDecimal bonus;

    /**
     * 充值截止时间
     */
    @Column(name = "recharge_deadline")
    private Date rechargeDeadline;

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
     * 获取面值
     *
     * @return face_value - 面值
     */
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    /**
     * 设置面值
     *
     * @param faceValue 面值
     */
    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
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
     * 获取充值截止时间
     *
     * @return recharge_deadline - 充值截止时间
     */
    public Date getRechargeDeadline() {
        return rechargeDeadline;
    }

    /**
     * 设置充值截止时间
     *
     * @param rechargeDeadline 充值截止时间
     */
    public void setRechargeDeadline(Date rechargeDeadline) {
        this.rechargeDeadline = rechargeDeadline;
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