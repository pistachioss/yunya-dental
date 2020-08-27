package com.yunya.feign.patient_central.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简单介绍:</br> 充值记录
 *
 * @author: WY
 * @date 2020/8/22 15:14
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PrepaidRechargeRecordVo implements Serializable {
    /**
     * 会员充值记录id
     */
    private Integer id;

    /**
     * 操作时间
     */
    private Date operatingTime;

    /**
     * 充值本金
     */
    private BigDecimal rechargePrincipal;

    /**
     * 充值赠金
     */
    private BigDecimal rechargeBonus;

    /**
     * 入账方式
     */
    private Integer paymentId;

    /**
     * 入账方式
     */
    private String paymentName;

    /**
     * 门诊id
     */
    private Integer orgId;

    /**
     * 诊所简称
     */
    private String orgName;

    /**
     * 操作人id
     */
    private Integer operatorId;

    /**
     * 操作人员
     */
    private String operatorName;

    /**
     * 类型
     */
    private Integer rechargeType;

    /**
     * 充值卡号
     */
    private String rechargeCardNumber;

    /**
     * 备注
     */
    private String remarks;

}
