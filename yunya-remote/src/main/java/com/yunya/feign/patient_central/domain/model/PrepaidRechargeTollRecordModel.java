package com.yunya.feign.patient_central.domain.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 预付款充值收费记录
 *
 * @author: WY
 * @date 2020/8/22 14:54
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PrepaidRechargeTollRecordModel implements Serializable {

    /**
     * 充值记录ID
     */
    @Column(name = "recharge_record_id")
    private Integer rechargeRecordId;

    /**
     * 入账方式ID
     */
    @Column(name = "payment_id")
    private Integer paymentId;

    /**
     * 入账金额
     */
    @Column(name = "credit_amount")
    private BigDecimal creditAmount;

    /**
     * 备注 备注
     */
    private String remarks;
}
