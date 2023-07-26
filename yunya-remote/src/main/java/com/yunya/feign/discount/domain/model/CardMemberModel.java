package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ApiModel("会员卡账户参数模型")
@Data
@ToString
public class CardMemberModel {

    /**
     * 会员卡账户ID
     */
    @ApiModelProperty(value = "会员卡卡号", required = true)
    @NotBlank(message = "会员卡卡号不能为空！")
    private String memberNum;

    @ApiModelProperty(value = "入账方式ID", required = true)
    @NotNull(message = "入账方式ID不能为空！")
    private Integer accountItemId;

    @ApiModelProperty(value = "预付款入账方式name", required = true)
    @NotNull(message = "预付款入账方式name不能为空！")
    private String accountItemName;

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
    @Min(value = 0, message = "输入本金金额不能小于0！")
    private BigDecimal principalAmount;

    /**
     * 赠金金额
     */
    @ApiModelProperty(value = "赠金金额")
    private BigDecimal bonusAmount;
}
