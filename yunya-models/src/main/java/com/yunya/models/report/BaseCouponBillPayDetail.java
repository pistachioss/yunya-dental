package com.yunya.models.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "base_coupon_bill_pay_detail")
@Data
public class BaseCouponBillPayDetail {
    /**
     * 账单支付明细记录ID
     */
    @Id
    @Column(name = "bill_pay_detail_id")
    private Integer billPayDetailId;

    /**
     * 订单id
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 账单收费记录ID
     */
    @Column(name = "bill_pay_id")
    private Integer billPayId;

    /**
     * 入账方式明细ID
     */
    @Column(name = "account_item_id")
    private Integer accountItemId;

    /**
     * 入账方式明细
     */
    @Column(name = "account_item_name")
    private String accountItemName;

    /**
     * 入账方式类型（0-预付款；1-会员卡；2-其他支付方式）
     */
    private Integer type;

    /**
     * 入账金额
     */
    private BigDecimal amount;

    /**
     * 消费本金
     */
    @Column(name = "principal_amount")
    private BigDecimal principalAmount;

    /**
     * 消费赠金
     */
    @Column(name = "bonus_amount")
    private BigDecimal bonusAmount;

    /**
     * 会员卡号或预付款卡号
     */
    @Column(name = "patient_num")
    private String patientNum;
}