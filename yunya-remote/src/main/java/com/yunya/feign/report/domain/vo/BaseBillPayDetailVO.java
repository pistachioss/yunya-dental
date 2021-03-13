package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：账单收费详情
 *
 * @author: chenlin
 * @Description: 账单收费详情
 * @Date: 2021/3/11 14:00
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单收费详情")
public class BaseBillPayDetailVO implements Serializable {
    /**
     * 账单支付明细记录ID
     */
    @ApiModelProperty("账单支付明细记录ID")
    private Integer billPayDetailRecordId;

    /**
     * 账单ID
     */
    @ApiModelProperty("账单ID")
    private Integer billId;

    /**
     * 账单收费记录ID
     */
    @ApiModelProperty("账单收费记录ID")
    private Integer billPayId;

    /**
     * 入账方式类型（0-预付款；1-会员卡；2-其他支付方式）
     */
    @ApiModelProperty("入账方式类型（0-预付款；1-会员卡；2-其他支付方式）")
    private Byte type;

    /**
     * 入账方式明细ID
     */
    @ApiModelProperty("入账方式明细ID")
    private Integer accountItemId;

    /**
     * 消费本金
     */
    @ApiModelProperty("消费本金")
    private BigDecimal principalAmount;

    /**
     * 消费赠金
     */
    @ApiModelProperty("消费赠金")
    private BigDecimal bonusAmount;

    /**
     * 卡号
     */
    @ApiModelProperty("卡号")
    private String cardNum;

    /**
     * 收费门诊id
     */
    @ApiModelProperty("收费门诊id")
    private Integer orgId;
}
