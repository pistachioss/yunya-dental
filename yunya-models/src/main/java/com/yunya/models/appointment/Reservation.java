package com.yunya.models.appointment;

import java.util.Date;
import javax.persistence.*;

public class Reservation {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 预约意向渠道ID
     */
    @Column(name = "reservation_source_id")
    private Integer reservationSourceId;

    /**
     * 预约项目
     */
    @Column(name = "appoint_item_name")
    private String appointItemName;

    /**
     * 预约意向门诊ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 就诊人名字
     */
    @Column(name = "patient_name")
    private String patientName;

    /**
     * 就诊人手机号
     */
    @Column(name = "patient_phone")
    private String patientPhone;

    /**
     * 就诊人性别
     */
    @Column(name = "patient_gender")
    private Byte patientGender;

    /**
     * 登记状态(0：新增；1：已预约；2：已放弃)
     */
    @Column(name = "status")
    private Byte status;

    /**
     * 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    private Boolean inservice;

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
     * 更新人名称
     */
    @Column(name = "upd_name")
    private String updName;

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
     * 获取预约意向渠道ID
     *
     * @return reservation_source_id - 预约意向渠道ID
     */
    public Integer getReservationSourceId() {
        return reservationSourceId;
    }

    /**
     * 设置预约意向渠道ID
     *
     * @param reservationSourceId 预约意向渠道ID
     */
    public void setReservationSourceId(Integer reservationSourceId) {
        this.reservationSourceId = reservationSourceId;
    }

    /**
     * 获取预约项目
     *
     * @return appoint_item_name - 预约项目
     */
    public String getAppointItemName() {
        return appointItemName;
    }

    /**
     * 设置预约项目
     *
     * @param appointItemName 预约项目
     */
    public void setAppointItemName(String appointItemName) {
        this.appointItemName = appointItemName;
    }

    /**
     * 获取预约意向门诊ID
     *
     * @return org_id - 预约意向门诊ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置预约意向门诊ID
     *
     * @param orgId 预约意向门诊ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取就诊人名字
     *
     * @return patient_name - 就诊人名字
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * 设置就诊人名字
     *
     * @param patientName 就诊人名字
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * 获取就诊人手机号
     *
     * @return patient_phone - 就诊人手机号
     */
    public String getPatientPhone() {
        return patientPhone;
    }

    /**
     * 设置就诊人手机号
     *
     * @param patientPhone 就诊人手机号
     */
    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    /**
     * 获取就诊人性别
     *
     * @return patient_gender - 就诊人性别
     */
    public Byte getPatientGender() {
        return patientGender;
    }

    /**
     * 设置就诊人性别
     *
     * @param patientGender 就诊人性别
     */
    public void setPatientGender(Byte patientGender) {
        this.patientGender = patientGender;
    }

    /**
     * 获取是否有效，是否删除(默认有效) 1-有效；0删除
     *
     * @return inservice - 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效，是否删除(默认有效) 1-有效；0删除
     *
     * @param inservice 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
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
     * 获取更新人名称
     *
     * @return upd_name - 更新人名称
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人名称
     *
     * @param updName 更新人名称
     */
    public void setUpdName(String updName) {
        this.updName = updName;
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