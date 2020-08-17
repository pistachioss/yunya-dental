package com.yunya.feign.patient_central.domain.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 会员充值入账方式
 *
 * @author: WY
 * @date 2020/8/15 16:51
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class AccountedWayModel implements Serializable {

    /**
     * 入账方式ID
     */
    private Integer paymentId;

    /**
     * 入账金额
     */
    private BigDecimal creditAmount;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;

}
