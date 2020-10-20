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
@ApiModel("其他入账方式信息参数模型")
@Data
@ToString
public class PaymentModel implements Serializable {

  /** 入账方式ID */
  @ApiModelProperty(value = "入账方式ID", required = true)
  @NotNull(message = "入账方式ID不能为空！")
  private Integer accountItemId;

  @ApiModelProperty(value = "入账金额", required = true)
  @NotNull(message = "入账金额不能为空！")
  @Min(value = 0, message = "输入金额不能小于0！")
  private BigDecimal amount;
}
