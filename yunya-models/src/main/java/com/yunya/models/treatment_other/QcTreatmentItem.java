package com.yunya.models.treatment_other;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "qc_treatment_item")
public class QcTreatmentItem {
    @Id
    private Integer id;

    /**
     * 全程就诊记录id
     */
    @Column(name = "qc_treatment_id")
    private Integer qcTreatmentId;

    /**
     * 全程医疗-平台医嘱流水号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 订单明细id
     */
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    /**
     * 医嘱项描述
     */
    @Column(name = "item_desc")
    private String itemDesc;

    /**
     * 项目编码（医嘱项代码）
     */
    @Column(name = "item_num")
    private String itemNum;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 数量单位描述
     */
    @Column(name = "unit_desc")
    private String unitDesc;

    /**
     * 医嘱项状态：1-核实，2-作废，4-停止，6-执行，12-撤销
     */
    private Byte status;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 执行人id
     */
    @Column(name = "executor_id")
    private Integer executorId;

    /**
     * 是否全程代收
     */
    @Column(name = "qc_collected")
    private Boolean qcCollected;

    /**
     * 实收金额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 是否有效
     */
    private Boolean inservice;

    /**
     * 开立医嘱时间
     */
    @Column(name = "open_time")
    private Date openTime;

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
     * 获取全程就诊记录id
     *
     * @return
     */
    public Integer getQcTreatmentId() {
        return qcTreatmentId;
    }

    /**
     * 设置 全程就诊记录id
     *
     * @param qcTreatmentId
     */
    public void setQcTreatmentId(Integer qcTreatmentId) {
        this.qcTreatmentId = qcTreatmentId;
    }

    /**
     * 获取全程医疗-平台医嘱流水号
     *
     * @return order_no - 全程医疗-平台医嘱流水号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置全程医疗-平台医嘱流水号
     *
     * @param orderNo 全程医疗-平台医嘱流水号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取订单明细id
     *
     * @return order_detail_id - 订单明细id
     */
    public Integer getOrderDetailId() {
        return orderDetailId;
    }

    /**
     * 设置订单明细id
     *
     * @param orderDetailId 订单明细id
     */
    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    /**
     * 获取医嘱项描述
     *
     * @return item_desc - 医嘱项描述
     */
    public String getItemDesc() {
        return itemDesc;
    }

    /**
     * 设置医嘱项描述
     *
     * @param itemDesc 医嘱项描述
     */
    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    /**
     * 获取项目编码（医嘱项代码）
     *
     * @return item_num - 项目编码（医嘱项代码）
     */
    public String getItemNum() {
        return itemNum;
    }

    /**
     * 设置项目编码（医嘱项代码）
     *
     * @param itemNum 项目编码（医嘱项代码）
     */
    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
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
     * 获取数量单位描述
     *
     * @return unit_desc - 数量单位描述
     */
    public String getUnitDesc() {
        return unitDesc;
    }

    /**
     * 设置数量单位描述
     *
     * @param unitDesc 数量单位描述
     */
    public void setUnitDesc(String unitDesc) {
        this.unitDesc = unitDesc;
    }

    /**
     * 获取医嘱项状态：1-核实，2-作废，4-停止，6-执行，12-撤销
     *
     * @return status - 医嘱项状态：1-核实，2-作废，4-停止，6-执行，12-撤销
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置医嘱项状态：1-核实，2-作废，4-停止，6-执行，12-撤销
     *
     * @param status 医嘱项状态：1-核实，2-作废，4-停止，6-执行，12-撤销
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取单价
     *
     * @return price - 单价
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置单价
     *
     * @param price 单价
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取是否全程代收
     *
     * @return qc_collected - 是否全程代收
     */
    public Boolean getQcCollected() {
        return qcCollected;
    }

    /**
     * 设置是否全程代收
     *
     * @param qcCollected 是否全程代收
     */
    public void setQcCollected(Boolean qcCollected) {
        this.qcCollected = qcCollected;
    }

    /**
     * 获取实收金额
     *
     * @return received_amount - 实收金额
     */
    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * 设置实收金额
     *
     * @param receivedAmount 实收金额
     */
    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    /**
     * 获取执行人id
     *
     * @return
     */
    public Integer getExecutorId() {
        return executorId;
    }

    /**
     * 设置执行人id
     *
     * @param executorId
     */
    public void setExecutorId(Integer executorId) {
        this.executorId = executorId;
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
     * 获取开立医嘱时间
     *
     * @return open_time - 开立医嘱时间
     */
    public Date getOpenTime() {
        return openTime;
    }

    /**
     * 设置开立医嘱时间
     *
     * @param openTime 开立医嘱时间
     */
    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    /**
     * 获取更新人id
     *
     * @return upt_id - 更新人id
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
     * @return upt_time - 更新时间
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