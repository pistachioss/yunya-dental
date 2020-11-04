package com.yunya.models.patient_central;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "member_recharge_record")
public class MemberRechargeRecord {
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
     * 会员卡ID
     */
    @Column(name = "member_id")
    private String memberId;

    /**
     * 充值本金
     */
    @Column(name = "recharge_principal")
    private BigDecimal rechargePrincipal;

    /**
     * 充值赠金
     */
    @Column(name = "recharge_bonus")
    private BigDecimal rechargeBonus;

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
    private BigDecimal currentPrincipal;

    /**
     * 充值后当前赠金
     */
    @Column(name = "current_recharge_bonus")
    private BigDecimal currentBonus;


    /**
     *  获取充值后当前本金
     * @return currentRechargePrincipal
     */
    public BigDecimal getCurrentRechargePrincipal() {
        return currentPrincipal;
    }

    /**
     * 设置充值后当前本金
     * @param currentRechargePrincipal
     */
    public void setCurrentRechargePrincipal(BigDecimal currentRechargePrincipal) {
        this.currentPrincipal = currentRechargePrincipal;
    }

    /**
     * 获取 充值后当前赠金
     * @return currentRechargeBonus
     */
    public BigDecimal getCurrentRechargeBonus() {
        return currentBonus;
    }

    /**
     * 设置 充值后当前赠金
     * @param currentRechargeBonus
     */
    public void setCurrentRechargeBonus(BigDecimal currentRechargeBonus) {
        this.currentBonus = currentRechargeBonus;
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
     * 获取会员卡ID
     *
     * @return member_id - 会员卡ID
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * 设置会员卡ID
     *
     * @param memberId 会员卡ID
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    /**
     * 获取充值本金
     *
     * @return recharge_principal - 充值本金
     */
    public BigDecimal getRechargePrincipal() {
        return rechargePrincipal;
    }

    /**
     * 设置充值本金
     *
     * @param rechargePrincipal 充值本金
     */
    public void setRechargePrincipal(BigDecimal rechargePrincipal) {
        this.rechargePrincipal = rechargePrincipal;
    }

    /**
     * 获取充值赠金
     *
     * @return recharge_bonus - 充值赠金
     */
    public BigDecimal getRechargeBonus() {
        return rechargeBonus;
    }

    /**
     * 设置充值赠金
     *
     * @param rechargeBonus 充值赠金
     */
    public void setRechargeBonus(BigDecimal rechargeBonus) {
        this.rechargeBonus = rechargeBonus;
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