package com.yunya.models.treatment_other;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "qc_treatment_record")
public class QcTreatmentRecord {
    @Id
    private Integer id;

    /**
     * 平台就诊流水号
     */
    @Column(name = "adm_no")
    private String admNo;

    /**
     * 订单记录id
     */
    @Column(name = "order_record_id")
    private Integer orderRecordId;

    /**
     * 绑定患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 就诊账单id
     */
    @Column(name = "bill_id")
    private Integer billId;

    /**
     * 类型：1-医嘱单，2-引导单
     */
    private Byte type;

    /**
     * mall平台-核销码
     */
    @Column(name = "verify_code")
    private String verifyCode;

    /**
     * 核销时间
     */
    @Column(name = "verify_date")
    private Date verifyDate;

    /**
     * 核销操作人id
     */
    @Column(name = "verify_id")
    private Integer verifyId;

    /**
     * 状态：1-未核销（已下载），2-未开单（已核销并绑定患者），3-已开单（已绑定账单），4-已同步（已上传）
     */
    private Byte status;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 是否有效
     */
    private Boolean inservice;

    /**
     * 平台就诊时间
     */
    @Column(name = "adm_date")
    private Date admDate;

    /**
     * 同步人id
     */
    @Column(name = "sync_id")
    private Integer syncId;

    /**
     * 同步时间
     */
    @Column(name = "sync_time")
    private Date syncTime;

    /**
     * 客户姓名
     */
    @Column(name = "customer_name")
    private String customerName;

    /**
     * 身份证号
     */
    @Column(name = "id_card")
    private String idCard;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 艾维开单总实收
     */
    @Column(name = "ivy_cost")
    private BigDecimal ivyCost;

    /**
     * 更新人id
     */
    @Column(name = "upd_id")
    private Integer updId;

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
     * 获取平台就诊流水号
     *
     * @return adm_no - 平台就诊流水号
     */
    public String getAdmNo() {
        return admNo;
    }

    /**
     * 设置平台就诊流水号
     *
     * @param admNo 平台就诊流水号
     */
    public void setAdmNo(String admNo) {
        this.admNo = admNo;
    }

    /**
     * 获取订单记录id
     *
     * @return order_record_id - 订单记录id
     */
    public Integer getOrderRecordId() {
        return orderRecordId;
    }

    /**
     * 设置订单记录id
     *
     * @param orderRecordId 订单记录id
     */
    public void setOrderRecordId(Integer orderRecordId) {
        this.orderRecordId = orderRecordId;
    }

    /**
     * 获取绑定患者id
     *
     * @return patient_id - 绑定患者id
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置绑定患者id
     *
     * @param patientId 绑定患者id
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取就诊账单id
     *
     * @return bill_id - 就诊账单id
     */
    public Integer getBillId() {
        return billId;
    }

    /**
     * 设置就诊账单id
     *
     * @param billId 就诊账单id
     */
    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    /**
     * 获取类型：1-医嘱单，2-引导单
     *
     * @return type - 类型：1-医嘱单，2-引导单
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置类型：1-医嘱单，2-引导单
     *
     * @param type 类型：1-医嘱单，2-引导单
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取mall平台-核销码
     *
     * @return verify_code - mall平台-核销码
     */
    public String getVerifyCode() {
        return verifyCode;
    }

    /**
     * 设置mall平台-核销码
     *
     * @param verifyCode mall平台-核销码
     */
    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    /**
     * 获取核销时间
     *
     * @return verify_date - 核销时间
     */
    public Date getVerifyDate() {
        return verifyDate;
    }

    /**
     * 设置核销时间
     *
     * @param verifyDate 核销时间
     */
    public void setVerifyDate(Date verifyDate) {
        this.verifyDate = verifyDate;
    }

    /**
     * 获取核销操作人id
     *
     * @return verify_id - 核销操作人id
     */
    public Integer getVerifyId() {
        return verifyId;
    }

    /**
     * 设置核销操作人id
     *
     * @param verifyId 核销操作人id
     */
    public void setVerifyId(Integer verifyId) {
        this.verifyId = verifyId;
    }

    /**
     * 获取状态：1-未核销（已下载），2-未开单（已核销并绑定患者），3-已开单（已绑定账单），4-已同步（已上传）
     *
     * @return
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态：1-未核销（已下载），2-未开单（已核销并绑定患者），3-已开单（已绑定账单），4-已同步（已上传）
     *
     * @param status
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取备注信息
     *
     * @return remark - 备注信息
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
     * 获取客户姓名
     *
     * @return
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * 设置客户姓名
     *
     * @param customerName
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * 获取 身份证号
     *
     * @return
     */
    public String getIdCard() {
        return idCard;
    }

    /**
     * 设置身份证号
     *
     * @param idCard
     */
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    /**
     * 获取手机号码
     *
     * @return
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置手机号码
     *
     * @param mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
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
     * 获取平台就诊时间
     *
     * @return adm_date - 平台就诊时间
     */
    public Date getAdmDate() {
        return admDate;
    }

    /**
     * 设置平台就诊时间
     *
     * @param admDate 平台就诊时间
     */
    public void setAdmDate(Date admDate) {
        this.admDate = admDate;
    }

    /**
     * 获取同步人id
     *
     * @return sync_id - 同步人id
     */
    public Integer getSyncId() {
        return syncId;
    }

    /**
     * 设置同步人id
     *
     * @param syncId 同步人id
     */
    public void setSyncId(Integer syncId) {
        this.syncId = syncId;
    }

    /**
     * 获取同步时间
     *
     * @return sync_time - 同步时间
     */
    public Date getSyncTime() {
        return syncTime;
    }

    /**
     * 设置同步时间
     *
     * @param syncTime 同步时间
     */
    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    /**
     * 获取更新人id
     *
     * @return upd_id - 更新人id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人id
     *
     * @param updId 更新人id
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
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

    /**
     * 获取 艾维开单总实收
     *
     * @return
     */
    public BigDecimal getIvyCost() {
        return ivyCost;
    }

    /**
     * 设置 艾维开单总实收
     *
     * @param ivyCost
     */
    public void setIvyCost(BigDecimal ivyCost) {
        this.ivyCost = ivyCost;
    }
}