package com.yunya.models.treatment;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "bill_record")
public class BillRecord {
    /**
     * 账单ID
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 组织（门诊）id
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
     * 账单编号（ZD+门诊号+时间戳）
     */
    @Column(name = "bill_number")
    private String billNumber;

    /**
     * 应收金额（消费总额）
     */
    @Column(name = "receivable_amount")
    private BigDecimal receivableAmount;

    /**
     * 优惠方式（0-未使用优惠；1-一般优惠；2-授权折扣；3-混搭（价目使用优惠+商品使用折扣））
     * */
    @Column(name = "privilege_type")
    private Byte privilegeType;

    /**
     * 优惠使用门诊
     * */
    @Column(name = "privilege_org_id")
    private Integer privilegeOrgId;

    /**
     * 本单优惠总额
     */
    @Column(name = "privilege_amount")
    private BigDecimal privilegeAmount;

    /**
     * 本单优惠日期
     * */
    @Column(name = "privilege_date")
    private Date privilegeDate;

    /**
     * 是否首次收费使用优惠
     */
    @Column(name = "first_privilege")
    private Boolean firstPrivilege;

    /**
     * 实际应收金额
     */
    @Column(name = "actual_receivable_amount")
    private BigDecimal actualReceivableAmount;

    /**
     * 已收金额（本单收费总额）
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 欠费金额（本单欠费）
     */
    @Column(name = "debt_amount")
    private BigDecimal debtAmount;

    /**
     * 是否开发票
     */
    private Boolean invoice;

    /**
     * 发票编号
     * */
    @Column(name = "invoice_number")
    private String invoiceNumber;

    /**
     * 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人id
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
     * 获取账单ID
     *
     * @return id - 账单ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置账单ID
     *
     * @param id 账单ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取组织（门诊）id
     *
     * @return org_id - 组织（门诊）id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织（门诊）id
     *
     * @param orgId 组织（门诊）id
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
     * 获取账单编号（ZD+门诊号+时间戳）
     *
     * @return bill_number - 账单编号（ZD+门诊号+时间戳）
     */
    public String getBillNumber() {
        return billNumber;
    }

    /**
     * 设置账单编号（ZD+门诊号+时间戳）
     *
     * @param billNumber 账单编号（ZD+门诊号+时间戳）
     */
    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    /**
     * 获取应收金额（消费总额）
     *
     * @return receivable_amount - 应收金额（消费总额）
     */
    public BigDecimal getReceivableAmount() {
        return receivableAmount;
    }

    /**
     * 设置应收金额（消费总额）
     *
     * @param receivableAmount 应收金额（消费总额）
     */
    public void setReceivableAmount(BigDecimal receivableAmount) {
        this.receivableAmount = receivableAmount;
    }

    /**
     * 获取优惠方式
     *
     * @return privilegeType 优惠方式（0-未使用优惠；1-一般优惠；2-授权折扣；3-混搭（价目使用优惠+商品使用折扣））
     */
    public Byte getPrivilegeType() {
        return privilegeType;
    }

    /**
     * 设置优惠方式
     *
     * @param privilegeType 优惠方式（0-未使用优惠；1-一般优惠；2-授权折扣；3-混搭（价目使用优惠+商品使用折扣））
     */
    public void setPrivilegeType(Byte privilegeType) {
        this.privilegeType = privilegeType;
    }

    /**
     * 获取优惠使用门诊
     *
     * @return privilegeOrgId - 优惠使用门诊
     */
    public Integer getPrivilegeOrgId() {
        return privilegeOrgId;
    }

    /**
     * 设置优惠使用门诊
     *
     * @param privilegeOrgId - 优惠使用门诊
     */
    public void setPrivilegeOrgId(Integer privilegeOrgId) {
        this.privilegeOrgId = privilegeOrgId;
    }

    /**
     * 获取本单优惠总额
     *
     * @return privilege_amount - 本单优惠总额
     */
    public BigDecimal getPrivilegeAmount() {
        return privilegeAmount;
    }

    /**
     * 设置本单优惠总额
     *
     * @param privilegeAmount 本单优惠总额
     */
    public void setPrivilegeAmount(BigDecimal privilegeAmount) {
        this.privilegeAmount = privilegeAmount;
    }

    /**
     * 获取优惠日期
     *
     * @return privilege_date 优惠日期
     */
    public Date getPrivilegeDate() {
        return privilegeDate;
    }

    /**
     * 设置本单优惠日期
     *
     * @param privilegeDate privilege_date 优惠日期
     */
    public void setPrivilegeDate(Date privilegeDate) {
        this.privilegeDate = privilegeDate;
    }

    /**
     * 获取是否首次收费使用优惠
     *
     * @return firstPrivilege - 是否首次收费使用优惠
     */
    public Boolean getFirstPrivilege() {
        return firstPrivilege;
    }

    /**
     * 设置是否首次收费使用优惠
     *
     * @param firstPrivilege - 是否首次收费使用优惠
     */
    public void setFirstPrivilege(Boolean firstPrivilege) {
        this.firstPrivilege = firstPrivilege;
    }

    /**
     * 获取实际应收金额
     *
     * @return actual_receivable_amount - 实际应收金额
     */
    public BigDecimal getActualReceivableAmount() {
        return actualReceivableAmount;
    }

    /**
     * 设置实际应收金额
     *
     * @param actualReceivableAmount 实际应收金额
     */
    public void setActualReceivableAmount(BigDecimal actualReceivableAmount) {
        this.actualReceivableAmount = actualReceivableAmount;
    }

    /**
     * 获取已收金额（本单收费总额）
     *
     * @return received_amount - 已收金额（本单收费总额）
     */
    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * 设置已收金额（本单收费总额）
     *
     * @param receivedAmount 已收金额（本单收费总额）
     */
    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    /**
     * 获取欠费金额（本单欠费）
     *
     * @return debt_amount - 欠费金额（本单欠费）
     */
    public BigDecimal getDebtAmount() {
        return debtAmount;
    }

    /**
     * 设置欠费金额（本单欠费）
     *
     * @param debtAmount 欠费金额（本单欠费）
     */
    public void setDebtAmount(BigDecimal debtAmount) {
        this.debtAmount = debtAmount;
    }

    /**
     * 获取是否开发票
     *
     * @return invoice - 是否开发票
     */
    public Boolean getInvoice() {
        return invoice;
    }

    /**
     * 设置是否开发票
     *
     * @param invoice 是否开发票
     */
    public void setInvoice(Boolean invoice) {
        this.invoice = invoice;
    }

    /**
     * 获取发票编号
     *
     * @return
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * 设置发票编号
     *
     * @param invoiceNumber 发票编号
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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
     * 获取创建人id
     *
     * @return crt_id - 创建人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人id
     *
     * @param crtId 创建人id
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