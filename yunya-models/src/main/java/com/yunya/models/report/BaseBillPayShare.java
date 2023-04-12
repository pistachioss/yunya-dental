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
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 更新时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

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
     * 获取创建人id
     *
     * @return crt_id - 创建人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人id
     *
     * @param crtId 创建人id
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
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
     * @return upt_id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * @param uptId
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取更新时间
     *
     * @return upt_time - 更新时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置更新时间
     *
     * @param uptTime 更新时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }
}