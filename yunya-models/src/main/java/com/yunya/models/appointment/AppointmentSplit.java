package com.yunya.models.appointment;

import java.util.Date;
import javax.persistence.*;

@Table(name = "appointment_split")
public class AppointmentSplit {
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
     * 拆分开始时间 需校验时间
     */
    @Column(name = "split_start_time")
    private Date splitStartTime;

    /**
     * 拆分结束时间 需校验时间
     */
    @Column(name = "split_end_time")
    private Date splitEndTime;

    /**
     * 医生/助手ID
     */
    @Column(name = "assistant_id")
    private Integer assistantId;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 更新人姓名
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
     * 获取拆分开始时间 需校验时间
     *
     * @return split_start_time - 拆分开始时间 需校验时间
     */
    public Date getSplitStartTime() {
        return splitStartTime;
    }

    /**
     * 设置拆分开始时间 需校验时间
     *
     * @param splitStartTime 拆分开始时间 需校验时间
     */
    public void setSplitStartTime(Date splitStartTime) {
        this.splitStartTime = splitStartTime;
    }

    /**
     * 获取拆分结束时间 需校验时间
     *
     * @return split_end_time - 拆分结束时间 需校验时间
     */
    public Date getSplitEndTime() {
        return splitEndTime;
    }

    /**
     * 设置拆分结束时间 需校验时间
     *
     * @param splitEndTime 拆分结束时间 需校验时间
     */
    public void setSplitEndTime(Date splitEndTime) {
        this.splitEndTime = splitEndTime;
    }

    /**
     * 获取医生/助手ID
     *
     * @return assistant_id - 医生/助手ID
     */
    public Integer getAssistantId() {
        return assistantId;
    }

    /**
     * 设置医生/助手ID
     *
     * @param assistantId 医生/助手ID
     */
    public void setAssistantId(Integer assistantId) {
        this.assistantId = assistantId;
    }

    /**
     * 获取备注 备注
     *
     * @return remarks - 备注 备注
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注 备注
     *
     * @param remarks 备注 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 获取是否启用 是否有效
     *
     * @return inservice - 是否启用 是否有效
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用 是否有效
     *
     * @param inservice 是否启用 是否有效
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
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

    /**
     * 获取创建人姓名
     *
     * @return crt_name - 创建人姓名
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人姓名
     *
     * @param crtName 创建人姓名
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
     * 获取更新人ID
     *
     * @return upt_id - 更新人ID
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置更新人ID
     *
     * @param uptId 更新人ID
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取更新人姓名
     *
     * @return upd_name - 更新人姓名
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人姓名
     *
     * @param updName 更新人姓名
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