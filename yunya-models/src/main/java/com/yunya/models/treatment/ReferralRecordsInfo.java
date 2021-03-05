package com.yunya.models.treatment;

import java.util.Date;
import javax.persistence.*;

@Table(name = "referral_records_info")
public class ReferralRecordsInfo {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 组织id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 转诊人id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 转诊人的科室ID
     */
    @Column(name = "dep_id")
    private Integer depId;

    /**
     * 转诊人挂号id
     */
    @Column(name = "registered_id")
    private Integer registeredId;

    /**
     * 被转诊人id
     */
    @Column(name = "referred_id")
    private Integer referredId;

    /**
     * 被转诊人的科室ID
     */
    @Column(name = "referred_dep_id")
    private Integer referredDepId;

    /**
     * 被转诊人挂号id
     */
    @Column(name = "referred_registered_id")
    private Integer referredRegisteredId;

    /**
     * 备注
     */
    private String remark;

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

    @Column(name = "upd_id")
    private Integer updId;

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
     * 获取组织id
     *
     * @return org_id - 组织id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织id
     *
     * @param orgId 组织id
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
     * 获取转诊人id
     *
     * @return user_id - 转诊人id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置转诊人id
     *
     * @param userId 转诊人id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取转诊人的科室ID
     *
     * @return dep_id - 转诊人的科室ID
     */
    public Integer getDepId() {
        return depId;
    }

    /**
     * 设置转诊人的科室ID
     *
     * @param depId 转诊人的科室ID
     */
    public void setDepId(Integer depId) {
        this.depId = depId;
    }

    /**
     * 获取转诊人挂号id
     *
     * @return registered_id - 转诊人挂号id
     */
    public Integer getRegisteredId() {
        return registeredId;
    }

    /**
     * 设置转诊人挂号id
     *
     * @param registeredId 转诊人挂号id
     */
    public void setRegisteredId(Integer registeredId) {
        this.registeredId = registeredId;
    }

    /**
     * 获取被转诊人id
     *
     * @return referred_id - 被转诊人id
     */
    public Integer getReferredId() {
        return referredId;
    }

    /**
     * 设置被转诊人id
     *
     * @param referredId 被转诊人id
     */
    public void setReferredId(Integer referredId) {
        this.referredId = referredId;
    }

    /**
     * 获取被转诊人的科室ID
     *
     * @return referred_dep_id - 被转诊人的科室ID
     */
    public Integer getReferredDepId() {
        return referredDepId;
    }

    /**
     * 设置被转诊人的科室ID
     *
     * @param referredDepId 被转诊人的科室ID
     */
    public void setReferredDepId(Integer referredDepId) {
        this.referredDepId = referredDepId;
    }

    /**
     * 获取被转诊人挂号id
     *
     * @return referred_registered_id - 被转诊人挂号id
     */
    public Integer getReferredRegisteredId() {
        return referredRegisteredId;
    }

    /**
     * 设置被转诊人挂号id
     *
     * @param referredRegisteredId 被转诊人挂号id
     */
    public void setReferredRegisteredId(Integer referredRegisteredId) {
        this.referredRegisteredId = referredRegisteredId;
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
     * @return upd_id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * @param updId
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * @return upd_time
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * @param updTime
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}