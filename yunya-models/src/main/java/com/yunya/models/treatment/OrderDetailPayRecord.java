package com.yunya.models.treatment;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Table(name = "order_detail_pay_record")
public class OrderDetailPayRecord {
    /**
     * 主键ID
     */
    @Id
    private Integer id;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 就诊记录ID
     */
    @Column(name = "treatment_record_id")
    private Integer treatmentRecordId;

    /**
     * 订单记录id
     */
    @Column(name = "order_record_id")
    private Integer orderRecordId;

    /**
     * 订单明细ID
     */
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    /**
     * 账单记录ID
     */
    @Column(name = "bill_record_id")
    private Integer billRecordId;

    /**
     * 项目应收
     */
    @Column(name = "receivable_amount")
    private BigDecimal receivableAmount;

    /**
     * 优惠金额
     */
    @Column(name = "privilege_amount")
    private BigDecimal privilegeAmount;

    /**
     * 实际应收
     */
    @Column(name = "actual_receivable")
    private BigDecimal actualReceivable;

    /**
     * 已收金额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 项目免单
     */
    @Column(name = "free_amount")
    private BigDecimal freeAmount;

    /**
     * 卡券单个项目补入工作量
     */
    @Column(name = "coupon_workload")
    private BigDecimal couponWorkload;

    /**
     * 划扣卡工作量
     */
    @Column(name = "swipe_workload")
    private BigDecimal swipeWorkload;

    /**
     * 划扣卡补入工作量
     */
    @Column(name = "swipe_coupon_workload")
    private BigDecimal swipeCouponWorkload;

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
     * 创建人ID
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人id
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新人姓名
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取主键ID
     *
     * @return id - 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
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
     * 获取订单明细ID
     *
     * @return order_detail_id - 订单明细ID
     */
    public Integer getOrderDetailId() {
        return orderDetailId;
    }

    /**
     * 设置订单明细ID
     *
     * @param orderDetailId 订单明细ID
     */
    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
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
     * 获取项目应收
     *
     * @return receivable_amount - 项目应收
     */
    public BigDecimal getReceivableAmount() {
        return receivableAmount;
    }

    /**
     * 设置项目应收
     *
     * @param receivableAmount 项目应收
     */
    public void setReceivableAmount(BigDecimal receivableAmount) {
        this.receivableAmount = receivableAmount;
    }

    /**
     * 获取优惠金额
     *
     * @return privilege_amount - 优惠金额
     */
    public BigDecimal getPrivilegeAmount() {
        return privilegeAmount;
    }

    /**
     * 设置优惠金额
     *
     * @param privilegeAmount 优惠金额
     */
    public void setPrivilegeAmount(BigDecimal privilegeAmount) {
        this.privilegeAmount = privilegeAmount;
    }

    /**
     * 获取实际应收
     *
     * @return actual_receivable - 实际应收
     */
    public BigDecimal getActualReceivable() {
        return actualReceivable;
    }

    /**
     * 设置实际应收
     *
     * @param actualReceivable 实际应收
     */
    public void setActualReceivable(BigDecimal actualReceivable) {
        this.actualReceivable = actualReceivable;
    }

    /**
     * 获取已收金额
     *
     * @return received_amount - 已收金额
     */
    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * 设置已收金额
     *
     * @param receivedAmount 已收金额
     */
    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    /**
     * 获取免单金额
     *
     * @return 免单金额
     */
    public BigDecimal getFreeAmount() {
        return freeAmount;
    }

    /**
     * 设置免单金额
     *
     * @param freeAmount 免单金额
     */
    public void setFreeAmount(BigDecimal freeAmount) {
        this.freeAmount = freeAmount;
    }

    /**
     * 获取卡券单个项目补入工作量
     *
     * @return couponWorkload-单个项目补入工作量
     */
    public BigDecimal getCouponWorkload() {
        return couponWorkload;
    }

    /**
     * 设置卡券单个项目补入工作量
     *
     * @param couponWorkload 单个项目补入工作量
     */
    public void setCouponWorkload(BigDecimal couponWorkload) {
        this.couponWorkload = couponWorkload;
    }

    /**
     * 获取划扣卡工作量
     *
     * @return
     */
    public BigDecimal getSwipeWorkload() {
        return swipeWorkload;
    }

    /**
     * 设置划扣卡工作量
     *
     * @param swipeWorkload
     */
    public void setSwipeWorkload(BigDecimal swipeWorkload) {
        this.swipeWorkload = swipeWorkload;
    }

    /**
     * 获取划扣卡补入工作量
     *
     * @return
     */
    public BigDecimal getSwipeCouponWorkload() {
        return swipeCouponWorkload;
    }

    /**
     * 设置划扣卡补入工作量
     *
     * @param swipeCouponWorkload
     */
    public void setSwipeCouponWorkload(BigDecimal swipeCouponWorkload) {
        this.swipeCouponWorkload = swipeCouponWorkload;
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
     * 获取创建人ID
     *
     * @return crt_name - 创建人ID
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人ID
     *
     * @param crtName 创建人ID
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
     * 获取修改时间
     *
     * @return upd_time - 修改时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置修改时间
     *
     * @param updTime 修改时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}