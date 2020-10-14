package com.yunya.models.patient_central;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "prepaid_recharge_record")
public class PrepaidRechargeRecord {
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
     * 预付款卡ID
     */
    @Column(name = "prepaid_id")
    private String prepaidId;

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
    @Column(name = "remarks")
    private String remarks;

    /**
     * 充值卡号 充值卡号
     */
    @Column(name = "recharge_card_number")
    private String rechargeCardNumber;

    /**
     * 类型 0普通充值 1充值卡充值
     */
    @Column(name = "recharge_type")
    private Byte rechargeType;

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
     *  获取充值后当前本金
     * @return currentRechargePrincipal
     */
    public BigDecimal getCurrentRechargePrincipal() {
        return currentRechargePrincipal;
    }

    /**
     * 设置充值后当前本金
     * @param currentRechargePrincipal
     */
    public void setCurrentRechargePrincipal(BigDecimal currentRechargePrincipal) {
        this.currentRechargePrincipal = currentRechargePrincipal;
    }

    /**
     * 获取 充值后当前赠金
     * @return currentRechargeBonus
     */
    public BigDecimal getCurrentRechargeBonus() {
        return currentRechargeBonus;
    }

    /**
     * 设置 充值后当前赠金
     * @param currentRechargeBonus
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
     * 获取预付款卡ID
     *
     * @return prepaid_id - 预付款卡ID
     */
    public String getPrepaidId() {
        return prepaidId;
    }

    /**
     * 设置预付款卡ID
     *
     * @param prepaidId 预付款卡ID
     */
    public void setPrepaidId(String prepaidId) {
        this.prepaidId = prepaidId;
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
     * 获取充值卡号 充值卡号
     *
     * @return recharge_card_number - 充值卡号 充值卡号
     */
    public String getRechargeCardNumber() {
        return rechargeCardNumber;
    }

    /**
     * 设置充值卡号 充值卡号
     *
     * @param rechargeCardNumber 充值卡号 充值卡号
     */
    public void setRechargeCardNumber(String rechargeCardNumber) {
        this.rechargeCardNumber = rechargeCardNumber;
    }

    /**
     * 获取类型 0普通充值 1充值卡充值
     *
     * @return recharge_type - 类型 0普通充值 1充值卡充值
     */
    public Byte getRechargeType() {
        return rechargeType;
    }

    /**
     * 设置类型 0普通充值 1充值卡充值
     *
     * @param rechargeType 类型 0普通充值 1充值卡充值
     */
    public void setRechargeType(Byte rechargeType) {
        this.rechargeType = rechargeType;
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