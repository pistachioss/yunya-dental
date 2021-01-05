package com.yunya.report.ultimate.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "current_month_bill_statistics")
public class CurrentMonthBillStatistics {
    /**
     * 主键ID
     */
    @Id
    private Integer id;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 当前日期（年月）
     */
    @Column(name = "current_month")
    private Date currentMonth;

    /**
     * 当月实收金额合计
     */
    @Column(name = "actual_receivable_amount")
    private BigDecimal actualReceivableAmount;

    /**
     * 当月优化金额合计
     */
    @Column(name = "privelege_amount")
    private BigDecimal privelegeAmount;

    /**
     * 当月已收金额合计
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 当月账单欠费合计
     */
    @Column(name = "debt_amount")
    private BigDecimal debtAmount;

    /**
     * 获取主键ID
     *
     * @return id - 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
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
     * 获取当前日期（年月）
     *
     * @return current_month - 当前日期（年月）
     */
    public Date getCurrentMonth() {
        return currentMonth;
    }

    /**
     * 设置当前日期（年月）
     *
     * @param currentMonth 当前日期（年月）
     */
    public void setCurrentMonth(Date currentMonth) {
        this.currentMonth = currentMonth;
    }

    /**
     * 获取当月实收金额合计
     *
     * @return actual_receivable_amount - 当月实收金额合计
     */
    public BigDecimal getActualReceivableAmount() {
        return actualReceivableAmount;
    }

    /**
     * 设置当月实收金额合计
     *
     * @param actualReceivableAmount 当月实收金额合计
     */
    public void setActualReceivableAmount(BigDecimal actualReceivableAmount) {
        this.actualReceivableAmount = actualReceivableAmount;
    }

    /**
     * 获取当月优化金额合计
     *
     * @return privelege_amount - 当月优化金额合计
     */
    public BigDecimal getPrivelegeAmount() {
        return privelegeAmount;
    }

    /**
     * 设置当月优化金额合计
     *
     * @param privelegeAmount 当月优化金额合计
     */
    public void setPrivelegeAmount(BigDecimal privelegeAmount) {
        this.privelegeAmount = privelegeAmount;
    }

    /**
     * 获取当月已收金额合计
     *
     * @return received_amount - 当月已收金额合计
     */
    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * 设置当月已收金额合计
     *
     * @param receivedAmount 当月已收金额合计
     */
    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    /**
     * 获取当月账单欠费合计
     *
     * @return debt_amount - 当月账单欠费合计
     */
    public BigDecimal getDebtAmount() {
        return debtAmount;
    }

    /**
     * 设置当月账单欠费合计
     *
     * @param debtAmount 当月账单欠费合计
     */
    public void setDebtAmount(BigDecimal debtAmount) {
        this.debtAmount = debtAmount;
    }
}