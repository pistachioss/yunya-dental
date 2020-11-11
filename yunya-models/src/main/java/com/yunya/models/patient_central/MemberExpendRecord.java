package com.yunya.models.patient_central;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "member_expend_record")
public class MemberExpendRecord {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 会员卡id
     */
    @Column(name = "member_id")
    private String memberId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 消费本金
     */
    @Column(name = "expend_principal")
    private BigDecimal expendPrincipal;

    /**
     * 消费赠金
     */
    @Column(name = "expend_gift")
    private BigDecimal expendGift;

    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

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
     * 就诊id
     */
    @Column(name = "treatment_record_id")
    private Integer treatmentRecordId;

    /**
     * 账单id
     */
    @Column(name = "bill_record_id")
    private Integer billRecordId;

    /**
     * 账单付款记录id
     */
    @Column(name = "bill_pay_record_id")
    private Integer billPayRecordId;

    /**
     * 类型（0：撤销消费 1：消费 ）
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 消费后当前本金
     */
    @Column(name = "current_recharge_principal")
    private BigDecimal currentPrincipal;

    /**
     * 消费后当前赠金
     */
    @Column(name = "current_recharge_bonus")
    private BigDecimal currentBonus;

    /**
     * 是否有效 是否有效
     */
    @Column(name = "inservice")
    private Boolean inservice;

    /**
     * 获取type
     * @return type
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置type
     * @param type
     */
    public void setType(Integer type) {
        this.type = type;
    }

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
     * 获取会员卡id
     *
     * @return member_id - 会员卡id
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * 设置会员卡id
     *
     * @param memberId 会员卡id
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
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
     * 获取消费本金
     *
     * @return expend_principal - 消费本金
     */
    public BigDecimal getExpendPrincipal() {
        return expendPrincipal;
    }

    /**
     * 设置消费本金
     *
     * @param expendPrincipal 消费本金
     */
    public void setExpendPrincipal(BigDecimal expendPrincipal) {
        this.expendPrincipal = expendPrincipal;
    }

    /**
     * 获取消费赠金
     *
     * @return expend_gift - 消费赠金
     */
    public BigDecimal getExpendGift() {
        return expendGift;
    }

    /**
     * 设置消费赠金
     *
     * @param expendGift 消费赠金
     */
    public void setExpendGift(BigDecimal expendGift) {
        this.expendGift = expendGift;
    }

    /**
     * 获取消费原因
     *
     * @return remarks - 消费原因
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置消费原因
     *
     * @param remarks 消费原因
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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


    /**
     * 获取就诊id
     *
     * @return treatmentRecordId - 就诊id
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
     * 获取账单id
     *
     * @return billRecordId - 账单id
     */
    public Integer getBillRecordId() {
        return billRecordId;
    }

    /**
     * 设置账单id
     *
     * @param billRecordId 账单id
     */
    public void setBillRecordId(Integer billRecordId) {
        this.billRecordId = billRecordId;
    }

    /**
     * 获取账单付款记录id
     *
     * @return billPayRecordId - 账单付款记录id
     */
    public Integer getBillPayRecordId() {
        return billPayRecordId;
    }

    /**
     * 设置账单付款记录id
     *
     * @param billPayRecordId 账单付款记录id
     */
    public void setBillPayRecordId(Integer billPayRecordId) {
        this.billPayRecordId = billPayRecordId;
    }

    /**
     * 获取当前本金
     * @return BigDecimal
     */
    public BigDecimal getCurrentPrincipal() {
        return currentPrincipal;
    }

    /**
     * 设置当前本金
     * @param currentPrincipal
     */
    public void setCurrentPrincipal(BigDecimal currentPrincipal) {
        this.currentPrincipal = currentPrincipal;
    }

    /**
     * 获取当前证金
     * @return BigDecimal
     */
    public BigDecimal getCurrentBonus() {
        return currentBonus;
    }

    /**
     * 设置当前赠金
     * @param currentBonus
     */
    public void setCurrentBonus(BigDecimal currentBonus) {
        this.currentBonus = currentBonus;
    }
}