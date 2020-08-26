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
 * 简介: 预付款账户参数模型
 *
 * @author: chow
 * @date: 2020/8/26 17:31
 * @description:
 * @since: 1.0.0
 */
@ApiModel("预付款账户参数模型")
@Data
@ToString
public class PrepaymentAccountModel implements Serializable {
  /** 预付款账户ID */
  @ApiModelProperty(value = "预付款账户ID", required = true)
  @NotNull(message = "预付款账户ID不能为空！")
  private Integer prepaymentAccountId;
  /** 支付金额 */
  @ApiModelProperty(value = "支付金额", required = true)
  @NotNull(message = "支付金额不能为空！")
  @Min(value = 0, message = "最小金额为0")
  private BigDecimal amount;
}
