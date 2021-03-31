package com.yunya.models.patient_central;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "wx_fans_bind")
public class WxFansBind {
    /**
     * 记录ID
     */
    @Id
    private Integer id;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * openid
     */
    @Column(name = "open_id")
    private String openId;

    /**
     * 是否绑定
     */
    private Boolean bind;

    /**
     * 绑定时间
     */
    @Column(name = "bind_time")
    private Date bindTime;

    /**
     * 是否是微信注册会员
     */
    @Column(name = "is_vip")
    private Boolean isVip;

    /**
     * 是否是微信拥有者
     */
    @Column(name = "is_owner")
    private Boolean isOwner;

    /**
     * 更新者
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 创建者
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 获取记录ID
     *
     * @return id - 记录ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置记录ID
     *
     * @param id 记录ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取患者id
     *
     * @return patient_id - 患者id
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者id
     *
     * @param patientId 患者id
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取openid
     *
     * @return open_id - openid
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * 设置openid
     *
     * @param openId openid
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * 获取是否绑定
     *
     * @return bind - 是否绑定
     */
    public Boolean getBind() {
        return bind;
    }

    /**
     * 设置是否绑定
     *
     * @param bind 是否绑定
     */
    public void setBind(Boolean bind) {
        this.bind = bind;
    }

    /**
     * 获取绑定时间
     *
     * @return bind_time - 绑定时间
     */
    public Date getBindTime() {
        return bindTime;
    }

    /**
     * 设置绑定时间
     *
     * @param bindTime 绑定时间
     */
    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }

    /**
     * 获取是否是微信注册会员
     *
     * @return is_vip - 是否是微信注册会员
     */
    public Boolean getIsVip() {
        return isVip;
    }

    /**
     * 设置是否是微信注册会员
     *
     * @param isVip 是否是微信注册会员
     */
    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    /**
     * 获取是否是微信拥有者
     *
     * @return is_owner - 是否是微信拥有者
     */
    public Boolean getIsOwner() {
        return isOwner;
    }

    /**
     * 设置是否是微信拥有者
     *
     * @param isOwner 是否是微信拥有者
     */
    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    /**
     * 获取更新者
     *
     * @return upd_id - 更新者
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新者
     *
     * @param updId 更新者
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

    /**
     * 获取创建者
     *
     * @return crt_id - 创建者
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建者
     *
     * @param crtId 创建者
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