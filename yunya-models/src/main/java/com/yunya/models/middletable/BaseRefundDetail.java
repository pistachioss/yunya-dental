package com.yunya.models.middletable;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_refund_detail")
public class BaseRefundDetail {
    /**
     * 退费明细ID
     */
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
    @Column(name = "bill_detail_id")
    private Integer billDetailId;

    /**
     * 退费金额
     */
    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    /**
     * 获取退费明细ID
     *
     * @return refund_detail_id - 退费明细ID
     */
    public Integer getRefundDetailId() {
        return refundDetailId;
    }

    /**
     * 设置退费明细ID
     *
     * @param refundDetailId 退费明细ID
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
     * @return bill_detail_id - 订单明细ID
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
}