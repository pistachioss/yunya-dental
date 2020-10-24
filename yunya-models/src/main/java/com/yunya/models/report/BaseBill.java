package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "base_bill")
public class BaseBill {
    /**
     * 订单记录ID
     */
    @Id
    @Column(name = "bill_id")
    private Integer billId;

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
     * 就诊ID
     */
    @Column(name = "treatment_id")
    private Integer treatmentId;

    /**
     * 账单状态(0-欠费；1-结清)
     */
    @Column(name = "bill_status")
    private Byte billStatus;

    /**
     * 订单编号
     */
    @Column(name = "order_num")
    private String orderNum;

    /**
     * 订单总额
     */
    @Column(name = "order_amount")
    private BigDecimal orderAmount;

    /**
     * 开单日期
     */
    @Column(name = "order_date")
    private Date orderDate;

    /**
     * 助手1
     */
    @Column(name = "assistant_1")
    private Integer assistant1;

    /**
     * 助手2
     */
    @Column(name = "assistant_2")
    private Integer assistant2;

    /**
     * 助手3
     */
    @Column(name = "assistant_3")
    private Integer assistant3;

    /**
     * 优惠类型（0-普通优惠；1-授权折扣）
     */
    @Column(name = "privilege_type")
    private Byte privilegeType;

    /**
     * 优惠总额
     */
    @Column(name = "privilege_amount")
    private BigDecimal privilegeAmount;

    /**
     * 账单编号
     */
    @Column(name = "bill_num")
    private String billNum;

    /**
     * 实收总额（订单总额-优惠总额）
     */
    @Column(name = "actual_amount")
    private BigDecimal actualAmount;

    /**
     * 已收总额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 欠费总额
     */
    @Column(name = "debt_amount")
    private BigDecimal debtAmount;

    /**
     * 获取订单记录ID
     *
     * @return bill_id - 订单记录ID
     */
    public Integer getBillId() {
        return billId;
    }

    /**
     * 设置订单记录ID
     *
     * @param billId 订单记录ID
     */
    public void setBillId(Integer billId) {
        this.billId = billId;
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
     * 获取就诊ID
     *
     * @return treatment_id - 就诊ID
     */
    public Integer getTreatmentId() {
        return treatmentId;
    }

    /**
     * 设置就诊ID
     *
     * @param treatmentId 就诊ID
     */
    public void setTreatmentId(Integer treatmentId) {
        this.treatmentId = treatmentId;
    }

    /**
     * 获取账单状态(0-欠费；1-结清)
     *
     * @return bill_status - 账单状态(0-欠费；1-结清)
     */
    public Byte getBillStatus() {
        return billStatus;
    }

    /**
     * 设置账单状态(0-欠费；1-结清)
     *
     * @param billStatus 账单状态(0-欠费；1-结清)
     */
    public void setBillStatus(Byte billStatus) {
        this.billStatus = billStatus;
    }

    /**
     * 获取订单编号
     *
     * @return order_num - 订单编号
     */
    public String getOrderNum() {
        return orderNum;
    }

    /**
     * 设置订单编号
     *
     * @param orderNum 订单编号
     */
    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
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
     * 获取开单日期
     *
     * @return order_date - 开单日期
     */
    public Date getOrderDate() {
        return orderDate;
    }

    /**
     * 设置开单日期
     *
     * @param orderDate 开单日期
     */
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * 获取助手1
     *
     * @return assistant_1 - 助手1
     */
    public Integer getAssistant1() {
        return assistant1;
    }

    /**
     * 设置助手1
     *
     * @param assistant1 助手1
     */
    public void setAssistant1(Integer assistant1) {
        this.assistant1 = assistant1;
    }

    /**
     * 获取助手2
     *
     * @return assistant_2 - 助手2
     */
    public Integer getAssistant2() {
        return assistant2;
    }

    /**
     * 设置助手2
     *
     * @param assistant2 助手2
     */
    public void setAssistant2(Integer assistant2) {
        this.assistant2 = assistant2;
    }

    /**
     * 获取助手3
     *
     * @return assistant_3 - 助手3
     */
    public Integer getAssistant3() {
        return assistant3;
    }

    /**
     * 设置助手3
     *
     * @param assistant3 助手3
     */
    public void setAssistant3(Integer assistant3) {
        this.assistant3 = assistant3;
    }

    /**
     * 获取优惠类型（0-普通优惠；1-授权折扣）
     *
     * @return privilege_type - 优惠类型（0-普通优惠；1-授权折扣）
     */
    public Byte getPrivilegeType() {
        return privilegeType;
    }

    /**
     * 设置优惠类型（0-普通优惠；1-授权折扣）
     *
     * @param privilegeType 优惠类型（0-普通优惠；1-授权折扣）
     */
    public void setPrivilegeType(Byte privilegeType) {
        this.privilegeType = privilegeType;
    }

    /**
     * 获取优惠总额
     *
     * @return privilege_amount - 优惠总额
     */
    public BigDecimal getPrivilegeAmount() {
        return privilegeAmount;
    }

    /**
     * 获取账单编号
     *
     * @return billNum -账单编号
     */
    public String getBillNum() {
        return billNum;
    }

    /**
     * 设置账单编号
     *
     * @param billNum billNum -账单编号
     */
    public void setBillNum(String billNum) {
        this.billNum = billNum;
    }

    /**
     * 设置优惠总额
     *
     * @param privilegeAmount 优惠总额
     */
    public void setPrivilegeAmount(BigDecimal privilegeAmount) {
        this.privilegeAmount = privilegeAmount;
    }

    /**
     * 获取实收总额（订单总额-优惠总额）
     *
     * @return actual_amount - 实收总额（订单总额-优惠总额）
     */
    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    /**
     * 设置实收总额（订单总额-优惠总额）
     *
     * @param actualAmount 实收总额（订单总额-优惠总额）
     */
    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    /**
     * 获取已收总额
     *
     * @return received_amount - 已收总额
     */
    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * 设置已收总额
     *
     * @param receivedAmount 已收总额
     */
    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    /**
     * 获取欠费总额
     *
     * @return debt_amount - 欠费总额
     */
    public BigDecimal getDebtAmount() {
        return debtAmount;
    }

    /**
     * 设置欠费总额
     *
     * @param debtAmount 欠费总额
     */
    public void setDebtAmount(BigDecimal debtAmount) {
        this.debtAmount = debtAmount;
    }
}