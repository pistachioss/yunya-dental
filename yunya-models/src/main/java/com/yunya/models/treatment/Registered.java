package com.yunya.models.treatment;

import java.util.Date;
import javax.persistence.*;

public class Registered {
    /**
     * 唯一id
     */
    @Id
    private Integer id;

    /**
     * 诊所id
     */
    @Column(name = "clinic_id")
    private Integer clinicId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 预约id
     */
    @Column(name = "appointment_id")
    private Integer appointmentId;

    /**
     * 可挂号医生id
     */
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 助手id
     */
    @Column(name = "assistant_id")
    private Integer assistantId;

    /**
     * 科室ID
     */
    @Column(name = "dept_room_id")
    private Integer deptRoomId;

    /**
     * 挂号时间
     */
    @Column(name = "reg_time")
    private Date regTime;

    /**
     * 接诊状态状态（0-待接诊；1-已接诊）
     */
    private Byte status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用（有效）
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
     * 获取诊所id
     *
     * @return clinic_id - 诊所id
     */
    public Integer getClinicId() {
        return clinicId;
    }

    /**
     * 设置诊所id
     *
     * @param clinicId 诊所id
     */
    public void setClinicId(Integer clinicId) {
        this.clinicId = clinicId;
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
     * 获取预约id
     *
     * @return appointment_id - 预约id
     */
    public Integer getAppointmentId() {
        return appointmentId;
    }

    /**
     * 设置预约id
     *
     * @param appointmentId 预约id
     */
    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * 获取可挂号医生id
     *
     * @return dentist_id - 可挂号医生id
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置可挂号医生id
     *
     * @param dentistId 可挂号医生id
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }

    /**
     * 获取助手id
     *
     * @return assistant_id - 助手id
     */
    public Integer getAssistantId() {
        return assistantId;
    }

    /**
     * 设置助手id
     *
     * @param assistantId 助手id
     */
    public void setAssistantId(Integer assistantId) {
        this.assistantId = assistantId;
    }

    /**
     * 获取科室ID
     *
     * @return dept_room_id - 科室ID
     */
    public Integer getDeptRoomId() {
        return deptRoomId;
    }

    /**
     * 设置科室ID
     *
     * @param deptRoomId 科室ID
     */
    public void setDeptRoomId(Integer deptRoomId) {
        this.deptRoomId = deptRoomId;
    }

    /**
     * 获取挂号时间
     *
     * @return reg_time - 挂号时间
     */
    public Date getRegTime() {
        return regTime;
    }

    /**
     * 设置挂号时间
     *
     * @param regTime 挂号时间
     */
    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    /**
     * 获取接诊状态状态（0-待接诊；1-已接诊）
     *
     * @return status - 接诊状态状态（0-待接诊；1-已接诊）
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置接诊状态状态（0-待接诊；1-已接诊）
     *
     * @param status 接诊状态状态（0-待接诊；1-已接诊）
     */
    public void setStatus(Byte status) {
        this.status = status;
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

    /**
     * 获取是否启用（有效）
     *
     * @return inservice - 是否启用（有效）
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用（有效）
     *
     * @param inservice 是否启用（有效）
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