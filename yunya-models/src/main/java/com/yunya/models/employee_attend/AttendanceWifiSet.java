package com.yunya.models.employee_attend;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "attendance_wifi_set")
public class AttendanceWifiSet {
    @Id
    private Integer id;

    /**
     * 组织Id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * wifi名称
     */
    @Column(name = "wifi_name")
    private String wifiName;

    /**
     * mac地址
     */
    @Column(name = "mac_address")
    private String macAddress;

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
     * 获取wifi名称
     *
     * @return wifi_name - wifi名称
     */
    public String getWifiName() {
        return wifiName;
    }

    /**
     * 设置wifi名称
     *
     * @param wifiName wifi名称
     */
    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    /**
     * 获取mac地址
     *
     * @return mac_address - mac地址
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * 设置mac地址
     *
     * @param macAddress mac地址
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
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