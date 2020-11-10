package com.yunya.models.patient_central;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

/**
 * @author WY
 */
@Table(name = "patient_prepayments_info")
public class PatientPrepaymentsInfo {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 诊所ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 预付款账号
     */
    @Column(name = "prepayment_number")
    private String prepaymentNumber;

    /**
     * 预付款本金
     */
    @Column(name = "prepayment_principal")
    private BigDecimal prepaymentPrincipal;

    /**
     * 预付款赠金
     */
    @Column(name = "prepayment_bonus")
    private BigDecimal prepaymentBonus;

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
     * 充值后当前本金
     */
    @Column(name = "current_recharge_principal")
    private BigDecimal currentRechargePrincipal;

    /**
     * 充值后当前赠金
     */
    @Column(name = "current_recharge_bonus")
    private BigDecimal currentRechargeBonus;

    /**
     * 操作类型
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 获取操作类型
     * @return type
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置操作类型
     * @param type 操作类型
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取当前本金
     * @return currentRechargePrincipal 当前本金
     */
    public BigDecimal getCurrentRechargePrincipal() {
        return currentRechargePrincipal;
    }

    /**
     * 设置当前赠金
     * @param currentRechargePrincipal 设置当前赠金
     */
    public void setCurrentRechargePrincipal(BigDecimal currentRechargePrincipal) {
        this.currentRechargePrincipal = currentRechargePrincipal;
    }

    /**
     * 获取当前赠金
     * @return currentRechargeBonus 当前赠金
     */
    public BigDecimal getCurrentRechargeBonus() {
        return currentRechargeBonus;
    }

    /**
     * 设置当前赠金
     * @param currentRechargeBonus currentRechargeBonus 当前赠金
     */
    public void setCurrentRechargeBonus(BigDecimal currentRechargeBonus) {
        this.currentRechargeBonus = currentRechargeBonus;
    }

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
     * 获取预付款账号
     *
     * @return prepayment_number - 预付款账号
     */
    public String getPrepaymentNumber() {
        return prepaymentNumber;
    }

    /**
     * 设置预付款账号
     *
     * @param prepaymentNumber 预付款账号
     */
    public void setPrepaymentNumber(String prepaymentNumber) {
        this.prepaymentNumber = prepaymentNumber;
    }

    /**
     * 获取预付款本金
     *
     * @return prepayment_principal - 预付款本金
     */
    public BigDecimal getPrepaymentPrincipal() {
        return prepaymentPrincipal;
    }

    /**
     * 设置预付款本金
     *
     * @param prepaymentPrincipal 预付款本金
     */
    public void setPrepaymentPrincipal(BigDecimal prepaymentPrincipal) {
        this.prepaymentPrincipal = prepaymentPrincipal;
    }

    /**
     * 获取预付款赠金
     *
     * @return prepayment_bonus - 预付款赠金
     */
    public BigDecimal getPrepaymentBonus() {
        return prepaymentBonus;
    }

    /**
     * 设置预付款赠金
     *
     * @param prepaymentBonus 预付款赠金
     */
    public void setPrepaymentBonus(BigDecimal prepaymentBonus) {
        this.prepaymentBonus = prepaymentBonus;
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