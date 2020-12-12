package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "base_bill_pay")
public class BaseBillPay {
    /**
     * 订单收费记录ID
     */
    @Id
    @Column(name = "bill_pay_id")
    private Integer billPayId;

    /**
     * 订单ID
     */
    @Column(name = "bill_id")
    private Integer billId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 就诊ID
     */
    @Column(name = "treatment_id")
    private Integer treatmentId;

    /**
     * 收款人ID
     */
    @Column(name = "payee_user_id")
    private Integer payeeUserId;

    /**
     * 收款日期
     */
    @Column(name = "payee_date")
    private Date payeeDate;

    /**
     * 本次收费总额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 本次仍欠费总额
     */
    @Column(name = "still_owe_amount")
    private BigDecimal stillOweAmount;

    /**
     * 获取订单收费记录ID
     *
     * @return bill_pay_id - 订单收费记录ID
     */
    public Integer getBillPayId() {
        return billPayId;
    }

    /**
     * 设置订单收费记录ID
     *
     * @param billPayId 订单收费记录ID
     */
    public void setBillPayId(Integer billPayId) {
        this.billPayId = billPayId;
    }

    /**
     * 获取订单ID
     *
     * @return bill_id - 订单ID
     */
    public Integer getBillId() {
        return billId;
    }

    /**
     * 设置订单ID
     *
     * @param billId 订单ID
     */
    public void setBillId(Integer billId) {
        this.billId = billId;
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
     * 获取就诊ID
     *
     * @return treatment_id - 就诊ID
     */
    public Integer getTreatmentId() {
        return treatmentId;
    }

    /**
     * 设置就诊ID
     *
     * @param treatmentId 就诊ID
     */
    public void setTreatmentId(Integer treatmentId) {
        this.treatmentId = treatmentId;
    }

    /**
     * 获取收款人ID
     *
     * @return payee_user_id - 收款人ID
     */
    public Integer getPayeeUserId() {
        return payeeUserId;
    }

    /**
     * 设置收款人ID
     *
     * @param payeeUserId 收款人ID
     */
    public void setPayeeUserId(Integer payeeUserId) {
        this.payeeUserId = payeeUserId;
    }

    /**
     * 获取收款日期
     *
     * @return payee_date - 收款日期
     */
    public Date getPayeeDate() {
        return payeeDate;
    }

    /**
     * 设置收款日期
     *
     * @param payeeDate 收款日期
     */
    public void setPayeeDate(Date payeeDate) {
        this.payeeDate = payeeDate;
    }

    /**
     * 获取本次收费总额
     *
     * @return received_amount - 本次收费总额
     */
    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * 设置本次收费总额
     *
     * @param receivedAmount 本次收费总额
     */
    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    /**
     * 获取本次仍欠费总额
     *
     * @return still_owe_amount - 本次仍欠费总额
     */
    public BigDecimal getStillOweAmount() {
        return stillOweAmount;
    }

    /**
     * 设置本次仍欠费总额
     *
     * @param stillOweAmount 本次仍欠费总额
     */
    public void setStillOweAmount(BigDecimal stillOweAmount) {
        this.stillOweAmount = stillOweAmount;
    }
}