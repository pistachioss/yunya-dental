package com.yunya.models.patient_central;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "prepaid_return_record")
public class PrepaidReturnRecord {
    @Id
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 预付款id
     */
    @Column(name = "prepaid_id")
    private String prepaidId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 退费方式ID
     */
    @Column(name = "return_way_id")
    private Integer returnWayId;

    /**
     * 退还本金
     */
    @Column(name = "return_principal_amount")
    private BigDecimal returnPrincipalAmount;

    /**
     * 退还赠金
     */
    @Column(name = "return_gift_amount")
    private BigDecimal returnGiftAmount;

    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

    /**
     * 退费方式
     */
    @Column(name = "return_way_type")
    private String returnWayType;

    /**
     * 创建人id
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
     * 更新人id
     */
    @Column(name = "cpd_id")
    private Integer cpdId;

    /**
     * 更新人姓名
     */
    @Column(name = "cpd_name")
    private String cpdName;

    /**
     * 更新时间
     */
    @Column(name = "cpd_time")
    private Date cpdTime;

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
     * 获取预付款id
     *
     * @return prepaid_id - 预付款id
     */
    public String getPrepaidId() {
        return prepaidId;
    }

    /**
     * 设置预付款id
     *
     * @param prepaidId 预付款id
     */
    public void setPrepaidId(String prepaidId) {
        this.prepaidId = prepaidId;
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
     * 获取退费方式ID
     *
     * @return return_way_id - 退费方式ID
     */
    public Integer getReturnWayId() {
        return returnWayId;
    }

    /**
     * 设置退费方式ID
     *
     * @param returnWayId 退费方式ID
     */
    public void setReturnWayId(Integer returnWayId) {
        this.returnWayId = returnWayId;
    }

    /**
     * 获取退还本金
     *
     * @return return_principal_amount - 退还本金
     */
    public BigDecimal getReturnPrincipalAmount() {
        return returnPrincipalAmount;
    }

    /**
     * 设置退还本金
     *
     * @param returnPrincipalAmount 退还本金
     */
    public void setReturnPrincipalAmount(BigDecimal returnPrincipalAmount) {
        this.returnPrincipalAmount = returnPrincipalAmount;
    }

    /**
     * 获取退还赠金
     *
     * @return return_gift_amount - 退还赠金
     */
    public BigDecimal getReturnGiftAmount() {
        return returnGiftAmount;
    }

    /**
     * 设置退还赠金
     *
     * @param returnGiftAmount 退还赠金
     */
    public void setReturnGiftAmount(BigDecimal returnGiftAmount) {
        this.returnGiftAmount = returnGiftAmount;
    }

    /**
     * 获取退费原因
     *
     * @return return_reason - 退费原因
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置退费原因
     *
     * @param remarks 退费原因
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 获取退费方式
     *
     * @return return_way_type - 退费方式
     */
    public String getReturnWayType() {
        return returnWayType;
    }

    /**
     * 设置退费方式
     *
     * @param returnWayType 退费方式
     */
    public void setReturnWayType(String returnWayType) {
        this.returnWayType = returnWayType;
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
     * 获取更新人id
     *
     * @return cpd_id - 更新人id
     */
    public Integer getCpdId() {
        return cpdId;
    }

    /**
     * 设置更新人id
     *
     * @param cpdId 更新人id
     */
    public void setCpdId(Integer cpdId) {
        this.cpdId = cpdId;
    }

    /**
     * 获取更新人姓名
     *
     * @return cpd_name - 更新人姓名
     */
    public String getCpdName() {
        return cpdName;
    }

    /**
     * 设置更新人姓名
     *
     * @param cpdName 更新人姓名
     */
    public void setCpdName(String cpdName) {
        this.cpdName = cpdName;
    }

    /**
     * 获取更新时间
     *
     * @return cpd_time - 更新时间
     */
    public Date getCpdTime() {
        return cpdTime;
    }

    /**
     * 设置更新时间
     *
     * @param cpdTime 更新时间
     */
    public void setCpdTime(Date cpdTime) {
        this.cpdTime = cpdTime;
    }
}