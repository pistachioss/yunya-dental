package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "base_coupon_bill_pay")
public class BaseCouponBillPay {
    /**
     * 账单收费记录ID
     */
    @Id
    @Column(name = "bill_pay_id")
    private Integer billPayId;

    /**
     * 订单ID
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

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
     * 欠费总额
     */
    @Column(name = "owe_amount")
    private BigDecimal oweAmount;

    /**
     * 获取账单收费记录ID
     *
     * @return bill_pay_id - 账单收费记录ID
     */
    public Integer getBillPayId() {
        return billPayId;
    }

    /**
     * 设置账单收费记录ID
     *
     * @param billPayId 账单收费记录ID
     */
    public void setBillPayId(Integer billPayId) {
        this.billPayId = billPayId;
    }

    /**
     * 获取订单ID
     *
     * @return order_id - 订单ID
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单ID
     *
     * @param orderId 订单ID
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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
     * 获取欠费总额
     *
     * @return owe_amount - 欠费总额
     */
    public BigDecimal getOweAmount() {
        return oweAmount;
    }

    /**
     * 设置欠费总额
     *
     * @param oweAmount 欠费总额
     */
    public void setOweAmount(BigDecimal oweAmount) {
        this.oweAmount = oweAmount;
    }
}