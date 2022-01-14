package com.yunya.models.emr;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "medical_common_record")
public class MedicalCommonRecord {
    /**
     * 主键ID
     */
    @Id
    private Integer id;

    /**
     * 就诊ID
     */
    @Column(name = "treatment_id")
    private Integer treatmentId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 主治医生ID
     */
    @Column(name = "major_dentist_id")
    private Integer majorDentistId;

    /**
     * 主诉
     */
    @Column(name = "chief_complaint")
    private String chiefComplaint;

    /**
     * 现病史
     */
    @Column(name = "present_illness")
    private String presentIllness;

    /**
     * 既往史
     */
    @Column(name = "past_history")
    private String pastHistory;

    /**
     * 复诊
     */
    @Column(name = "re_examination")
    private String reExamination;

    /**
     * 检查
     */
    private String examination;

    /**
     * 诊断
     */
    private String diagnosis;

    /**
     * 诊疗计划
     */
    private String plan;

    /**
     * 诊疗方案
     */
    private String treatment;

    /**
     * 处方
     */
    private String prescription;

    /**
     * 类型：0初诊，1复诊
     */
    private Integer type;

    /**
     * 状态：0不需要审批，1待审核，2同意，3拒绝
     */
    private Integer status;

    /**
     * 医生审批时间
     */
    @Column(name = "approval_time")
    private Date approvalTime;

    /**
     * 补写病历的时间
     */
    private Date time;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改人ID
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取主键ID
     *
     * @return id - 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取就诊ID
     *
     * @return treatment_id - 就诊ID
     */
    public Integer getTreatmentId() {
        return treatmentId;
    }

    /**
     * 设置就诊ID
     *
     * @param treatmentId 就诊ID
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
     * 获取主治医生ID
     *
     * @return major_dentist_id - 主治医生ID
     */
    public Integer getMajorDentistId() {
        return majorDentistId;
    }

    /**
     * 设置主治医生ID
     *
     * @param majorDentistId 主治医生ID
     */
    public void setMajorDentistId(Integer majorDentistId) {
        this.majorDentistId = majorDentistId;
    }

    /**
     * 获取主诉
     *
     * @return chief_complaint - 主诉
     */
    public String getChiefComplaint() {
        return chiefComplaint;
    }

    /**
     * 设置主诉
     *
     * @param chiefComplaint 主诉
     */
    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    /**
     * 获取现病史
     *
     * @return present_illness - 现病史
     */
    public String getPresentIllness() {
        return presentIllness;
    }

    /**
     * 设置现病史
     *
     * @param presentIllness 现病史
     */
    public void setPresentIllness(String presentIllness) {
        this.presentIllness = presentIllness;
    }

    /**
     * 获取既往史
     *
     * @return past_history - 既往史
     */
    public String getPastHistory() {
        return pastHistory;
    }

    /**
     * 设置既往史
     *
     * @param pastHistory 既往史
     */
    public void setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
    }

    /**
     * 获取复诊
     *
     * @return re_examination - 复诊
     */
    public String getReExamination() {
        return reExamination;
    }

    /**
     * 设置复诊
     *
     * @param reExamination 复诊
     */
    public void setReExamination(String reExamination) {
        this.reExamination = reExamination;
    }

    /**
     * 获取检查
     *
     * @return examination - 检查
     */
    public String getExamination() {
        return examination;
    }

    /**
     * 设置检查
     *
     * @param examination 检查
     */
    public void setExamination(String examination) {
        this.examination = examination;
    }

    /**
     * 获取诊断
     *
     * @return diagnosis - 诊断
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * 设置诊断
     *
     * @param diagnosis 诊断
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    /**
     * 获取诊疗计划
     *
     * @return plan - 诊疗计划
     */
    public String getPlan() {
        return plan;
    }

    /**
     * 设置诊疗计划
     *
     * @param plan 诊疗计划
     */
    public void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * 获取诊疗方案
     *
     * @return treatment - 诊疗方案
     */
    public String getTreatment() {
        return treatment;
    }

    /**
     * 设置诊疗方案
     *
     * @param treatment 诊疗方案
     */
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    /**
     * 获取处方
     *
     * @return prescription - 处方
     */
    public String getPrescription() {
        return prescription;
    }

    /**
     * 设置处方
     *
     * @param prescription 处方
     */
    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    /**
     * 获取类型：0初诊，1复诊
     *
     * @return type - 类型：0初诊，1复诊
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置类型：0初诊，1复诊
     *
     * @param type 类型：0初诊，1复诊
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取状态：0不需要审批，1待审核，2同意，3拒绝
     *
     * @return status - 状态：0不需要审批，1待审核，2同意，3拒绝
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态：0不需要审批，1待审核，2同意，3拒绝
     *
     * @param status 状态：0不需要审批，1待审核，2同意，3拒绝
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取医生审批时间
     *
     * @return approval_time - 医生审批时间
     */
    public Date getApprovalTime() {
        return approvalTime;
    }

    /**
     * 设置医生审批时间
     *
     * @param approvalTime 医生审批时间
     */
    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }

    /**
     * 获取补写病历的时间
     *
     * @return time - 补写病历的时间
     */
    public Date getTime() {
        return time;
    }

    /**
     * 设置补写病历的时间
     *
     * @param time 补写病历的时间
     */
    public void setTime(Date time) {
        this.time = time;
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
     * 获取修改人ID
     *
     * @return upd_id - 修改人ID
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置修改人ID
     *
     * @param updId 修改人ID
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
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
}