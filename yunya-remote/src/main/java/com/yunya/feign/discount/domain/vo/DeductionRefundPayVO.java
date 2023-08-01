package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2023/07/31
 */
@Data
@ApiModel(value = "划扣退费方式模型")
public class DeductionRefundPayVO implements Serializable {
    @ApiModelProperty("卡号（会员或预付款）")
    private String number;

    @ApiModelProperty(value = "预付款入账方式ID")
    private Integer accountItemId;

    /**
     * 支付金额
     */
    @ApiModelProperty(value = "支付金额")
    private BigDecimal amount;

    /**
     * 本金金额
     */
    @ApiModelProperty(value = "本金金额")
    private BigDecimal principalAmount;

    /**
     * 赠金金额
     */
    @ApiModelProperty(value = "赠金金额")
    private BigDecimal bonusAmount;
}
