package com.yunya.models.treatment_other;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "visiting_remind")
public class VisitingRemind {
    /**
     * 随访提醒ID
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 诊所ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 医生ID 默认末诊医生
     */
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 患者就诊ID
     */
    @Column(name = "treatment_id")
    private Integer treatmentId;

    /**
     * 提醒日期
     */
    @Column(name = "remind_date")
    private Date remindDate;

    /**
     * 提醒时间
     */
    @Column(name = "remind_time")
    private String remindTime;

    /**
     * 提醒内容
     */
    @Column(name = "remind_content")
    private String remindContent;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 提醒状态 0-待提醒；1-提醒完成
     */
    private Boolean status;

    /**
     * 是否启用 0-不启用；1-启用
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
     * 获取随访提醒ID
     *
     * @return id - 随访提醒ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置随访提醒ID
     *
     * @param id 随访提醒ID
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
     * 获取患者ID
     *
     * @return patient_id - 患者ID
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者ID
     *
     * @param patientId 患者ID
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取医生ID 默认末诊医生
     *
     * @return distent_id - 医生ID 默认末诊医生
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置医生ID 默认末诊医生
     *
     * @param dentistId 医生ID 默认末诊医生
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }

    /**
     * 获取患者就诊ID
     *
     * @return treatment_id - 患者就诊ID
     */
    public Integer getTreatmentId() {
        return treatmentId;
    }

    /**
     * 设置患者就诊ID
     *
     * @param treatmentId 患者就诊ID
     */
    public void setTreatmentId(Integer treatmentId) {
        this.treatmentId = treatmentId;
    }

    /**
     * 获取提醒日期
     *
     * @return remindDateTime - 提醒日期
     */
    public Date getRemindDate() {
        return remindDate;
    }

    /**
     * 设置提醒日期
     *
     * @param remindDate 提醒日期
     */
    public void setRemindDate(Date remindDate) {
        this.remindDate = remindDate;
    }

    /**
     * 获取提醒时间
     *
     * @return remindTime - 提醒时间
     */
    public String getRemindTime() {
        return remindTime;
    }

    /**
     * 设置提醒时间
     *
     * @param remindTime 提醒时间
     */
    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }


    /**
     * 获取提醒内容
     *
     * @return remindContent - 提醒内容
     */
    public String getRemindContent() {
        return remindContent;
    }

    /**
     * 设置提醒内容
     *
     * @param remindContent 提醒内容
     */
    public void setRemindContent(String remindContent) {
        this.remindContent = remindContent;
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
     * 获取是否启用 0-不启用；1-启用
     *
     * @return inservice - 是否启用 0-不启用；1-启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用 0-不启用；1-启用
     *
     * @param inservice 是否启用 0-不启用；1-启用
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

    /**
     * 获取提醒状态 0-待提醒；1-提醒完成
     * @return status 提醒状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置提醒状态 0-待提醒；1-提醒完成
     * @param status 提醒状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }
}