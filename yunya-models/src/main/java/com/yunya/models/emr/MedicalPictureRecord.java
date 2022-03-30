package com.yunya.models.emr;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "medical_picture_record")
public class MedicalPictureRecord {
    /**
     * 病历照片记录id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 日期
     */
    private String name;

    /**
     * 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建日期
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 修改日期
     */
    @Column(name = "upt_time")
    private Date uptTime;

    /**
     * 获取病历照片记录id
     *
     * @return id - 病历照片记录id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置病历照片记录id
     *
     * @param id 病历照片记录id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取日期
     *
     * @return name - 日期
     */
    public String getName() {
        return name;
    }

    /**
     * 设置日期
     *
     * @param name 日期
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取是否有效
     *
     * @return inservice - 是否有效
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效
     *
     * @param inservice 是否有效
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
     * @return upt_id - 修改人id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置修改人id
     *
     * @param uptId 修改人id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取修改日期
     *
     * @return upt_time - 修改日期
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置修改日期
     *
     * @param uptTime 修改日期
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }
}