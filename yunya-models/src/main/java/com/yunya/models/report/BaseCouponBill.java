package com.yunya.models.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "base_coupon_bill")
@Data
public class BaseCouponBill {
    @Id
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 订单记录ID(order_record表ID)
     */
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
     * 状态（0-已下单；1-已收费；2-已退款）
     */
    @Column(name = "order_status")
    private Integer orderStatus;

    /**
     * 订单编号
     */
    @Column(name = "order_num")
    private String orderNum;

    /**
     * 账单编号
     */
    @Column(name = "bill_num")
    private String billNum;

    /**
     * 订单总额
     */
    @Column(name = "order_amount")
    private BigDecimal orderAmount;

    /**
     * 已收总额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 应收金额
     */
    @Column(name = "receivable_amount")
    private BigDecimal receivableAmount;

    /**
     * 欠费总额
     */
    @Column(name = "debt_amount")
    private BigDecimal debtAmount;

    /**
     * 开单日期
     */
    @Column(name = "order_date")
    private Date orderDate;

    /**
     * 开单人ID
     */
    @Column(name = "biller_id")
    private Integer billerId;

    /**
     * 账单日期
     */
    @Column(name = "bill_date")
    private Date billDate;

    /**
     * 结账人ID
     */
    @Column(name = "checker_id")
    private Integer checkerId;
}