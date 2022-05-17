package com.yunya365.mini.entity;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

public class Distribution {
    @Id
    private Integer id;

    /**
     * 起送价格
     */
    @Column(name = "start_sending_price")
    @ApiModelProperty(value = "起送价格")
    private BigDecimal startSendingPrice;

    /**
     * 配送费用
     */
    @Column(name = "sending_price")
    @ApiModelProperty(value = "配送费用")
    private BigDecimal sendingPrice;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

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
     * 获取起送价格
     *
     * @return start_sending_price - 起送价格
     */
    public BigDecimal getStartSendingPrice() {
        return startSendingPrice;
    }

    /**
     * 设置起送价格
     *
     * @param startSendingPrice 起送价格
     */
    public void setStartSendingPrice(BigDecimal startSendingPrice) {
        this.startSendingPrice = startSendingPrice;
    }

    /**
     * 获取配送费用
     *
     * @return sending_price - 配送费用
     */
    public BigDecimal getSendingPrice() {
        return sendingPrice;
    }

    /**
     * 设置配送费用
     *
     * @param sendingPrice 配送费用
     */
    public void setSendingPrice(BigDecimal sendingPrice) {
        this.sendingPrice = sendingPrice;
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