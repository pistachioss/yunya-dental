package com.yunya.models.treatment_other;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tooth_cycle")
public class ToothCycle {
    @Id
    private Integer id;

    /**
     * 诊所id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 就诊id
     */
    @Column(name = "treatment_record_id")
    private Integer treatmentRecordId;

    /**
     * 牙医id
     */
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 牙周期
     */
    @Column(name = "tooth_cycle")
    private String toothCycle;

    /**
     * 检查日期
     */
    @Column(name = "examination_data")
    private Date examinationData;

    /**
     * 创建日期
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改人id
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

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
     * 获取诊所id
     *
     * @return org_id - 诊所id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置诊所id
     *
     * @param orgId 诊所id
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
     * 获取就诊id
     *
     * @return treatment_record_id - 就诊id
     */
    public Integer getTreatmentRecordId() {
        return treatmentRecordId;
    }

    /**
     * 设置就诊id
     *
     * @param treatmentRecordId 就诊id
     */
    public void setTreatmentRecordId(Integer treatmentRecordId) {
        this.treatmentRecordId = treatmentRecordId;
    }

    /**
     * 获取牙医id
     *
     * @return dentist_id - 牙医id
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置牙医id
     *
     * @param dentistId 牙医id
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }

    /**
     * 获取牙周期
     *
     * @return tooth_cycle - 牙周期
     */
    public String getToothCycle() {
        return toothCycle;
    }

    /**
     * 设置牙周期
     *
     * @param toothCycle 牙周期
     */
    public void setToothCycle(String toothCycle) {
        this.toothCycle = toothCycle;
    }

    /**
     * 获取检查日期
     *
     * @return examination_data - 检查日期
     */
    public Date getExaminationData() {
        return examinationData;
    }

    /**
     * 设置检查日期
     *
     * @param examinationData 检查日期
     */
    public void setExaminationData(Date examinationData) {
        this.examinationData = examinationData;
    }

    /**
     * 获取创建日期
     *
     * @return crt_time - 创建日期
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建日期
     *
     * @param crtTime 创建日期
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * 获取修改人id
     *
     * @return upd_id - 修改人id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置修改人id
     *
     * @param updId 修改人id
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
}