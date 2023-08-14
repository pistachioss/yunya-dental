package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 其他入账方式信息参数模型
 *
 * @author: chow
 * @date: 2020/8/27 17:35
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("退费其他入账方式信息参数模型")
public class TreatPaymentModel implements Serializable {
  
  /** 会员卡或预付款卡号 */
  @ApiModelProperty("会员卡或预付款卡号")
  private String cardNumber;

  /** 入账方式ID */
  @ApiModelProperty(value = "入账方式ID", required = true)
  @NotNull(message = "入账方式ID不能为空！")
  private Integer accountItemId;

  @ApiModelProperty(value = "入账金额", required = true)
  @NotNull(message = "入账金额不能为空！")
  @Min(value = 0, message = "输入金额不能小于0！")
  private BigDecimal amount;

  /** 本金金额 */
  @ApiModelProperty(value = "本金金额", required = true)
  @NotNull(message = "本金金额不能为空！")
  @Min(value = 0, message = "输入本金金额不能小于0！")
  private BigDecimal principal;
  
  /** 备注 */
  @ApiModelProperty(value = "备注")
  private String remarks;
}
