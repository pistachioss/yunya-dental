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
 * 简介: 会员退费参数模型
 *
 * @author: chow
 * @date: 2020/9/22 20:46
 * @description:
 * @since: 1.0.0
 */
@ApiModel("会员退费参数模型")
@Data
@ToString
public class CardMemberRefundModel implements Serializable {

  /** 会员卡账户ID */
  @ApiModelProperty(value = "会员卡卡号", required = true)
  @NotBlank(message = "会员卡卡号不能为空！")
  private String memberAccountId;
  /** 会员卡入账方式ID */
  @ApiModelProperty(value = "会员卡入账方式ID", required = true)
  @NotNull(message = "会员卡入账方式ID不能为空！")
  private Integer accountItemId;

  /** 支付金额 */
  @ApiModelProperty(value = "会员卡退费本金", required = true)
  @NotNull(message = "会员卡退费本金不能为空！")
  @Min(value = 0, message = "输入会员卡退费本金金额不能小于0！")
  private BigDecimal principalAmount;

  @ApiModelProperty(value = "会员卡退费赠金", required = true)
  @NotNull(message = "会员卡退费赠金不能为空！")
  @Min(value = 0, message = "输入会员卡退费赠金金额不能小于0！")
  private BigDecimal giftAmount;
}
