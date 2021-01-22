package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 预付款账单退费Model
 *
 * @author: WY
 * @date 2020/8/15 15:41
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("预付款账单退费Model")
public class PrepaidBillRechargeModel implements Serializable {


    /**
     * 预付款账号
     */
    @ApiModelProperty(value = "预付款账号",required = true)
    private String prepaidId;

    /**
     * 充值本金
     */
    @ApiModelProperty(value = "充值本金",required = false)
    private BigDecimal rechargePrincipal;

    /**
     * 充值赠金
     */
    @ApiModelProperty(value = "充值赠金",required = false)
    private BigDecimal rechargeBonus;

    /**
     * 订单id
     */
    @ApiModelProperty(value = "订单id",required = false)
    private Integer orderRecordId;

    /**
     * 账单退款记录id
     */
    @ApiModelProperty(value = "账单付款记录id",required = false)
    private Integer billPayRecordId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注",required = false)
    private String remarks;

}
