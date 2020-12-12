package com.yunya.models.treatment;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "bill_pay_detail_record")
public class BillPayDetailRecord {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 组织（诊所）id
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
     * 账单收费记录
     */
    @Column(name = "bill_pay_record_id")
    private Integer billPayRecordId;

    /**
     * 入账方式类型（0-预付款；1-会员卡；3-其他支付方式）
     */
    private Byte type;

    /**
     * 入账方式明细ID
     */
    @Column(name = "account_item_id")
    private Integer accountItemId;

    /**
     * 入账金额
     */
    private BigDecimal amount;

    /**
     * 交易号
     */
    @Column(name = "transaction_number")
    private String transactionNumber;

    /**
     * 交易状态
     */
    private Byte status;

    /**
     * 备注信息信息
     * */
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
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 创建时间
     */
    @Column(name = "crt_name")
    private String crtName;

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
     * 获取组织（诊所）id
     *
     * @return org_id - 组织（诊所）id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织（诊所）id
     *
     * @param orgId 组织（诊所）id
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
     * @return treatment_record_id - 就诊记录id
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
     * 获取账单收费记录ID
     *
     * @return bill_pay_record_id - 账单收费记录ID
     */
    public Integer getBillPayRecordId() {
        return billPayRecordId;
    }

    /**
     * 设置账单收费记录
     *
     * @param billPayRecordId 账单收费记录
     */
    public void setBillPayRecordId(Integer billPayRecordId) {
        this.billPayRecordId = billPayRecordId;
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
     * 获取入账方式类型（0-预付款；1-会员卡；2-其他支付方式）
     *
     * @return type - 入账方式类型（0-预付款；1-会员卡；2-其他支付方式）
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置入账方式类型（0-预付款；1-会员卡；3-其他支付方式）
     *
     * @param type 入账方式类型（0-预付款；1-会员卡；3-其他支付方式）
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取入账方式明细ID
     *
     * @return account_item_id - 入账方式明细ID
     */
    public Integer getAccountItemId() {
        return accountItemId;
    }

    /**
     * 设置入账方式明细ID
     *
     * @param accountItemId 入账方式明细ID
     */
    public void setAccountItemId(Integer accountItemId) {
        this.accountItemId = accountItemId;
    }

    /**
     * 获取入账金额
     *
     * @return amount - 入账金额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置入账金额
     *
     * @param amount 入账金额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 获取交易号
     *
     * @return transaction_number - 交易号
     */
    public String getTransactionNumber() {
        return transactionNumber;
    }

    /**
     * 设置交易号
     *
     * @param transactionNumber 交易号
     */
    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    /**
     * 获取交易状态
     *
     * @return status - 交易状态
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置交易状态
     *
     * @param status 交易状态
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取备注信息
     *
     * @return remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注信息
     *
     * @param remark 备注信息
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
     * @return crt_time - 创建人姓名
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建人姓名
     *
     * @param crtTime 创建人姓名
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * 获取创建时间
     *
     * @return crt_name - 创建时间
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建时间
     *
     * @param crtName 创建时间
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
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