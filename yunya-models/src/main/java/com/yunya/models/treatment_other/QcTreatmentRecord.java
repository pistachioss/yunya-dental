package com.yunya.models.treatment_other;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "qc_treatment_record")
public class QcTreatmentRecord {
    @Id
    @GeneratedValue(generator = "JDBC")
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
     * 账单收费id
     */
    @Column(name = "bill_pay_id")
    private Integer billPayId;

    /**
     * 类型：O-医嘱单，L-引导单
     */
    private String type;

    /**
     * mall平台-核销码
     */
    @Column(name = "verify_code")
    private String verifyCode;

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
     * 获取账单收费id
     *
     * @return bill_pay_id - 账单收费id
     */
    public Integer getBillPayId() {
        return billPayId;
    }

    /**
     * 设置账单收费id
     *
     * @param billPayId 账单收费id
     */
    public void setBillPayId(Integer billPayId) {
        this.billPayId = billPayId;
    }

    /**
     * 获取类型：O-医嘱单，L-引导单
     *
     * @return type - 类型：O-医嘱单，L-引导单
     */
    public String getType() {
        return type;
    }

    /**
     * 设置类型：O-医嘱单，L-引导单
     *
     * @param type 类型：O-医嘱单，L-引导单
     */
    public void setType(String type) {
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
}