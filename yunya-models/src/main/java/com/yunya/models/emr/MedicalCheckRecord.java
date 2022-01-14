package com.yunya.models.emr;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "medical_check_record")
public class MedicalCheckRecord {
    /**
     * 检查牙位记录id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 电子病历记录id
     */
    @Column(name = "medical_record_id")
    private Integer medicalRecordId;

    /**
     * 牙位
     */
    @Column(name = "tooth_position")
    private Short toothPosition;

    /**
     * 检查id
     */
    @Column(name = "check_id")
    private Integer checkId;

    /**
     * 症状id
     */
    @Column(name = "symptom_id")
    private Integer symptomId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 检查人id
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
     * 获取检查牙位记录id
     *
     * @return id - 检查牙位记录id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置检查牙位记录id
     *
     * @param id 检查牙位记录id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取电子病历记录id
     *
     * @return medical_record_id - 电子病历记录id
     */
    public Integer getMedicalRecordId() {
        return medicalRecordId;
    }

    /**
     * 设置电子病历记录id
     *
     * @param medicalRecordId 电子病历记录id
     */
    public void setMedicalRecordId(Integer medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    /**
     * 获取牙位
     *
     * @return tooth_position - 牙位
     */
    public Short getToothPosition() {
        return toothPosition;
    }

    /**
     * 设置牙位
     *
     * @param toothPosition 牙位
     */
    public void setToothPosition(Short toothPosition) {
        this.toothPosition = toothPosition;
    }

    /**
     * 获取检查id
     *
     * @return check_id - 检查id
     */
    public Integer getCheckId() {
        return checkId;
    }

    /**
     * 设置检查id
     *
     * @param checkId 检查id
     */
    public void setCheckId(Integer checkId) {
        this.checkId = checkId;
    }

    /**
     * 获取症状id
     *
     * @return symptom_id - 症状id
     */
    public Integer getSymptomId() {
        return symptomId;
    }

    /**
     * 设置症状id
     *
     * @param symptomId 症状id
     */
    public void setSymptomId(Integer symptomId) {
        this.symptomId = symptomId;
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
     * 获取检查人id
     *
     * @return crt_id - 检查人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置检查人id
     *
     * @param crtId 检查人id
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
}