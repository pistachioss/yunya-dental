package com.yunya.models.middletable;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_bill_detail")
public class BaseBillDetail {
    /**
     * 账单明细ID
     */
    @Column(name = "bill_detail_id")
    private Integer billDetailId;

    /**
     * 账单ID
     */
    @Column(name = "bill_id")
    private Integer billId;

    /**
     * 折扣金额(多种优惠方式)
     */
    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    /**
     * 卡券单个工作量
     */
    @Column(name = "coupon_workload")
    private BigDecimal couponWorkload;

    /**
     * 执行人ID
     */
    @Column(name = "executor_id")
    private Integer executorId;

    /**
     * 项目ID
     */
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 项目类型（0-价目表；1-商品）
     */
    @Column(name = "item_type")
    private Byte itemType;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 项目收费金额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 获取账单明细ID
     *
     * @return bill_detail_id - 账单明细ID
     */
    public Integer getBillDetailId() {
        return billDetailId;
    }

    /**
     * 设置账单明细ID
     *
     * @param billDetailId 账单明细ID
     */
    public void setBillDetailId(Integer billDetailId) {
        this.billDetailId = billDetailId;
    }

    /**
     * 获取账单ID
     *
     * @return bill_id - 账单ID
     */
    public Integer getBillId() {
        return billId;
    }

    /**
     * 设置账单ID
     *
     * @param billId 账单ID
     */
    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    /**
     * 获取折扣金额(多种优惠方式)
     *
     * @return discount_amount - 折扣金额(多种优惠方式)
     */
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    /**
     * 设置折扣金额(多种优惠方式)
     *
     * @param discountAmount 折扣金额(多种优惠方式)
     */
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    /**
     * 获取卡券单个工作量
     *
     * @return coupon_workload - 卡券单个工作量
     */
    public BigDecimal getCouponWorkload() {
        return couponWorkload;
    }

    /**
     * 设置卡券单个工作量
     *
     * @param couponWorkload 卡券单个工作量
     */
    public void setCouponWorkload(BigDecimal couponWorkload) {
        this.couponWorkload = couponWorkload;
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
     * 获取项目ID
     *
     * @return item_id - 项目ID
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 设置项目ID
     *
     * @param itemId 项目ID
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 获取项目类型（0-价目表；1-商品）
     *
     * @return item_type - 项目类型（0-价目表；1-商品）
     */
    public Byte getItemType() {
        return itemType;
    }

    /**
     * 设置项目类型（0-价目表；1-商品）
     *
     * @param itemType 项目类型（0-价目表；1-商品）
     */
    public void setItemType(Byte itemType) {
        this.itemType = itemType;
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
     * 获取项目收费金额
     *
     * @return received_amount - 项目收费金额
     */
    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * 设置项目收费金额
     *
     * @param receivedAmount 项目收费金额
     */
    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }
}