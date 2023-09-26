package com.yunya.models.report;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "base_qcyl_treatment")
public class BaseQcylTreatment {
    /**
     * 全程就诊记录id
     */
    @Id
    @Column(name = "qc_treatment_id")
    private Integer qcTreatmentId;

    /**
     * mall平台就诊流水号
     */
    @Column(name = "adm_no")
    private String admNo;

    /**
     * 客户姓名
     */
    @Column(name = "customer_name")
    private String customerName;

    /**
     * 客户手机号
     */
    @Column(name = "customer_mobile")
    private String customerMobile;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 类型：O-医嘱单，L-引导单
     */
    private String type;

    /**
     * 状态：1-未核销（已下载），2-未开单（已核销并绑定患者），3-已开单（已绑定账单），4-已同步（已上传）
     */
    private Byte status;

    /**
     * 订单id
     */
    @Column(name = "bill_id")
    private Integer billId;

    /**
     * 收费记录id
     */
    @Column(name = "bill_pay_id")
    private Integer billPayId;

    /**
     * 执行医嘱费用
     */
    @Column(name = "executed_amount")
    private BigDecimal executedAmount;

    /**
     * 未执行医嘱费用
     */
    @Column(name = "un_executed_amount")
    private BigDecimal unExecutedAmount;

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
     * 获取全程就诊记录id
     *
     * @return qc_treatment_id - 全程就诊记录id
     */
    public Integer getQcTreatmentId() {
        return qcTreatmentId;
    }

    /**
     * 设置全程就诊记录id
     *
     * @param qcTreatmentId 全程就诊记录id
     */
    public void setQcTreatmentId(Integer qcTreatmentId) {
        this.qcTreatmentId = qcTreatmentId;
    }

    /**
     * 获取mall平台就诊流水号
     *
     * @return adm_no - mall平台就诊流水号
     */
    public String getAdmNo() {
        return admNo;
    }

    /**
     * 设置mall平台就诊流水号
     *
     * @param admNo mall平台就诊流水号
     */
    public void setAdmNo(String admNo) {
        this.admNo = admNo;
    }

    /**
     * 获取客户姓名
     *
     * @return customer_name - 客户姓名
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * 设置客户姓名
     *
     * @param customerName 客户姓名
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * 获取客户手机号
     *
     * @return customer_mobile - 客户手机号
     */
    public String getCustomerMobile() {
        return customerMobile;
    }

    /**
     * 设置客户手机号
     *
     * @param customerMobile 客户手机号
     */
    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
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
     * 获取订单id
     *
     * @return bill_id - 订单id
     */
    public Integer getBillId() {
        return billId;
    }

    /**
     * 设置订单id
     *
     * @param billId 订单id
     */
    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    /**
     * 获取收费记录id
     *
     * @return bill_pay_id - 收费记录id
     */
    public Integer getBillPayId() {
        return billPayId;
    }

    /**
     * 设置收费记录id
     *
     * @param billPayId 收费记录id
     */
    public void setBillPayId(Integer billPayId) {
        this.billPayId = billPayId;
    }

    /**
     * 获取执行医嘱费用
     *
     * @return executed_amount - 执行医嘱费用
     */
    public BigDecimal getExecutedAmount() {
        return executedAmount;
    }

    /**
     * 设置执行医嘱费用
     *
     * @param executedAmount 执行医嘱费用
     */
    public void setExecutedAmount(BigDecimal executedAmount) {
        this.executedAmount = executedAmount;
    }

    /**
     * 获取未执行医嘱费用
     *
     * @return un_executed_amount - 未执行医嘱费用
     */
    public BigDecimal getUnExecutedAmount() {
        return unExecutedAmount;
    }

    /**
     * 设置未执行医嘱费用
     *
     * @param unExecutedAmount 未执行医嘱费用
     */
    public void setUnExecutedAmount(BigDecimal unExecutedAmount) {
        this.unExecutedAmount = unExecutedAmount;
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