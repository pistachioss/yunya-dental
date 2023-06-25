package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "base_bill_pay_share")
public class BaseBillPayShare {

    /**
     * 订单id
     */
    @Column(name = "bill_id")
    private Integer billId;

    /**
     * 账单收费记录id
     */
    @Column(name = "bill_pay_id")
    private Integer billPayId;

    /**
     * 订单明细ID
     */
    @Column(name = "bill_detail_id")
    private Integer billDetailId;

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
    @Column(name = "swipe_workload")
    private BigDecimal swipeWorkload;

    /**
     * 执行人id
     */
    @Column(name = "executor_id")
    private Integer executorId;

    /**
     * 收费日期
     */
    @Column(name = "pay_date")
    private Date payDate;

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
     * 获取订单明细ID
     *
     * @return order_detail_id - 订单明细ID
     */
    public Integer getBillDetailId() {
        return billDetailId;
    }

    /**
     * 设置订单明细ID
     *
     * @param billDetailId 订单明细ID
     */
    public void setBillDetailId(Integer billDetailId) {
        this.billDetailId = billDetailId;
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
     * 获取项目划扣卡核销工作量
     *
     * @return
     */
    public BigDecimal getSwipeWorkload() {
        return swipeWorkload;
    }

    /**
     * 设置项目划扣卡核销工作量
     *
     * @param swipeWorkload
     */
    public void setSwipeWorkload(BigDecimal swipeWorkload) {
        this.swipeWorkload = swipeWorkload;
    }

    /**
     * 获取执行人id
     *
     * @return executor_id - 执行人id
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
     * 获取收费日期
     *
     * @return pay_date - 收费日期
     */
    public Date getPayDate() {
        return payDate;
    }

    /**
     * 设置收费日期
     *
     * @param payDate 收费日期
     */
    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }
}