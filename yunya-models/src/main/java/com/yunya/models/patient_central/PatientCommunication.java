package com.yunya.models.patient_central;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "patient_communication")
public class PatientCommunication {
    /**
     * 沟通记录id
     */
    @Id
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 沟通患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 沟通内容
     */
    private String content;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 修改人id
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 是否启用
     */
    private Boolean inservice;

    /**
     * 获取沟通记录id
     *
     * @return id - 沟通记录id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置沟通记录id
     *
     * @param id 沟通记录id
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
     * 获取沟通患者id
     *
     * @return patient_id - 沟通患者id
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置沟通患者id
     *
     * @param patientId 沟通患者id
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取沟通内容
     *
     * @return content - 沟通内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置沟通内容
     *
     * @param content 沟通内容
     */
    public void setContent(String content) {
        this.content = content;
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
     * 获取是否启用
     *
     * @return inservice - 是否启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用
     *
     * @param inservice 是否启用
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }
}