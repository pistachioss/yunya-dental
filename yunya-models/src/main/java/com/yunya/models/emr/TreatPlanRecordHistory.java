package com.yunya.models.emr;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "treat_plan_record_history")
public class TreatPlanRecordHistory {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 治疗计划记录id
     */
    @Column(name = "plan_id")
    private Integer planId;

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
     * 方案是否变更：0-否，1-是
     */
    private Byte isChange;

    /**
     * 写操作类型：0-新增，1-修改，2-删除
     */
    private Byte operation;

    /**
     * 操作原因（变更状态时操作人填写的原因）
     */
    @Column(name="operation_reason")
    private String operationReason;

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
     * 获取治疗计划记录id
     *
     * @return plan_id - 治疗计划记录id
     */
    public Integer getPlanId() {
        return planId;
    }

    /**
     * 设置治疗计划记录id
     *
     * @param planId 治疗计划记录id
     */
    public void setPlanId(Integer planId) {
        this.planId = planId;
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

    public Byte getIsChange() {
        return isChange;
    }

    public void setIsChange(Byte isChange) {
        this.isChange = isChange;
    }

    public Byte getOperation() {
        return operation;
    }

    public void setOperation(Byte operation) {
        this.operation = operation;
    }

    public String getOperationReason() {
        return operationReason;
    }

    public void setOperationReason(String operationReason) {
        this.operationReason = operationReason;
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