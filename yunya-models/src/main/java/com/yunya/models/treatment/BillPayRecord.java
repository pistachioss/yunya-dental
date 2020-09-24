package com.yunya.models.treatment;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "bill_pay_record")
public class BillPayRecord {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 组织id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 就诊记录id
     */
    @Column(name = "treatment_record_id")
    private Integer treatmentRecordId;

    /**
     * 开单记录id
     */
    @Column(name = "order_record_id")
    private Integer orderRecordId;

    /**
     * 账单记录ID
     */
    @Column(name = "bill_record_id")
    private Integer billRecordId;

    /**
     * 本次收费合计金额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 仍欠费金额
     */
    @Column(name = "still_owe_amount")
    private BigDecimal stillOweAmount;

    /**
     * 是否有效
     */
    private Boolean inservice;

    /**
     * 收款人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 收款人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 收款时间
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
     * 获取组织id
     *
     * @return org_id - 组织id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织id
     *
     * @param orgId 组织id
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
     * 获取就诊记录id
     *
     * @return treatment_id - 就诊记录id
     */
    public Integer getTreatmentRecordId() {
        return treatmentRecordId;
    }

    /**
     * 设置就诊记录id
     *
     * @param treatmentRecordId 就诊记录id
     */
    public void setTreatmentRecordId(Integer treatmentRecordId) {
        this.treatmentRecordId = treatmentRecordId;
    }

    /**
     * 获取开单记录id
     *
     * @return order_record_id - 开单记录id
     */
    public Integer getOrderRecordId() {
        return orderRecordId;
    }

    /**
     * 设置开单记录id
     *
     * @param orderRecordId 开单记录id
     */
    public void setOrderRecordId(Integer orderRecordId) {
        this.orderRecordId = orderRecordId;
    }

    /**
     * 获取账单记录ID
     *
     * @return bill_record_id - 账单记录ID
     */
    public Integer getBillRecordId() {
        return billRecordId;
    }

    /**
     * 设置账单记录ID
     *
     * @param billRecordId 账单记录ID
     */
    public void setBillRecordId(Integer billRecordId) {
        this.billRecordId = billRecordId;
    }

    /**
     * 获取本次收费合计金额
     *
     * @return received_amount - 本次收费合计金额
     */
    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * 设置本次收费合计金额
     *
     * @param receivedAmount 本次收费合计金额
     */
    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    /**
     * 获取仍欠费金额
     *
     * @return still_owe - 仍欠费金额
     */
    public BigDecimal getStillOweAmount() {
        return stillOweAmount;
    }

    /**
     * 设置仍欠费金额
     *
     * @param stillOweAmount 仍欠费金额
     */
    public void setStillOweAmount(BigDecimal stillOweAmount) {
        this.stillOweAmount = stillOweAmount;
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
     * 获取收款人id
     *
     * @return crt_id - 收款人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置收款人id
     *
     * @param crtId 收款人id
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取收款人姓名
     *
     * @return crt_name - 收款人姓名
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置收款人姓名
     *
     * @param crtName 收款人姓名
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
    }

    /**
     * 获取收款时间
     *
     * @return crt_time - 收款时间
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置收款时间
     *
     * @param crtTime 收款时间
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