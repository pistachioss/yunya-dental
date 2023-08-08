package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "base_coupon_refund")
public class BaseCouponRefund {
    /**
     * 退费记录ID
     */
    @Id
    @Column(name = "refund_id")
    private Integer refundId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 订单id
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 退费总额
     */
    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    /**
     * 退费人ID
     */
    @Column(name = "refund_operator_id")
    private Integer refundOperatorId;

    /**
     * 退费日期
     */
    @Column(name = "refund_date")
    private Date refundDate;

    /**
     * 退费原因
     */
    @Column(name = "refund_reason")
    private String refundReason;

    /**
     * 账单编号
     */
    @Column(name = "bill_num")
    private String billNum;

    /**
     * 账单日期
     */
    @Column(name = "bill_date")
    private Date billDate;

    /**
     * 订单总额
     */
    @Column(name = "order_amount")
    private BigDecimal orderAmount;

    /**
     * 应收总额
     */
    @Column(name = "receivable_amount")
    private BigDecimal receivableAmount;

    /**
     * 账单已收总额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 获取退费记录ID
     *
     * @return refund_id - 退费记录ID
     */
    public Integer getRefundId() {
        return refundId;
    }

    /**
     * 设置退费记录ID
     *
     * @param refundId 退费记录ID
     */
    public void setRefundId(Integer refundId) {
        this.refundId = refundId;
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
     * 获取订单id
     *
     * @return order_id - 订单id
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单id
     *
     * @param orderId 订单id
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取退费总额
     *
     * @return refund_amount - 退费总额
     */
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    /**
     * 设置退费总额
     *
     * @param refundAmount 退费总额
     */
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    /**
     * 获取退费人ID
     *
     * @return refund_operator_id - 退费人ID
     */
    public Integer getRefundOperatorId() {
        return refundOperatorId;
    }

    /**
     * 设置退费人ID
     *
     * @param refundOperatorId 退费人ID
     */
    public void setRefundOperatorId(Integer refundOperatorId) {
        this.refundOperatorId = refundOperatorId;
    }

    /**
     * 获取退费日期
     *
     * @return refund_date - 退费日期
     */
    public Date getRefundDate() {
        return refundDate;
    }

    /**
     * 设置退费日期
     *
     * @param refundDate 退费日期
     */
    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    /**
     * 获取退费原因
     *
     * @return refund_reason - 退费原因
     */
    public String getRefundReason() {
        return refundReason;
    }

    /**
     * 设置退费原因
     *
     * @param refundReason 退费原因
     */
    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    /**
     * 获取账单编号
     *
     * @return bill_num - 账单编号
     */
    public String getBillNum() {
        return billNum;
    }

    /**
     * 设置账单编号
     *
     * @param billNum 账单编号
     */
    public void setBillNum(String billNum) {
        this.billNum = billNum;
    }

    /**
     * 获取账单日期
     *
     * @return bill_date - 账单日期
     */
    public Date getBillDate() {
        return billDate;
    }

    /**
     * 设置账单日期
     *
     * @param billDate 账单日期
     */
    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    /**
     * 获取订单总额
     *
     * @return order_amount - 订单总额
     */
    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    /**
     * 设置订单总额
     *
     * @param orderAmount 订单总额
     */
    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    /**
     * 获取应收总额
     *
     * @return receivable_amount - 应收总额
     */
    public BigDecimal getReceivableAmount() {
        return receivableAmount;
    }

    /**
     * 设置应收总额
     *
     * @param receivableAmount 应收总额
     */
    public void setReceivableAmount(BigDecimal receivableAmount) {
        this.receivableAmount = receivableAmount;
    }

    /**
     * 获取账单已收总额
     *
     * @return received_amount - 账单已收总额
     */
    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * 设置账单已收总额
     *
     * @param receivedAmount 账单已收总额
     */
    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }
}