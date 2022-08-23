package com.yunya.models.system;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "clinic_live_code_visit")
public class ClinicLiveCodeVisit {
    @Id
    private Integer id;

    /**
     * 微信昵称
     */
    @Column(name = "nick_name")
    private String nickName;

    /**
     * 微信用户id
     */
    @Column(name = "open_id")
    private String openId;

    /**
     * 访问时间
     */
    @Column(name = "visit_time")
    private Date visitTime;

    /**
     * 是否首次访问
     */
    @Column(name = "is_first_visit")
    private Boolean isFirstVisit;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 所在城市
     */
    private String city;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 浏览器
     */
    @Column(name = "visit_device")
    private String visitDevice;

    /**
     * 访问时长（秒）
     */
    @Column(name = "visit_duration")
    private Integer visitDuration;

    /**
     * 意向门诊id
     */
    @Column(name = "intention_org_id")
    private Integer intentionOrgId;

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
     * 获取微信昵称
     *
     * @return nick_name - 微信昵称
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 设置微信昵称
     *
     * @param nickName 微信昵称
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 获取微信用户id
     *
     * @return open_id - 微信用户id
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * 设置微信用户id
     *
     * @param openId 微信用户id
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * 获取访问时间
     *
     * @return visit_time - 访问时间
     */
    public Date getVisitTime() {
        return visitTime;
    }

    /**
     * 设置访问时间
     *
     * @param visitTime 访问时间
     */
    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

    /**
     * 获取是否首次访问
     *
     * @return is_first_visit - 是否首次访问
     */
    public Boolean getIsFirstVisit() {
        return isFirstVisit;
    }

    /**
     * 设置是否首次访问
     *
     * @param isFirstVisit 是否首次访问
     */
    public void setIsFirstVisit(Boolean isFirstVisit) {
        this.isFirstVisit = isFirstVisit;
    }

    /**
     * 获取经度
     *
     * @return longitude - 经度
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * 设置经度
     *
     * @param longitude 经度
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * 获取纬度
     *
     * @return latitude - 纬度
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * 设置纬度
     *
     * @param latitude 纬度
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * 获取所在城市
     *
     * @return
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置所在城市
     *
     * @param city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取ip地址
     *
     * @return ip - ip地址
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置ip地址
     *
     * @param ip ip地址
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取浏览器
     *
     * @return visit_device - 浏览器
     */
    public String getVisitDevice() {
        return visitDevice;
    }

    /**
     * 设置浏览器
     *
     * @param visitDevice 浏览器
     */
    public void setVisitDevice(String visitDevice) {
        this.visitDevice = visitDevice;
    }

    /**
     * 获取访问时长
     *
     * @return visit_duration - 访问时长
     */
    public Integer getVisitDuration() {
        return visitDuration;
    }

    /**
     * 设置访问时长
     *
     * @param visitDuration 访问时长
     */
    public void setVisitDuration(Integer visitDuration) {
        this.visitDuration = visitDuration;
    }

    /**
     * 获取意向门诊id
     *
     * @return intention_org_id - 意向门诊id
     */
    public Integer getIntentionOrgId() {
        return intentionOrgId;
    }

    /**
     * 设置意向门诊id
     *
     * @param intentionOrgId 意向门诊id
     */
    public void setIntentionOrgId(Integer intentionOrgId) {
        this.intentionOrgId = intentionOrgId;
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