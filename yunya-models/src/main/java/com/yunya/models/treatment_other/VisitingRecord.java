package com.yunya.models.treatment_other;

import java.util.Date;
import javax.persistence.*;

@Table(name = "visiting_record")
public class VisitingRecord {
    /**
     * 随访记录ID
     */
    @Id
    private Integer id;

    /**
     * 诊所ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者就诊记录ID
     */
    @Column(name = "treatment_id")
    private Integer treatmentId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 医生ID 默认为末诊医生
     */
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 科室ID 默认末诊科室
     */
    @Column(name = "dept_room_id")
    private Integer deptRoomId;

    /**
     * 就诊日期
     */
    @Column(name = "treatment_date")
    private Date treatmentDate;

    /**
     * 随访日期
     */
    @Column(name = "visiting_date")
    private Date visitingDate;

    /**
     * 随访时间
     */
    @Column(name = "visiting_time")
    private String visitingTime;

    /**
     * 随访原因 新建随访
     */
    private String reason;

    /**
     * 随访内容 执行随访
     */
    @Column(name = "visiting_content")
    private String visitingContent;

    /**
     * 随访状态 0-待随访（默认）；1-随访完成
     */
    private Boolean status;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否启用 0-启用；1-不启用
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
     * 获取随访记录ID
     *
     * @return id - 随访记录ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置随访记录ID
     *
     * @param id 随访记录ID
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
     * 获取患者就诊记录ID
     *
     * @return treatment_id - 患者就诊记录ID
     */
    public Integer getTreatmentId() {
        return treatmentId;
    }

    /**
     * 设置患者就诊记录ID
     *
     * @param treatmentId 患者就诊记录ID
     */
    public void setTreatmentId(Integer treatmentId) {
        this.treatmentId = treatmentId;
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
     * 获取医生ID 默认为末诊医生
     *
     * @return dentist_id - 医生ID 默认为末诊医生
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置医生ID 默认为末诊医生
     *
     * @param dentistId 医生ID 默认为末诊医生
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }

    /**
     * 获取科室ID 默认末诊科室
     *
     * @return dept_room_id - 科室ID 默认末诊科室
     */
    public Integer getDeptRoomId() {
        return deptRoomId;
    }

    /**
     * 设置科室ID 默认末诊科室
     *
     * @param deptRoomId 科室ID 默认末诊科室
     */
    public void setDeptRoomId(Integer deptRoomId) {
        this.deptRoomId = deptRoomId;
    }

    /**
     * 获取就诊日期
     *
     * @return treatment_date - 就诊日期
     */
    public Date getTreatmentDate() {
        return treatmentDate;
    }

    /**
     * 设置就诊日期
     *
     * @param treatmentDate 就诊日期
     */
    public void setTreatmentDate(Date treatmentDate) {
        this.treatmentDate = treatmentDate;
    }

    /**
     * 获取随访日期
     *
     * @return visiting_date - 随访日期
     */
    public Date getVisitingDate() {
        return visitingDate;
    }

    /**
     * 设置随访日期
     *
     * @param visitingDate 随访日期
     */
    public void setVisitingDate(Date visitingDate) {
        this.visitingDate = visitingDate;
    }

    /**
     * 获取随访时间
     *
     * @return visiting_time - 随访时间
     */
    public String getVisitingTime() {
        return visitingTime;
    }

    /**
     * 设置随访时间
     *
     * @param visitingTime 随访时间
     */
    public void setVisitingTime(String visitingTime) {
        this.visitingTime = visitingTime;
    }

    /**
     * 获取随访原因 新建随访
     *
     * @return reason - 随访原因 新建随访
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置随访原因 新建随访
     *
     * @param reason 随访原因 新建随访
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 获取随访内容 执行随访
     *
     * @return visiting_content - 随访内容 执行随访
     */
    public String getVisitingContent() {
        return visitingContent;
    }

    /**
     * 设置随访内容 执行随访
     *
     * @param visitingContent 随访内容 执行随访
     */
    public void setVisitingContent(String visitingContent) {
        this.visitingContent = visitingContent;
    }

    /**
     * 获取是否启用 0-启用；1-不启用
     *
     * @return inservice - 是否启用 0-启用；1-不启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用 0-启用；1-不启用
     *
     * @param inservice 是否启用 0-启用；1-不启用
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
     * 获取随访状态
     * @return status - 随访状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置随访状态
     * @param status - 随访状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }
}