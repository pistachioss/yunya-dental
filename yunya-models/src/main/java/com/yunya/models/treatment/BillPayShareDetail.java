package com.yunya.models.treatment;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "bill_pay_share_detail")
public class BillPayShareDetail {
    /**
     * 分摊记录id
     */
    @Id
    private Integer id;

    /**
     * 订单id
     */
    @Column(name = "order_record_id")
    private Integer orderRecordId;

    /**
     * 账单收费记录id
     */
    @Column(name = "bill_pay_id")
    private Integer billPayId;

    /**
     * 订单明细id
     */
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    /**
     * 项目类型：0-价目，1-商品
     */
    @Column(name = "item_type")
    private Byte itemType;

    /**
     * 项目id
     */
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 项目免单金额（分摊）
     */
    @Column(name = "free_amount")
    private BigDecimal freeAmount;

    /**
     * 项目实收金额（分摊）
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 项目划扣卡核销工作量
     */
    @Column(name = "swipe_wokload")
    private BigDecimal swipeWorkload;

    /**
     * 执行人id
     */
    @Column(name = "executor_id")
    private Integer executorId;

    /**
     * 咨询师id
     */
    @Column(name = "consulter_id")
    private Integer consulterId;

    /**
     * 收费时间
     */
    @Column(name = "pay_date")
    private Date payDate;

    /**
     * 获取分摊记录id
     *
     * @return id - 分摊记录id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置分摊记录id
     *
     * @param id 分摊记录id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取订单id
     *
     * @return order_record_id - 订单id
     */
    public Integer getOrderRecordId() {
        return orderRecordId;
    }

    /**
     * 设置订单id
     *
     * @param orderRecordId 订单id
     */
    public void setOrderRecordId(Integer orderRecordId) {
        this.orderRecordId = orderRecordId;
    }

    /**
     * 获取账单收费记录id
     *
     * @return bill_pay_id - 账单收费记录id
     */
    public Integer getBillPayId() {
        return billPayId;
    }

    /**
     * 设置账单收费记录id
     *
     * @param billPayId 账单收费记录id
     */
    public void setBillPayId(Integer billPayId) {
        this.billPayId = billPayId;
    }

    /**
     * 获取订单明细id
     *
     * @return 订单明细id
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
     * 获取项目类型：0-价目，1-商品
     *
     * @return item_type - 项目类型：0-价目，1-商品
     */
    public Byte getItemType() {
        return itemType;
    }

    /**
     * 设置项目类型：0-价目，1-商品
     *
     * @param itemType 项目类型：0-价目，1-商品
     */
    public void setItemType(Byte itemType) {
        this.itemType = itemType;
    }

    /**
     * 获取项目id
     *
     * @return item_id - 项目id
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 设置项目id
     *
     * @param itemId 项目id
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 获取项目免单金额（分摊）
     *
     * @return free_amount - 项目免单金额（分摊）
     */
    public BigDecimal getFreeAmount() {
        return freeAmount;
    }

    /**
     * 设置项目免单金额（分摊）
     *
     * @param freeAmount 项目免单金额（分摊）
     */
    public void setFreeAmount(BigDecimal freeAmount) {
        this.freeAmount = freeAmount;
    }

    /**
     * 获取项目实收金额（分摊）
     *
     * @return received_amount - 项目实收金额（分摊）
     */
    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * 设置项目实收金额（分摊）
     *
     * @param receivedAmount 项目实收金额（分摊）
     */
    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    /**
     * 获取划扣卡核销工作量
     *
     * @return
     */
    public BigDecimal getSwipeWorkload() {
        return swipeWorkload;
    }

    /**
     * 设置划扣卡核销工作量
     *
     * @param swipeWorkload
     */
    public void setSwipeWorkload(BigDecimal swipeWorkload) {
        this.swipeWorkload = swipeWorkload;
    }

    /**
     * 获取执行人id
     *
     * @return crt_id - 执行人id
     */
    public Integer getExecutorId() {
        return executorId;
    }

    /**
     * 设置执行人id
     *
     * @param executorId 执行人id
     */
    public void setExecutorId(Integer executorId) {
        this.executorId = executorId;
    }

    /**
     * 获取咨询师id
     *
     * @return
     */
    public Integer getConsulterId() {
        return consulterId;
    }

    /**
     * 设置咨询师id
     *
     * @param consulterId
     */
    public void setConsulterId(Integer consulterId) {
        this.consulterId = consulterId;
    }

    /**
     * 获取收费时间
     *
     * @return crt_time - 收费时间
     */
    public Date getPayDate() {
        return payDate;
    }

    /**
     * 设置收费时间
     *
     * @param payDate 收费时间
     */
    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }
}