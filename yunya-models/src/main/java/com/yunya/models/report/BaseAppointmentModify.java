package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "base_appointment_modify")
public class BaseAppointmentModify {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 诊所ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 预约ID
     */
    @Column(name = "appointment_id")
    private Integer appointmentId;

    /**
     * 被修改的预约日期
     */
    @Column(name = "appoint_date")
    private Date appointDate;

    /**
     * 被修改的预约医生
     */
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

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
     * 获取诊所ID
     *
     * @return org_id - 诊所ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置诊所ID
     *
     * @param orgId 诊所ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取预约ID
     *
     * @return appointment_id - 预约ID
     */
    public Integer getAppointmentId() {
        return appointmentId;
    }

    /**
     * 设置预约ID
     *
     * @param appointmentId 预约ID
     */
    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * 获取被修改的预约日期
     *
     * @return appoint_date - 被修改的预约日期
     */
    public Date getAppointDate() {
        return appointDate;
    }

    /**
     * 设置被修改的预约日期
     *
     * @param appointDate 被修改的预约日期
     */
    public void setAppointDate(Date appointDate) {
        this.appointDate = appointDate;
    }

    /**
     * 获取被修改的预约医生
     *
     * @return dentist_id - 被修改的预约医生
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置被修改的预约医生
     *
     * @param dentistId 被修改的预约医生
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
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
     * 获取创建人ID
     *
     * @return crt_id - 创建人ID
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人ID
     *
     * @param crtId 创建人ID
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }
}