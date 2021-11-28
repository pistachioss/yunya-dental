package com.yunya.models.treatment;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "order_detail")
public class OrderDetail {
    /**
     * 主键ID
     */
    @Id
    private Integer id;

    /**
     * 组织（门诊）ID
     */
    @Column(name = "org_id")
    private Integer orgId;

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
     * 开单类型（0-价目表；1-商品；）
     */
    private Byte type;

    /**
     * 开单项目ID
     */
    @Column(name = "billing_item_id")
    private Integer billingItemId;
    /**
     * 开单项目ID
     */
    @Column(name = "billing_item_name")
    private String billingItemName;

    /**
     * 门诊价目表单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 应收原价合计
     */
    @Column(name = "receivable_amount")
    private BigDecimal receivableAmount;

    /**
     * 牙位
     */
    @Column(name = "tooth_bit")
    private String toothBit;

    /**
     * 执行人ID
     */
    @Column(name = "executor_id")
    private Integer executorId;

    /**
     * 添加来源（0-开单；1-收费）
     */
    @Column(name = "source_type")
    private Byte sourceType;

    /**
     * 备注
     */
    private String remarks;

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
    @Column(name = "upt_name")
    private String uptName;

    /**
     * 更新时间
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
     * 获取组织（门诊）ID
     *
     * @return org_id - 组织（门诊）ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织（门诊）ID
     *
     * @param orgId 组织（门诊）ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
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
     * 获取开单类型（0-价目表；1-商品；）
     *
     * @return type - 开单类型（0-价目表；1-商品；）
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置开单类型（0-价目表；1-商品；）
     *
     * @param type 开单类型（0-价目表；1-商品；）
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取开单项目ID
     *
     * @return billing_item_id - 开单项目ID
     */
    public Integer getBillingItemId() {
        return billingItemId;
    }

    /**
     * 设置开单项目ID
     *
     * @param billingItemId 开单项目ID
     */
    public void setBillingItemId(Integer billingItemId) {
        this.billingItemId = billingItemId;
    }

    /**
     * 获取门诊价目表单价
     *
     * @return price - 门诊价目表单价
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置门诊价目表单价
     *
     * @param price 门诊价目表单价
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取数量
     *
     * @return quantity - 数量
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 设置数量
     *
     * @param quantity 数量
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 获取应收原价合计
     *
     * @return receivable_amount - 应收原价合计
     */
    public BigDecimal getReceivableAmount() {
        return receivableAmount;
    }

    /**
     * 设置应收原价合计
     *
     * @param receivableAmount 应收原价合计
     */
    public void setReceivableAmount(BigDecimal receivableAmount) {
        this.receivableAmount = receivableAmount;
    }

    /**
     * 获取牙位
     *
     * @return tooth_bit - 牙位
     */
    public String getToothBit() {
        return toothBit;
    }

    /**
     * 设置牙位
     *
     * @param toothBit 牙位
     */
    public void setToothBit(String toothBit) {
        this.toothBit = toothBit;
    }

    /**
     * 获取执行人ID
     *
     * @return executor_id - 执行人ID
     */
    public Integer getExecutorId() {
        return executorId;
    }

    /**
     * 设置执行人ID
     *
     * @param executorId 执行人ID
     */
    public void setExecutorId(Integer executorId) {
        this.executorId = executorId;
    }

    /**
     * 获取添加来源（0-开单；1-收费）
     *
     * @return sourceType - 添加来源（0-开单；1-收费）
     */
    public Byte getSourceType() {
        return sourceType;
    }

    /**
     * 设置添加来源
     *
     * @param sourceType 添加来源（0-开单；1-收费）
     */
    public void setSourceType(Byte sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * 获取备注
     *
     * @return remarks - 备注
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注
     *
     * @param remarks 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
     * @return upt_name - 更新人姓名
     */
    public String getUptName() {
        return uptName;
    }

    /**
     * 设置更新人姓名
     *
     * @param uptName 更新人姓名
     */
    public void setUptName(String uptName) {
        this.uptName = uptName;
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


    public String getBillingItemName() {
        return billingItemName;
    }

    public void setBillingItemName(String billingItemName) {
        this.billingItemName = billingItemName;
    }
}