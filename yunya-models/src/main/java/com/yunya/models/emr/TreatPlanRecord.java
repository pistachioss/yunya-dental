package com.yunya.models.emr;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "treat_plan_record")
public class TreatPlanRecord {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 医生id
     */
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 普通电子病历记录id
     */
    @Column(name = "medical_record_id")
    private Integer medicalRecordId;

    /**
     * 治疗计划名称
     */
    @Column(name = "plan_name")
    private String planName;

    /**
     * 状态：0-草稿; 1-已确认; 2-进行中; 3-全部完成; 4-提前终止
     */
    private Byte status;

    /**
     * 沟通方式：0-患者知情同意书
     */
    private Byte way;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 更新时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

    /**
     * 概述
     */
    private String summary;

    /**
     * 备注
     */
    private String remark;

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
     * 获取医生id
     *
     * @return dentist_id - 医生id
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置医生id
     *
     * @param dentistId 医生id
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
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
     * 获取普通电子病历记录id
     *
     * @return medical_record_id - 普通电子病历记录id
     */
    public Integer getMedicalRecordId() {
        return medicalRecordId;
    }

    /**
     * 设置普通电子病历记录id
     *
     * @param medicalRecordId 普通电子病历记录id
     */
    public void setMedicalRecordId(Integer medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    /**
     * 获取治疗计划名称
     *
     * @return plan_name - 治疗计划名称
     */
    public String getPlanName() {
        return planName;
    }

    /**
     * 设置治疗计划名称
     *
     * @param planName 治疗计划名称
     */
    public void setPlanName(String planName) {
        this.planName = planName;
    }

    /**
     * 获取状态：0-草稿; 1-已确认; 2-进行中; 3-全部完成; 4-提前终止
     *
     * @return status - 状态：0-草稿; 1-已确认; 2-进行中; 3-全部完成; 4-提前终止
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态：0-草稿; 1-已确认; 2-进行中; 3-全部完成; 4-提前终止
     *
     * @param status 状态：0-草稿; 1-已确认; 2-进行中; 3-全部完成; 4-提前终止
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取沟通方式：0-患者知情同意书
     *
     * @return way - 沟通方式：0-患者知情同意书
     */
    public Byte getWay() {
        return way;
    }

    /**
     * 设置沟通方式：0-患者知情同意书
     *
     * @param way 沟通方式：0-患者知情同意书
     */
    public void setWay(Byte way) {
        this.way = way;
    }

    /**
     * 获取创建人id
     *
     * @return crt_id - 创建人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人id
     *
     * @param crtId 创建人id
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
     * 获取更新人id
     *
     * @return upt_id - 更新人id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置更新人id
     *
     * @param uptId 更新人id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取更新时间
     *
     * @return upt_time - 更新时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置更新时间
     *
     * @param uptTime 更新时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }

    /**
     * 获取概述
     *
     * @return summary - 概述
     */
    public String getSummary() {
        return summary;
    }

    /**
     * 设置概述
     *
     * @param summary 概述
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}