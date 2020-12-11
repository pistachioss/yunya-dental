package com.yunya.models.patient_central;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "member_return_record")
public class MemberReturnRecord {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 会员卡号
     */
    @Column(name = "member_id")
    private String memberId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

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
     * 退费方式ID
     */
    @Column(name = "return_way_id")
    private Integer returnWayId;

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
     * @return member_id - 会员卡号
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * 设置会员卡号
     *
     * @param memberId 会员卡号
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
    public String getReturnReason() {
        return remarks;
    }

    /**
     * 设置退费原因
     *
     * @param remarks 退费原因
     */
    public void setReturnReason(String remarks) {
        this.remarks = remarks;
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
     * 获取备注
     * @return remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注
     * @param remarks 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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