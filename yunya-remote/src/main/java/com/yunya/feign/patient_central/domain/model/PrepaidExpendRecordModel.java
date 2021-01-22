package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
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
    @NotNull(message = "患者id (就诊患者id)不能为空")
    @ApiModelProperty(value = "患者id (就诊患者id)",required = true)
    private Integer patientId;

    /**
     * 预付款账号
     */
    @NotNull(message = "预付款账号不能为空")
    @ApiModelProperty(value = "预付款账号",required = true)
    private String prepaidId;

    /**
     * 消费总额
     */
    @ApiModelProperty(value = "消费总额",required = false)
    private BigDecimal expendTotal;

    /**
     * 就诊id
     */
    @NotNull(message = "就诊id不能为空")
    @ApiModelProperty(value = "就诊id",required = true)
    private Integer treatmentRecordId;

    /**
     * 账单id
     */
    @NotNull(message = "账单id不能为空")
    @ApiModelProperty(value = "账单id",required = true)
    private Integer billRecordId;

    /**
     * 账单付款记录id
     */
    @NotNull(message = "账单付款记录id不能为空")
    @ApiModelProperty(value = "账单付款记录id",required = true)
    private Integer billPayRecordId;

    /**
     * 订单记录id
     */
    @NotNull(message = "订单记录id不能为空")
    @ApiModelProperty(value = "订单记录id",required = true)
    private Integer orderRecordId;


    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;


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
