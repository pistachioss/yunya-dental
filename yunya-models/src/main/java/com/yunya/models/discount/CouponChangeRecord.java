package com.yunya.models.discount;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Table(name = "coupon_change_record")
public class CouponChangeRecord {
    @Id
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;


    /**
     * 患者ID(产生)
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 礼包账单ID
     */
    @Column(name = "coupon_bill_id")
    private Integer couponBillId;

    @Column(name = "card_id")
    private Integer cardId;

    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 发生类型(1.购买 2.消费 3.退款)
     */
    @Column(name = "occur_type")
    private Integer occurType;

    /**
     * 发生金额
     */
    @Column(name = "occur_amount")
    private BigDecimal occurAmount;

    /**
     * 当前金额
     */
    @Column(name = "current_amount")
    private BigDecimal currentAmount;


    /**
     * 操作人
     */
    @Column(name = "operator_user_id")
    private Integer operatorUserId;

    /**
     * 备注/原因
     */
    private String remarks;

    /**
     * 卡号
     */
    @Column(name = "card_number")
    private String cardNumber;

    /**
     * 发生日期
     */
    @Column(name = "occur_date")
    private Date occurDate;

    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;
}