package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 预付款消费Model
 *
 * @author: WY
 * @date 2020/8/29 13:36
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class PrepaidExpendRecordModel implements Serializable {

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id (就诊患者id)",required = true)
    private Integer patientId;

    /**
     * 预付款卡号
     */
    @ApiModelProperty(value = "预付款卡号",required = true)
    private String prepaidId;

    /**
     * 消费总额
     */
    @ApiModelProperty(value = "消费总额",required = true)
    private BigDecimal expendTotal;

    /**
     * 就诊id
     */
    @ApiModelProperty(value = "就诊id",required = true)
    private Integer treatmentRecordId;

    /**
     * 账单id
     */
    @ApiModelProperty(value = "账单id",required = true)
    private Integer billRecordId;

    /**
     * 账单付款记录id
     */
    @ApiModelProperty(value = "账单付款记录id",required = true)
    private Integer billPayRecordId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 类型（0：撤销消费 1：消费 ）
     */
    @ApiModelProperty(value = "类型（0：撤销消费 1：消费 ）",required = true)
    private Integer type;

    /**
     * 消费本金
     */
    @ApiModelProperty(value = "消费本金",required = false)
    private BigDecimal principalAmount;

    /**
     * 消费赠金
     */
    @ApiModelProperty(value = "消费赠金",required = false)
    private BigDecimal bonusAmount;

}
