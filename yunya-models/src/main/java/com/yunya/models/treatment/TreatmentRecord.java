package com.yunya.models.treatment;

import java.util.Date;
import javax.persistence.*;

@Table(name = "treatment_record")
public class TreatmentRecord {
    /**
     * 唯一id
     */
    @Id
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 患者预约id
     */
    @Column(name = "appointment_id")
    private Integer appointmentId;

    /**
     * 挂号表id
     */
    @Column(name = "registered_id")
    private Integer registeredId;

    /**
     * 主治医生id
     */
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 0：初诊，1：复诊
     */
    private Byte type;

    /**
     * 诊疗状态(0-接诊中；1-已开单；2-接诊完成，3-离店)
     */
    private Byte status;

    /**
     * 就诊开始时间（医生点击开始接诊的时间）
     */
    @Column(name = "treat_start_time")
    private Date treatStartTime;

    /**
     * 就诊结束时间(医生端点击就诊完成的时间)
     */
    @Column(name = "treat_end_time")
    private Date treatEndTime;

    /**
     * 是否书写电子病历（0-否；1-是）
     * */
    @Column(name = "medical_record_completed")
    private Boolean medicalRecordCompleted;

    /**
     * 配诊助手1ID
     */
    @Column(name = "assistant_id_1")
    private Integer assistantId1;

    /**
     * 配诊助手2ID
     */
    @Column(name = "assistant_id_2")
    private Integer assistantId2;

    /**
     * 配诊助手3ID
     */
    @Column(name = "assistant_id_3")
    private Integer assistantId3;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否有效、是否启用、是否可见
     */
    private Boolean inservice;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人id
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 最后更新人
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 最后更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取唯一id
     *
     * @return id - 唯一id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置唯一id
     *
     * @param id 唯一id
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
     * 获取挂号表id
     *
     * @return registered_id - 挂号表id
     */
    public Integer getRegisteredId() {
        return registeredId;
    }

    /**
     * 设置挂号表id
     *
     * @param registeredId 挂号表id
     */
    public void setRegisteredId(Integer registeredId) {
        this.registeredId = registeredId;
    }

    /**
     * 获取患者预约id
     *
     * @return appointment_id - 患者预约id
     */
    public Integer getAppointmentId() {
        return appointmentId;
    }

    /**
     * 设置患者预约id
     *
     * @param appointmentId 患者预约id
     */
    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * 获取主治医生id
     *
     * @return dentist_id - 主治医生id
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置主治医生id
     *
     * @param dentistId 主治医生id
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }

    /**
     * 获取0：初诊，1：复诊
     *
     * @return type - 0：初诊，1：复诊
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置0：初诊，1：复诊
     *
     * @param type 0：初诊，1：复诊
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取诊疗状态(0-接诊中；1-已开单；2-接诊完成，3-离店)
     *
     * @return status - 诊疗状态(0-接诊中；1-已开单；2-接诊完成，3-离店)
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置诊疗状态(0-接诊中；1-已开单；2-接诊完成，3-离店)
     *
     * @param status 诊疗状态(0-接诊中；1-已开单；2-接诊完成，3-离店)
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取就诊开始时间（医生点击开始接诊的时间）
     *
     * @return treat_start_time - 就诊开始时间（医生点击开始接诊的时间）
     */
    public Date getTreatStartTime() {
        return treatStartTime;
    }

    /**
     * 设置就诊开始时间（医生点击开始接诊的时间）
     *
     * @param treatStartTime 就诊开始时间（医生点击开始接诊的时间）
     */
    public void setTreatStartTime(Date treatStartTime) {
        this.treatStartTime = treatStartTime;
    }

    /**
     * 获取就诊结束时间(医生端点击就诊完成的时间)
     *
     * @return treat_end_time - 就诊结束时间(医生端点击就诊完成的时间)
     */
    public Date getTreatEndTime() {
        return treatEndTime;
    }

    /**
     * 设置就诊结束时间(医生端点击就诊完成的时间)
     *
     * @param treatEndTime 就诊结束时间(医生端点击就诊完成的时间)
     */
    public void setTreatEndTime(Date treatEndTime) {
        this.treatEndTime = treatEndTime;
    }

    public Boolean getMedicalRecordCompleted() {
        return medicalRecordCompleted;
    }

    public void setMedicalRecordCompleted(Boolean medicalRecordCompleted) {
        this.medicalRecordCompleted = medicalRecordCompleted;
    }

    /**
     * 获取配诊助手1ID
     *
     * @return assistant_id_1 - 配诊助手1ID
     */
    public Integer getAssistantId1() {
        return assistantId1;
    }

    /**
     * 设置配诊助手1ID
     *
     * @param assistantId1 配诊助手1ID
     */
    public void setAssistantId1(Integer assistantId1) {
        this.assistantId1 = assistantId1;
    }

    /**
     * 获取配诊助手2ID
     *
     * @return assistant_id_2 - 配诊助手2ID
     */
    public Integer getAssistantId2() {
        return assistantId2;
    }

    /**
     * 设置配诊助手2ID
     *
     * @param assistantId2 配诊助手2ID
     */
    public void setAssistantId2(Integer assistantId2) {
        this.assistantId2 = assistantId2;
    }

    /**
     * 获取配诊助手3ID
     *
     * @return assistant_id_3 - 配诊助手3ID
     */
    public Integer getAssistantId3() {
        return assistantId3;
    }

    /**
     * 设置配诊助手3ID
     *
     * @param assistantId3 配诊助手3ID
     */
    public void setAssistantId3(Integer assistantId3) {
        this.assistantId3 = assistantId3;
    }

    /**
     * 获取备注
     *
     * @return remarks - 备注
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注
     *
     * @param remarks 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 获取是否有效、是否启用、是否可见
     *
     * @return inservice - 是否有效、是否启用、是否可见
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效、是否启用、是否可见
     *
     * @param inservice 是否有效、是否启用、是否可见
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
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
     * 获取创建人
     *
     * @return crt_name - 创建人
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人
     *
     * @param crtName 创建人
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
     * 获取更新人id
     *
     * @return upd_id - 更新人id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人id
     *
     * @param updId 更新人id
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取最后更新人
     *
     * @return upd_name - 最后更新人
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置最后更新人
     *
     * @param updName 最后更新人
     */
    public void setUpdName(String updName) {
        this.updName = updName;
    }

    /**
     * 获取最后更新时间
     *
     * @return upd_time - 最后更新时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置最后更新时间
     *
     * @param updTime 最后更新时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}