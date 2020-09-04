package com.yunya.models.system;

import java.util.Date;
import javax.persistence.*;

@Table(name = "equipment_info")
public class EquipmentInfo {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 设备序列号
     */
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * 设备IP
     */
    private String ip;

    /**
     * 设备密码
     */
    private String pass;

    /**
     * 是否启用
     */
    private Boolean inservice;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 创建人名称
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 修改人id
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改人名称
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 修改时间
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
     * 获取设备序列号
     *
     * @return serial_number - 设备序列号
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * 设置设备序列号
     *
     * @param serialNumber 设备序列号
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * 获取设备IP
     *
     * @return ip - 设备IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置设备IP
     *
     * @param ip 设备IP
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取设备密码
     *
     * @return pass - 设备密码
     */
    public String getPass() {
        return pass;
    }

    /**
     * 设置设备密码
     *
     * @param pass 设备密码
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     * 获取门诊id
     *
     * @return org_id - 门诊id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置门诊id
     *
     * @param orgId 门诊id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取创建人名称
     *
     * @return crt_name - 创建人名称
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人名称
     *
     * @param crtName 创建人名称
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
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
     * 获取修改人id
     *
     * @return upd_id - 修改人id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置修改人id
     *
     * @param updId 修改人id
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取修改人名称
     *
     * @return upd_name - 修改人名称
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置修改人名称
     *
     * @param updName 修改人名称
     */
    public void setUpdName(String updName) {
        this.updName = updName;
    }

    /**
     * 获取修改时间
     *
     * @return upd_time - 修改时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置修改时间
     *
     * @param updTime 修改时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }

    /**
     * 获取创建人id
     *
     * @return upd_time - 修改时间
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人id
     *
     * @param crtId 修改时间
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取是否启用
     * @return inservice
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用
     *
     * @param inservice 是否启用
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }
}