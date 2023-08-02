package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 预付款退费参数模型
 *
 * @author: chow
 * @date: 2020/9/22 20:52
 * @description:
 * @since: 1.0.0
 */
@ApiModel("预付款退费参数模型")
@Data
@ToString
public class CardPrepaymentRefundModel implements Serializable {
    /**
     * 预付款账户ID
     */
    @ApiModelProperty(value = "预付款卡号", required = true)
    @NotBlank(message = "预付款卡号不能为空！")
    private String prepaymentAccountId;

    @ApiModelProperty(value = "预付款入账方式ID", required = true)
    @NotNull(message = "预付款入账方式ID不能为空！")
    private Integer accountItemId;

    @ApiModelProperty(value = "预付款退费本金金额", required = true)
    @NotNull(message = "预付款退费本金不能为空！")
    @Min(value = 0, message = "输入金额不能小于0！")
    private BigDecimal principalAmount;

    @ApiModelProperty(value = "预付款退费赠金金额", required = true)
    @NotNull(message = "预付款退费赠金不能为空！")
    @Min(value = 0, message = "输入金额不能小于0！")
    private BigDecimal giftAmount;
}
