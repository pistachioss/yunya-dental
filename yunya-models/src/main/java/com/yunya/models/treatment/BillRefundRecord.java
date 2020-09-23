package com.yunya.models.treatment;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "bill_refund_record")
public class BillRefundRecord {
    /**
     * 退费记录ID
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

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
     * 就诊记录ID
     */
    @Column(name = "treatment_record_id")
    private Integer treatmentRecordId;

    /**
     * 开单记录ID
     */
    @Column(name = "order_record_id")
    private Integer orderRecordId;

    /**
     * 退款总额
     */
    @Column(name = "total_refund_amount")
    private BigDecimal totalRefundAmount;

    /**
     * 退费原因
     */
    private String reason;

    /**
     * 退费状态
     */
    private Byte status;

    /**
     * 审核人ID
     */
    @Column(name = "approver_id")
    private Integer approverId;

    /**
     * 退费凭证(多个用法逗号隔开)
     */
    @Column(name = "refund_certificate")
    private String refundCertificate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否有效
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
     * 获取退费记录ID
     *
     * @return id - 退费记录ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置退费记录ID
     *
     * @param id 退费记录ID
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
     * 获取就诊记录ID
     *
     * @return treatment_record_id - 就诊记录ID
     */
    public Integer getTreatmentRecordId() {
        return treatmentRecordId;
    }

    /**
     * 设置就诊记录ID
     *
     * @param treatmentRecordId 就诊记录ID
     */
    public void setTreatmentRecordId(Integer treatmentRecordId) {
        this.treatmentRecordId = treatmentRecordId;
    }

    /**
     * 获取开单记录ID
     *
     * @return order_record_id - 开单记录ID
     */
    public Integer getOrderRecordId() {
        return orderRecordId;
    }

    /**
     * 设置开单记录ID
     *
     * @param orderRecordId 开单记录ID
     */
    public void setOrderRecordId(Integer orderRecordId) {
        this.orderRecordId = orderRecordId;
    }

    /**
     * 获取退款总额
     *
     * @return total_refund_amount - 退款总额
     */
    public BigDecimal getTotalRefundAmount() {
        return totalRefundAmount;
    }

    /**
     * 设置退款总额
     *
     * @param totalRefundAmount 退款总额
     */
    public void setTotalRefundAmount(BigDecimal totalRefundAmount) {
        this.totalRefundAmount = totalRefundAmount;
    }

    /**
     * 获取退费原因
     *
     * @return reason - 退费原因
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置退费原因
     *
     * @param reason 退费原因
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 获取退费状态
     *
     * @return status - 退费状态
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置退费状态
     *
     * @param status 退费状态
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取审核人ID
     *
     * @return approver_id - 审核人ID
     */
    public Integer getApproverId() {
        return approverId;
    }

    /**
     * 设置审核人ID
     *
     * @param approverId 审核人ID
     */
    public void setApproverId(Integer approverId) {
        this.approverId = approverId;
    }

    /**
     * 获取退费凭证(多个用法逗号隔开)
     *
     * @return refund_certificate - 退费凭证(多个用法逗号隔开)
     */
    public String getRefundCertificate() {
        return refundCertificate;
    }

    /**
     * 设置退费凭证(多个用法逗号隔开)
     *
     * @param refundCertificate 退费凭证(多个用法逗号隔开)
     */
    public void setRefundCertificate(String refundCertificate) {
        this.refundCertificate = refundCertificate;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取是否有效
     *
     * @return inservice - 是否有效
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效
     *
     * @param inservice 是否有效
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