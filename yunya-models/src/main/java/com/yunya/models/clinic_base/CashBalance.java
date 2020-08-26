package com.yunya.models.clinic_base;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "cash_balance")
public class CashBalance {
    @Id
    private Integer id;

    /**
     * 期初现金
     */
    @Column(name = "cash_first")
    private Integer cashFirst;

    /**
     * 期中现金
     */
    @Column(name = "cash_mid")
    private Integer cashMid;

    /**
     * 期末现金
     */
    @Column(name = "cash_end")
    private Integer cashEnd;

    /**
     * 今日存款
     */
    @Column(name = "amount_deposited")
    private Integer amountDeposited;

    /**
     * 差额调整
     */
    @Column(name = "balance_adjustment")
    private Integer balanceAdjustment;

    /**
     * 差额调整备注
     */
    @Column(name = "balance_adjustment_remark")
    private String balanceAdjustmentRemark;

    /**
     * 结存凭证
     */
    private String uri;

    @Column(name = "org_id")
    private Integer orgId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "crt_id")
    private Integer crtId;

    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upe_id")
    private Integer upeId;

    @Column(name = "upd_time")
    private Date updTime;

    @Column(name = "inservice")
    private Integer inservice;


    public Integer getInservice() {
        return inservice;
    }

    public void setInservice(Integer inservice) {
        this.inservice = inservice;
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
     * 获取期初现金
     *
     * @return cash_first - 期初现金
     */
    public Integer getCashFirst() {
        return cashFirst;
    }

    /**
     * 设置期初现金
     *
     * @param cashFirst 期初现金
     */
    public void setCashFirst(Integer cashFirst) {
        this.cashFirst = cashFirst;
    }

    /**
     * 获取期中现金
     *
     * @return cash_mid - 期中现金
     */
    public Integer getCashMid() {
        return cashMid;
    }

    /**
     * 设置期中现金
     *
     * @param cashMid 期中现金
     */
    public void setCashMid(Integer cashMid) {
        this.cashMid = cashMid;
    }

    /**
     * 获取期末现金
     *
     * @return cash_end - 期末现金
     */
    public Integer getCashEnd() {
        return cashEnd;
    }

    /**
     * 设置期末现金
     *
     * @param cashEnd 期末现金
     */
    public void setCashEnd(Integer cashEnd) {
        this.cashEnd = cashEnd;
    }

    /**
     * 获取今日存款
     *
     * @return amount_deposited - 今日存款
     */
    public Integer getAmountDeposited() {
        return amountDeposited;
    }

    /**
     * 设置今日存款
     *
     * @param amountDeposited 今日存款
     */
    public void setAmountDeposited(Integer amountDeposited) {
        this.amountDeposited = amountDeposited;
    }

    /**
     * 获取差额调整
     *
     * @return balance_adjustment - 差额调整
     */
    public Integer getBalanceAdjustment() {
        return balanceAdjustment;
    }

    /**
     * 设置差额调整
     *
     * @param balanceAdjustment 差额调整
     */
    public void setBalanceAdjustment(Integer balanceAdjustment) {
        this.balanceAdjustment = balanceAdjustment;
    }

    /**
     * 获取差额调整备注
     *
     * @return balance_adjustment_remark - 差额调整备注
     */
    public String getBalanceAdjustmentRemark() {
        return balanceAdjustmentRemark;
    }

    /**
     * 设置差额调整备注
     *
     * @param balanceAdjustmentRemark 差额调整备注
     */
    public void setBalanceAdjustmentRemark(String balanceAdjustmentRemark) {
        this.balanceAdjustmentRemark = balanceAdjustmentRemark;
    }

    /**
     * 获取结存凭证
     *
     * @return uri - 结存凭证
     */
    public String getUri() {
        return uri;
    }

    /**
     * 设置结存凭证
     *
     * @param uri 结存凭证
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return org_id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * @param orgId
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * @return user_id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return crt_id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * @param crtId
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * @return crt_time
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * @param crtTime
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * @return upe_id
     */
    public Integer getUpeId() {
        return upeId;
    }

    /**
     * @param upeId
     */
    public void setUpeId(Integer upeId) {
        this.upeId = upeId;
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