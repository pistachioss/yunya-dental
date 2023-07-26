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

@ApiModel("预付款账户参数模型")
@Data
@ToString
public class CardPrepaymentModel implements Serializable {
    /**
     * 预付款账户ID
     */
    @ApiModelProperty(value = "预付款卡号", required = true)
    @NotBlank(message = "预付款卡号不能为空！")
    private String prepaymentNum;

    @ApiModelProperty(value = "预付款入账方式ID", required = true)
    @NotNull(message = "预付款入账方式ID不能为空！")
    private Integer accountItemId;

    /**
     * 支付金额
     */
    @ApiModelProperty(value = "支付金额", required = true)
    @NotNull(message = "支付金额不能为空！")
    @Min(value = 0, message = "输入金额不能小于0！")
    private BigDecimal amount;

    /**
     * 本金金额
     */
    @ApiModelProperty(value = "本金金额", required = true)
    @NotNull(message = "本金金额不能为空！")
    private BigDecimal principalAmount;

    /**
     * 赠金金额
     */
    @ApiModelProperty(value = "赠金金额")
    private BigDecimal bonusAmount;
}
