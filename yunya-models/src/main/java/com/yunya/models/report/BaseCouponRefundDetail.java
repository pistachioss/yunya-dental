package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_coupon_refund_detail")
public class BaseCouponRefundDetail {
    /**
     * 退费明细记录ID
     */
    @Id
    @Column(name = "refund_detail_id")
    private Integer refundDetailId;

    /**
     * 退费记录ID
     */
    @Column(name = "refund_id")
    private Integer refundId;

    /**
     * 订单明细ID
     */
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    /**
     * 卡券id
     */
    @Column(name = "card_id")
    private Integer cardId;

    /**
     * 退费金额
     */
    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    /**
     * 执行人id
     */
    @Column(name = "executor_id")
    private Integer executorId;

    /**
     * 咨询死ID
     */
    @Column(name = "consulter_id")
    private Integer consulterId;

    /**
     * 项目类型（0-价目表；1-商品）
     */
    @Column(name = "item_type")
    private Byte itemType;

    /**
     * 项目ID
     */
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 开单项目名称
     */
    @Column(name = "item_name")
    private String itemName;

    /**
     * 获取退费明细记录ID
     *
     * @return refund_detail_id - 退费明细记录ID
     */
    public Integer getRefundDetailId() {
        return refundDetailId;
    }

    /**
     * 设置退费明细记录ID
     *
     * @param refundDetailId 退费明细记录ID
     */
    public void setRefundDetailId(Integer refundDetailId) {
        this.refundDetailId = refundDetailId;
    }

    /**
     * 获取退费记录ID
     *
     * @return refund_id - 退费记录ID
     */
    public Integer getRefundId() {
        return refundId;
    }

    /**
     * 设置退费记录ID
     *
     * @param refundId 退费记录ID
     */
    public void setRefundId(Integer refundId) {
        this.refundId = refundId;
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
     * 获取卡券id
     *
     * @return card_id - 卡券id
     */
    public Integer getCardId() {
        return cardId;
    }

    /**
     * 设置卡券id
     *
     * @param cardId 卡券id
     */
    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    /**
     * 获取退费金额
     *
     * @return refund_amount - 退费金额
     */
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    /**
     * 设置退费金额
     *
     * @param refundAmount 退费金额
     */
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
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
     * 获取咨询死ID
     *
     * @return consulter_id - 咨询死ID
     */
    public Integer getConsulterId() {
        return consulterId;
    }

    /**
     * 设置咨询死ID
     *
     * @param consulterId 咨询死ID
     */
    public void setConsulterId(Integer consulterId) {
        this.consulterId = consulterId;
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
     * 获取开单项目名称
     *
     * @return item_name - 开单项目名称
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * 设置开单项目名称
     *
     * @param itemName 开单项目名称
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}