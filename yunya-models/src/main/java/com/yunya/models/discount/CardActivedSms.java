package com.yunya.models.discount;

import java.util.Date;
import javax.persistence.*;

@Table(name = "card_actived_sms")
public class CardActivedSms {
    @Id
    private Integer id;

    /**
     * 卡id
     */
    @Column(name = "card_id")
    private Integer cardId;

    /**
     * 卡券分类
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

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
     * 获取卡id
     *
     * @return card_id - 卡id
     */
    public Integer getCardId() {
        return cardId;
    }

    /**
     * 设置卡id
     *
     * @param cardId 卡id
     */
    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    /**
     * 获取卡券分类
     *
     * @return coupon_id - 卡券分类
     */
    public Integer getCouponId() {
        return couponId;
    }

    /**
     * 设置卡券分类
     *
     * @param couponId 卡券分类
     */
    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    /**
     * 获取创建人id
     *
     * @return crt_id - 创建人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人id
     *
     * @param crtId 创建人id
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
}