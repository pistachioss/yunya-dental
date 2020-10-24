package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "base_treatment_process")
public class BaseTreatmentProcess {
    /**
     * 预约ID
     */
    @Column(name = "appointment_id")
    private Integer appointmentId;

    /**
     * 挂号ID
     */
    @Column(name = "registered_id")
    private Integer registeredId;

    /**
     * 就诊ID
     */
    @Column(name = "treatment_id")
    private Integer treatmentId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 诊疗状态0-挂号；1-接诊；2-开单；3-治疗完成；4-收费）
     */
    @Column(name = "treat_status")
    private Byte treatStatus;

    /**
     * 就诊类型（0-初诊；1-复诊）
     */
    @Column(name = "treat_type")
    private Byte treatType;

    /**
     * 预约状态0-未确认；1-已确认；2-改约未确认；3-改约已确认；4-取消预约）
     */
    @Column(name = "appoint_status")
    private Byte appointStatus;

    /**
     * 预约医生ID
     */
    @Column(name = "appoint_dentist_id")
    private Integer appointDentistId;

    /**
     * 预约时间
     */
    @Column(name = "appoint_start_time")
    private Date appointStartTime;

    /**
     * 预约时长
     */
    @Column(name = "appoint_duration")
    private Integer appointDuration;

    /**
     * 预约内容
     */
    @Column(name = "appoint_content")
    private String appointContent;

    @Column(name = "appoint_modify_time")
    private Integer appointModifyTime;

    /**
     * 挂号医生ID
     */
    @Column(name = "registered_dentist_id")
    private Integer registeredDentistId;

    /**
     * 挂号时间
     */
    @Column(name = "registered_time")
    private Date registeredTime;

    /**
     * 接诊开始时间
     */
    @Column(name = "treat_start_time")
    private Date treatStartTime;

    /**
     * 接诊结束时间
     */
    @Column(name = "treat_end_time")
    private Date treatEndTime;

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
     * 获取挂号ID
     *
     * @return registered_id - 挂号ID
     */
    public Integer getRegisteredId() {
        return registeredId;
    }

    /**
     * 设置挂号ID
     *
     * @param registeredId 挂号ID
     */
    public void setRegisteredId(Integer registeredId) {
        this.registeredId = registeredId;
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
     * 获取组织ID
     *
     * @return org_id - 组织ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织ID
     *
     * @param orgId 组织ID
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
     * 获取诊疗状态0-挂号；1-接诊；2-开单；3-治疗完成；4-收费）
     *
     * @return treat_status - 诊疗状态0-挂号；1-接诊；2-开单；3-治疗完成；4-收费）
     */
    public Byte getTreatStatus() {
        return treatStatus;
    }

    /**
     * 设置诊疗状态0-挂号；1-接诊；2-开单；3-治疗完成；4-收费）
     *
     * @param treatStatus 诊疗状态0-挂号；1-接诊；2-开单；3-治疗完成；4-收费）
     */
    public void setTreatStatus(Byte treatStatus) {
        this.treatStatus = treatStatus;
    }

    /**
     * 获取就诊类型（0-初诊；1-复诊）
     *
     * @return treat_type - 就诊类型（0-初诊；1-复诊）
     */
    public Byte getTreatType() {
        return treatType;
    }

    /**
     * 设置就诊类型（0-初诊；1-复诊）
     *
     * @param treatType 就诊类型（0-初诊；1-复诊）
     */
    public void setTreatType(Byte treatType) {
        this.treatType = treatType;
    }

    /**
     * 获取预约状态0-未确认；1-已确认；2-改约未确认；3-改约已确认；4-取消预约）
     *
     * @return appoint_status - 预约状态0-未确认；1-已确认；2-改约未确认；3-改约已确认；4-取消预约）
     */
    public Byte getAppointStatus() {
        return appointStatus;
    }

    /**
     * 设置预约状态0-未确认；1-已确认；2-改约未确认；3-改约已确认；4-取消预约）
     *
     * @param appointStatus 预约状态0-未确认；1-已确认；2-改约未确认；3-改约已确认；4-取消预约）
     */
    public void setAppointStatus(Byte appointStatus) {
        this.appointStatus = appointStatus;
    }

    /**
     * 获取预约医生ID
     *
     * @return appoint_dentist_id - 预约医生ID
     */
    public Integer getAppointDentistId() {
        return appointDentistId;
    }

    /**
     * 设置预约医生ID
     *
     * @param appointDentistId 预约医生ID
     */
    public void setAppointDentistId(Integer appointDentistId) {
        this.appointDentistId = appointDentistId;
    }

    /**
     * 获取预约时间
     *
     * @return appoint_start_time - 预约时间
     */
    public Date getAppointStartTime() {
        return appointStartTime;
    }

    /**
     * 设置预约时间
     *
     * @param appointStartTime 预约时间
     */
    public void setAppointStartTime(Date appointStartTime) {
        this.appointStartTime = appointStartTime;
    }

    /**
     * 获取预约时长
     *
     * @return appoint_duration - 预约时长
     */
    public Integer getAppointDuration() {
        return appointDuration;
    }

    /**
     * 设置预约时长
     *
     * @param appointDuration 预约时长
     */
    public void setAppointDuration(Integer appointDuration) {
        this.appointDuration = appointDuration;
    }

    /**
     * 获取预约内容
     *
     * @return appoint_content - 预约内容
     */
    public String getAppointContent() {
        return appointContent;
    }

    /**
     * 设置预约内容
     *
     * @param appointContent 预约内容
     */
    public void setAppointContent(String appointContent) {
        this.appointContent = appointContent;
    }

    /**
     * 获取预约修改次数
     *
     * @return appoint_modify_time
     */
    public Integer getAppointModifyTime() {
        return appointModifyTime;
    }

    /**
     * 设置预约修改次数
     *
     * @param appointModifyTime appoint_modify_time
     */
    public void setAppointModifyTime(Integer appointModifyTime) {
        this.appointModifyTime = appointModifyTime;
    }

    /**
     * 获取挂号医生ID
     *
     * @return registered_dentist_id - 挂号医生ID
     */
    public Integer getRegisteredDentistId() {
        return registeredDentistId;
    }

    /**
     * 设置挂号医生ID
     *
     * @param registeredDentistId 挂号医生ID
     */
    public void setRegisteredDentistId(Integer registeredDentistId) {
        this.registeredDentistId = registeredDentistId;
    }

    /**
     * 获取挂号时间
     *
     * @return registered_time - 挂号时间
     */
    public Date getRegisteredTime() {
        return registeredTime;
    }

    /**
     * 设置挂号时间
     *
     * @param registeredTime 挂号时间
     */
    public void setRegisteredTime(Date registeredTime) {
        this.registeredTime = registeredTime;
    }

    /**
     * 获取接诊开始时间
     *
     * @return treat_start_time - 接诊开始时间
     */
    public Date getTreatStartTime() {
        return treatStartTime;
    }

    /**
     * 设置接诊开始时间
     *
     * @param treatStartTime 接诊开始时间
     */
    public void setTreatStartTime(Date treatStartTime) {
        this.treatStartTime = treatStartTime;
    }

    /**
     * 获取接诊结束时间
     *
     * @return treat_end_time - 接诊结束时间
     */
    public Date getTreatEndTime() {
        return treatEndTime;
    }

    /**
     * 设置接诊结束时间
     *
     * @param treatEndTime 接诊结束时间
     */
    public void setTreatEndTime(Date treatEndTime) {
        this.treatEndTime = treatEndTime;
    }
}