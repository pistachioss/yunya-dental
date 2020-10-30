package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_refund")
public class BaseRefund {
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
     * 诊疗记录ID
     */
    @Column(name = "treatment_id")
    private Integer treatmentId;

    /**
     * 账单ID(开单记录ID)
     */
    @Column(name = "bill_id")
    private Integer billId;

    /**
     * 退费总额
     */
    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

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
     * 获取诊疗记录ID
     *
     * @return treatment_id - 诊疗记录ID
     */
    public Integer getTreatmentId() {
        return treatmentId;
    }

    /**
     * 设置诊疗记录ID
     *
     * @param treatmentId 诊疗记录ID
     */
    public void setTreatmentId(Integer treatmentId) {
        this.treatmentId = treatmentId;
    }

    /**
     * 获取账单ID
     *
     * @return bill_id - 账单ID
     */
    public Integer getBillId() {
        return billId;
    }

    /**
     * 设置账单ID
     *
     * @param billId 账单ID
     */
    public void setBillId(Integer billId) {
        this.billId = billId;
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
}