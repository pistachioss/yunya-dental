package com.yunya.models.appointment;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "clinic_appoint_not_arrived_setting")
public class ClinicAppointNotArrivedSetting {
    /**
     * 主键 主键
     */
    @Id
    private Integer id;

    /**
     * 用户ID 用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 患者列 是否显示：0-不显示；1-显示
     */
    @Column(name = "patient_column")
    private Boolean patientColumn;

    /**
     * 初复诊列 是否显示：0-不显示；1-显示
     */
    @Column(name = "appoint_type_column")
    private Boolean appointTypeColumn;

    /**
     * 医生列 是否显示：0-不显示；1-显示
     */
    @Column(name = "distent_column")
    private Boolean distentColumn;

    /**
     * 病历编号列 是否显示：0-不显示；1-显示
     */
    @Column(name = "medical_record_number_column")
    private Boolean medicalRecordNumberColumn;

    /**
     * 手机号列 是否显示：0-不显示；1-显示
     */
    @Column(name = "telephone_column")
    private Boolean telephoneColumn;

    /**
     * 预约时间列 是否显示：0-不显示；1-显示
     */
    @Column(name = "appoint_time_column")
    private Boolean appointTimeColumn;

    /**
     * 预约时长列 是否显示：0-不显示；1-显示
     */
    @Column(name = "appoint_duration_column")
    private Boolean appointDurationColumn;

    /**
     * 预约事项列 是否显示：0-不显示；1-显示
     */
    @Column(name = "appoint_item_type_column")
    private Boolean appointItemTypeColumn;

    /**
     * 确认状态列 是否显示：0-不显示；1-显示
     */
    @Column(name = "confirm_status_column")
    private Boolean confirmStatusColumn;

    /**
     * 预约备注列 是否显示：0-不显示；1-显示
     */
    @Column(name = "appoint_remarks_column")
    private Boolean appointRemarksColumn;

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
     * @return 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     * @param userId 用户ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取患者列 是否显示
     * @return 患者列 是否显示
     */
    public Boolean getPatientColumn() {
        return patientColumn;
    }

    /**
     * 设置患者列 是否显示
     * @param patientColumn 患者列 是否显示
     */
    public void setPatientColumn(Boolean patientColumn) {
        this.patientColumn = patientColumn;
    }

    /**
     * 获取初复诊列 是否显示：0-不显示；1-显示
     * @return 初复诊列 是否显示：0-不显示；1-显示
     */
    public Boolean getAppointTypeColumn() {
        return appointTypeColumn;
    }

    /**
     * 设置初复诊列 是否显示：0-不显示；1-显示
     * @param appointTypeColumn 初复诊列 是否显示：0-不显示；1-显示
     */
    public void setAppointTypeColumn(Boolean appointTypeColumn) {
        this.appointTypeColumn = appointTypeColumn;
    }

    /**
     * 获取医生列 是否显示：0-不显示；1-显示
     * @return 医生列 是否显示：0-不显示；1-显示
     */
    public Boolean getDistentColumn() {
        return distentColumn;
    }

    /**
     * 设置医生列 是否显示：0-不显示；1-显示
     * @param distentColumn 医生列 是否显示：0-不显示；1-显示
     */
    public void setDistentColumn(Boolean distentColumn) {
        this.distentColumn = distentColumn;
    }

    /**
     * 获取病历编号列 是否显示：0-不显示；1-显示
     * @return 病历编号列 是否显示：0-不显示；1-显示
     */
    public Boolean getMedicalRecordNumberColumn() {
        return medicalRecordNumberColumn;
    }

    /**
     * 设置病历编号列 是否显示：0-不显示；1-显示
     * @param medicalRecordNumberColumn 病历编号列 是否显示：0-不显示；1-显示
     */
    public void setMedicalRecordNumberColumn(Boolean medicalRecordNumberColumn) {
        this.medicalRecordNumberColumn = medicalRecordNumberColumn;
    }

    /**
     * 获取手机号列 是否显示：0-不显示；1-显示
     * @return  手机号列 是否显示：0-不显示；1-显示
     */
    public Boolean getTelephoneColumn() {
        return telephoneColumn;
    }

    /**
     * 设置手机号列 是否显示：0-不显示；1-显示
     * @param telephoneColumn 手机号列 是否显示：0-不显示；1-显示
     */
    public void setTelephoneColumn(Boolean telephoneColumn) {
        this.telephoneColumn = telephoneColumn;
    }

    /**
     * 获取预约时间列 是否显示：0-不显示；1-显示
     * @return 预约时间列 是否显示：0-不显示；1-显示
     */
    public Boolean getAppointTimeColumn() {
        return appointTimeColumn;
    }

    /**
     * 设置预约时间列 是否显示：0-不显示；1-显示
     * @param appointTimeColumn 预约时间列 是否显示：0-不显示；1-显示
     */
    public void setAppointTimeColumn(Boolean appointTimeColumn) {
        this.appointTimeColumn = appointTimeColumn;
    }

    /**
     * 获取预约时长列 是否显示：0-不显示；1-显示
     * @return 预约时长列 是否显示：0-不显示；1-显示
     */
    public Boolean getAppointDurationColumn() {
        return appointDurationColumn;
    }

    /**
     * 设置预约时长列 是否显示：0-不显示；1-显示
     * @param appointDurationColumn 预约时长列 是否显示：0-不显示；1-显示
     */
    public void setAppointDurationColumn(Boolean appointDurationColumn) {
        this.appointDurationColumn = appointDurationColumn;
    }

    /**
     * 获取预约事项列 是否显示：0-不显示；1-显示
     * @return 预约事项列 是否显示：0-不显示；1-显示
     */
    public Boolean getAppointItemTypeColumn() {
        return appointItemTypeColumn;
    }

    /**
     * 设置预约事项列 是否显示：0-不显示；1-显示
     * @param appointItemTypeColumn 预约事项列 是否显示：0-不显示；1-显示
     */
    public void setAppointItemTypeColumn(Boolean appointItemTypeColumn) {
        this.appointItemTypeColumn = appointItemTypeColumn;
    }

    /**
     * 获取确认状态列 是否显示：0-不显示；1-显示
     * @return 确认状态列 是否显示：0-不显示；1-显示
     */
    public Boolean getConfirmStatusColumn() {
        return confirmStatusColumn;
    }

    /**
     * 设置确认状态列 是否显示：0-不显示；1-显示
     * @param confirmStatusColumn 确认状态列 是否显示：0-不显示；1-显示
     */
    public void setConfirmStatusColumn(Boolean confirmStatusColumn) {
        this.confirmStatusColumn = confirmStatusColumn;
    }

    /**
     * 获取预约备注列 是否显示：0-不显示；1-显示
     * @return 预约备注列 是否显示：0-不显示；1-显示
     */
    public Boolean getAppointRemarksColumn() {
        return appointRemarksColumn;
    }

    /**
     * 设置预约备注列 是否显示：0-不显示；1-显示
     * @param appointRemarksColumn 预约备注列 是否显示：0-不显示；1-显示
     */
    public void setAppointRemarksColumn(Boolean appointRemarksColumn) {
        this.appointRemarksColumn = appointRemarksColumn;
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
