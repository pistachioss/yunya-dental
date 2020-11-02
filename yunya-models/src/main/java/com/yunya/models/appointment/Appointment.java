package com.yunya.models.appointment;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

public class Appointment {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 诊所ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 医生ID
     */
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 助手ID 默认医生配置助手ID
     */
    @Column(name = "assistant_id")
    private Integer assistantId;

    /**
     * 门诊科室ID 默认医生配置科室ID
     */
    @Column(name = "dept_room_id")
    private Integer deptRoomId;

    /**
     * 门诊设备ID
     */
    @Column(name = "clinic_device_item_id")
    private Integer clinicDeviceItemId;

    /**
     * 预约项目ID
     */
    @Column(name = "clinic_appoint_item_id")
    private Integer clinicAppointItemId;

    /**
     * 预约总时长 默认取预约项目时长
     */
    @Column(name = "appoint_duration")
    private Integer appointDuration;

    /**
     * 预约日期
     */
    @Column(name = "appoint_date")
    private Date appointDate;

    /**
     * 预约时间
     */
    @Column(name = "appoint_time")
    private String appointTime;

    /**
     * 预约开始时间
     */
    @Column(name = "appoint_start_time")
    private Date appointStartTime;

    /**
     * 预约结束时间
     */
    @Column(name = "appoint_end_time")
    private Date appointEndTime;

    /**
     * 预约时间段 预约开始时间-预约结束时间
     */
    @Column(name = "appoint_period")
    private String appointPeriod;

    /**
     * 牙位
     */
    @Column(name = "tooth_bit")
    private String toothBit;

    /**
     * 预约内容
     */
    @Column(name = "appoint_content")
    private String appointContent;

    /**
     * 预约类型 0-初诊预约；1-复诊预约
     */
    @Column(name = "appoint_type")
    private Byte appointType;

    /**
     * 预约确认 0-未确认；1-确认
     */
    @Column(name = "confirm_status")
    private Boolean confirmStatus;

    /**
     * 预约状态 0-预约未到，1-履约，2，取消预约，3-失约
     */
    @Column(name = "appoint_status")
    private Byte appointStatus;

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
     * 获取医生ID
     *
     * @return dentist_id - 医生ID
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置医生ID
     *
     * @param dentistId 医生ID
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }

    /**
     * 获取助手ID 默认医生配置助手ID
     *
     * @return assistant_id - 助手ID 默认医生配置助手ID
     */
    public Integer getAssistantId() {
        return assistantId;
    }

    /**
     * 设置助手ID 默认医生配置助手ID
     *
     * @param assistantId 助手ID 默认医生配置助手ID
     */
    public void setAssistantId(Integer assistantId) {
        this.assistantId = assistantId;
    }

    /**
     * 获取门诊科室ID 默认医生配置科室ID
     *
     * @return clinic_dept_room_id - 门诊科室ID 默认医生配置科室ID
     */
    public Integer getDeptRoomId() {
        return deptRoomId;
    }

    /**
     * 设置门诊科室ID 默认医生配置科室ID
     *
     * @param deptRoomId 门诊科室ID 默认医生配置科室ID
     */
    public void setDeptRoomId(Integer deptRoomId) {
        this.deptRoomId = deptRoomId;
    }

    /**
     * 获取门诊设备ID
     *
     * @return clinic_device_item_id - 门诊设备ID
     */
    public Integer getClinicDeviceItemId() {
        return clinicDeviceItemId;
    }

    /**
     * 设置门诊设备ID
     *
     * @param clinicDeviceItemId 门诊设备ID
     */
    public void setClinicDeviceItemId(Integer clinicDeviceItemId) {
        this.clinicDeviceItemId = clinicDeviceItemId;
    }

    /**
     * 获取预约项目ID
     *
     * @return clinic_appoint_item_id - 预约项目ID
     */
    public Integer getClinicAppointItemId() {
        return clinicAppointItemId;
    }

    /**
     * 设置预约项目ID
     *
     * @param clinicAppointItemId 预约项目ID
     */
    public void setClinicAppointItemId(Integer clinicAppointItemId) {
        this.clinicAppointItemId = clinicAppointItemId;
    }

    /**
     * 获取预约总时长 默认取预约项目时长
     *
     * @return appoint_duration - 预约总时长 默认取预约项目时长
     */
    public Integer getAppointDuration() {
        return appointDuration;
    }

    /**
     * 设置预约总时长 默认取预约项目时长
     *
     * @param appointDuration 预约总时长 默认取预约项目时长
     */
    public void setAppointDuration(Integer appointDuration) {
        this.appointDuration = appointDuration;
    }

    /**
     * 获取预约日期
     *
     * @return appoint_date - 预约日期
     */
    public Date getAppointDate() {
        return appointDate;
    }

    /**
     * 设置预约日期
     *
     * @param appointDate 预约日期
     */
    public void setAppointDate(Date appointDate) {
        this.appointDate = appointDate;
    }

    /**
     * 获取预约时间
     *
     * @return appoint_time - 预约时间
     */
    public String getAppointTime() {
        return appointTime;
    }

    /**
     * 设置预约时间
     *
     * @param appointTime 预约时间
     */
    public void setAppointTime(String appointTime) {
        this.appointTime = appointTime;
    }

    /**
     * 获取预约开始时间
     *
     * @return appoint_start_time - 预约开始时间
     */
    public Date getAppointStartTime() {
        return appointStartTime;
    }

    /**
     * 设置预约开始时间
     *
     * @param appointStartTime 预约开始时间
     */
    public void setAppointStartTime(Date appointStartTime) {
        this.appointStartTime = appointStartTime;
    }

    /**
     * 获取预约结束时间
     *
     * @return appoint_end_time - 预约结束时间
     */
    public Date getAppointEndTime() {
        return appointEndTime;
    }

    /**
     * 设置预约结束时间
     *
     * @param appointEndTime 预约结束时间
     */
    public void setAppointEndTime(Date appointEndTime) {
        this.appointEndTime = appointEndTime;
    }

    /**
     * 获取预约时间段 预约开始时间-预约结束时间
     *
     * @return appoint_period - 预约时间段 预约开始时间-预约结束时间
     */
    public String getAppointPeriod() {
        return appointPeriod;
    }

    /**
     * 设置预约时间段 预约开始时间-预约结束时间
     *
     * @param appointPeriod 预约时间段 预约开始时间-预约结束时间
     */
    public void setAppointPeriod(String appointPeriod) {
        this.appointPeriod = appointPeriod;
    }

    /**
     * 获取牙位
     *
     * @return tooth_bit - 牙位
     */
    public String getToothBit() {
        return toothBit;
    }

    /**
     * 设置牙位
     *
     * @param toothBit 牙位
     */
    public void setToothBit(String toothBit) {
        this.toothBit = toothBit;
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
     * 获取预约类型 0-初诊预约；1-复诊预约
     *
     * @return appoint_type - 预约类型 0-初诊预约；1-复诊预约
     */
    public Byte getAppointType() {
        return appointType;
    }

    /**
     * 设置预约类型 0-初诊预约；1-复诊预约
     *
     * @param appointType 预约类型 0-初诊预约；1-复诊预约
     */
    public void setAppointType(Byte appointType) {
        this.appointType = appointType;
    }

    /**
     * 获取预约确认 0-未确认；1-确认
     *
     * @return confirm_status - 预约确认 0-未确认；1-确认
     */
    public Boolean getConfirmStatus() {
        return confirmStatus;
    }

    /**
     * 设置预约确认 0-未确认；1-确认
     *
     * @param confirmStatus 预约确认 0-未确认；1-确认
     */
    public void setConfirmStatus(Boolean confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    /**
     * 获取预约状态 0-预约未到，1-履约，2，取消预约，3-失约
     *
     * @return appoint_status - 预约状态 0-预约未到，1-履约，2，取消预约，3-失约
     */
    public Byte getAppointStatus() {
        return appointStatus;
    }

    /**
     * 设置预约状态 0-预约未到，1-履约，2，取消预约，3-失约
     *
     * @param appointStatus 预约状态 0-预约未到，1-履约，2，取消预约，3-失约
     */
    public void setAppointStatus(Byte appointStatus) {
        this.appointStatus = appointStatus;
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