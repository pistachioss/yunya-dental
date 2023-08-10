package com.yunya.models.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "base_coupon_bill_pay")
@Data
public class BaseCouponBillPay {
    /**
     * 账单收费记录ID
     */
    @Id
    @Column(name = "bill_pay_id")
    private Integer billPayId;

    /**
     * 订单ID
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 收款人ID
     */
    @Column(name = "payee_user_id")
    private Integer payeeUserId;

    /**
     * 收款日期
     */
    @Column(name = "payee_date")
    private Date payeeDate;

    /**
     * 本次收费总额
     */
    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    /**
     * 欠费总额
     */
    @Column(name = "owe_amount")
    private BigDecimal oweAmount;

}