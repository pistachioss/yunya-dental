package com.yunya365.mini.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "fans_pick_up")
@Data
public class FansPickUp {
    @Id
    private Integer id;

    /**
     * 微信用户id
     */
    @Column(name = "fans_id")
    private Integer fansId;

    /**
     * 取货人名称
     */
    private String name;

    /**
     * 取货人电话
     */
    @Column(name = "phone_number")
    private String phoneNumber;
    /**
     * 是否为默认
     */
    @TableField("default_status")
    private Integer defaultStatus;
    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

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
     * 获取微信用户id
     *
     * @return fans_id - 微信用户id
     */
    public Integer getFansId() {
        return fansId;
    }

    /**
     * 设置微信用户id
     *
     * @param fansId 微信用户id
     */
    public void setFansId(Integer fansId) {
        this.fansId = fansId;
    }

    /**
     * 获取取货人名称
     *
     * @return name - 取货人名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置取货人名称
     *
     * @param name 取货人名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取取货人电话
     *
     * @return phone_number - 取货人电话
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 设置取货人电话
     *
     * @param phoneNumber 取货人电话
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
     * @return upd_time
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * @param updTime
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}