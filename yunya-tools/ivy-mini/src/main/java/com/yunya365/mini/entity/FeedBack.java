package com.yunya365.mini.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "feed_back")
public class FeedBack {
    @Id
    private Integer id;

    /**
     * 用户id
     */
    @Column(name = "fan_id")
    private Integer fanId;

    /**
     * 意见内容
     */
    private String context;

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
     * 获取用户id
     *
     * @return fan_id - 用户id
     */
    public Integer getFanId() {
        return fanId;
    }

    /**
     * 设置用户id
     *
     * @param fanId 用户id
     */
    public void setFanId(Integer fanId) {
        this.fanId = fanId;
    }

    /**
     * 获取意见内容
     *
     * @return context - 意见内容
     */
    public String getContext() {
        return context;
    }

    /**
     * 设置意见内容
     *
     * @param context 意见内容
     */
    public void setContext(String context) {
        this.context = context;
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