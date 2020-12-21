package com.yunya.models.clinic_base;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "cash_balance")
public class CashBalance {
    @Id
    private Integer id;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 结存日期
     */
    @Column(name = "settlement_date")
    private Date settlementDate;

    /**
     * 期初现金结余
     */
    @Column(name = "beginning_balance_cash")
    private BigDecimal beginningBalanceCash;

    /**
     * 期间现金收款
     */
    @Column(name = "period_collection")
    private BigDecimal periodCollection;

    /**
     * 当日现金存款
     */
    @Column(name = "deposited_cash")
    private BigDecimal depositedCash;

    /**
     * 期末现金结余
     */
    @Column(name = "ending_balance_cash")
    private BigDecimal endingBalanceCash;

    /**
     * 差额调整
     */
    @Column(name = "balance_adjustment")
    private BigDecimal balanceAdjustment;

    /**
     * 差额调整备注
     */
    @Column(name = "adjust_remark")
    private String adjustRemark;

    /**
     * 结存凭证
     */
    private String uri;

    /**
     * 是否启用 0-否；1-是
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
    @Column(name = "upd_id")
    private Integer updId;

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
     * 获取组织ID
     *
     * @return org_id - 组织ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织ID
     *
     * @param orgId 组织ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取结存日期
     *
     * @return settlement_date - 结存日期
     */
    public Date getSettlementDate() {
        return settlementDate;
    }

    /**
     * 设置结存日期
     *
     * @param settlementDate 结存日期
     */
    public void setSettlementDate(Date settlementDate) {
        this.settlementDate = settlementDate;
    }

    /**
     * 获取期初现金结余
     *
     * @return beginning_balance_cash - 期初现金结余
     */
    public BigDecimal getBeginningBalanceCash() {
        return beginningBalanceCash;
    }

    /**
     * 设置期初现金结余
     *
     * @param beginningBalanceCash 期初现金结余
     */
    public void setBeginningBalanceCash(BigDecimal beginningBalanceCash) {
        this.beginningBalanceCash = beginningBalanceCash;
    }

    /**
     * 获取期间现金收款
     *
     * @return period_collection - 期间现金收款
     */
    public BigDecimal getPeriodCollection() {
        return periodCollection;
    }

    /**
     * 设置期间现金收款
     *
     * @param periodCollection 期间现金收款
     */
    public void setPeriodCollection(BigDecimal periodCollection) {
        this.periodCollection = periodCollection;
    }

    /**
     * 获取当日现金存款
     *
     * @return deposited_cash - 当日现金存款
     */
    public BigDecimal getDepositedCash() {
        return depositedCash;
    }

    /**
     * 设置当日现金存款
     *
     * @param depositedCash 当日现金存款
     */
    public void setDepositedCash(BigDecimal depositedCash) {
        this.depositedCash = depositedCash;
    }

    /**
     * 获取期末现金结余
     *
     * @return ending_balance_cash - 期末现金结余
     */
    public BigDecimal getEndingBalanceCash() {
        return endingBalanceCash;
    }

    /**
     * 设置期末现金结余
     *
     * @param endingBalanceCash 期末现金结余
     */
    public void setEndingBalanceCash(BigDecimal endingBalanceCash) {
        this.endingBalanceCash = endingBalanceCash;
    }

    /**
     * 获取差额调整
     *
     * @return balance_adjustment - 差额调整
     */
    public BigDecimal getBalanceAdjustment() {
        return balanceAdjustment;
    }

    /**
     * 设置差额调整
     *
     * @param balanceAdjustment 差额调整
     */
    public void setBalanceAdjustment(BigDecimal balanceAdjustment) {
        this.balanceAdjustment = balanceAdjustment;
    }

    /**
     * 获取差额调整备注
     *
     * @return adjust_remark - 差额调整备注
     */
    public String getAdjustRemark() {
        return adjustRemark;
    }

    /**
     * 设置差额调整备注
     *
     * @param adjustRemark 差额调整备注
     */
    public void setAdjustRemark(String adjustRemark) {
        this.adjustRemark = adjustRemark;
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
     * 获取是否启用 0-否；1-是
     *
     * @return inservice - 是否启用 0-否；1-是
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用 0-否；1-是
     *
     * @param inservice 是否启用 0-否；1-是
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
     * @return upd_id - 更新人ID
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人ID
     *
     * @param updId 更新人ID
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
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