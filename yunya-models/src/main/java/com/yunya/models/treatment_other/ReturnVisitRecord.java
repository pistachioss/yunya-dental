package com.yunya.models.treatment_other;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "return_visit_record")
public class ReturnVisitRecord {
    /**
     * 回访id
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
     * 医生
     */
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 就诊id
     */
    @Column(name = "treatment_id")
    private Integer treatmentId;

    /**
     * 回访日期
     */
    @Column(name = "return_date")
    private Date returnDate;

    /**
     * 回访时间
     */
    @Column(name = "return_time")
    private Date returnTime;

    /**
     * 回访原因
     */
    @Column(name = "return_reason")
    private String returnReason;

    /**
     * 回访内容
     */
    @Column(name = "return_content")
    private String returnContent;

    /**
     * 回访登记人id
     */
    @Column(name = "register_id")
    private Integer registerId;

    /**
     * 回访登记人
     */
    @Column(name = "register_name")
    private String registerName;

    /**
     * 是否启用
     */
    private Boolean inservice;

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
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 更新时间
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 获取回访id
     *
     * @return id - 回访id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置回访id
     *
     * @param id 回访id
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
     * 获取医生
     *
     * @return dentist_id - 医生
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置医生
     *
     * @param dentistId 医生
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }

    /**
     * 获取就诊id
     *
     * @return treatment_id - 就诊id
     */
    public Integer getTreatmentId() {
        return treatmentId;
    }

    /**
     * 设置就诊id
     *
     * @param treatmentId 就诊id
     */
    public void setTreatmentId(Integer treatmentId) {
        this.treatmentId = treatmentId;
    }

    /**
     * 获取回访日期
     *
     * @return return_date - 回访日期
     */
    public Date getReturnDate() {
        return returnDate;
    }

    /**
     * 设置回访日期
     *
     * @param returnDate 回访日期
     */
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * 获取回访时间
     *
     * @return return_time - 回访时间
     */
    public Date getReturnTime() {
        return returnTime;
    }

    /**
     * 设置回访时间
     *
     * @param returnTime 回访时间
     */
    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    /**
     * 获取回访原因
     *
     * @return return_reason - 回访原因
     */
    public String getReturnReason() {
        return returnReason;
    }

    /**
     * 设置回访原因
     *
     * @param returnReason 回访原因
     */
    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    /**
     * 获取回访内容
     *
     * @return return_content - 回访内容
     */
    public String getReturnContent() {
        return returnContent;
    }

    /**
     * 设置回访内容
     *
     * @param returnContent 回访内容
     */
    public void setReturnContent(String returnContent) {
        this.returnContent = returnContent;
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

    /**
     * 获取回访登记人id
     *
     * @return register_id - 回访登记人id
     */
    public Integer getRegisterId() {
        return registerId;
    }

    /**
     * 设置回访登记人id
     *
     * @param registerId 回访登记人id
     */
    public void setRegisterId(Integer registerId) {
        this.registerId = registerId;
    }

    /**
     * 获取回访登记人
     *
     * @return register_name - 回访登记人
     */
    public String getRegisterName() {
        return registerName;
    }

    /**
     * 设置回访登记人
     *
     * @param registerName 回访登记人
     */
    public void setRegisterName(String registerName) {
        this.registerName = registerName;
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

    /**
     * 获取更新时间
     *
     * @return upd_id - 更新时间
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新时间
     *
     * @param updId 更新时间
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }
}