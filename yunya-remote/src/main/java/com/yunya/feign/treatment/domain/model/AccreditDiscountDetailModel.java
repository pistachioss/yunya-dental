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
 * 简介: 授权折扣开单明细参数模型
 *
 * @author: chow
 * @date: 2020/8/27 16:56
 * @description:
 * @since: 1.0.0
 */
@ApiModel("授权折扣开单明细参数模型")
@Data
@ToString
public class AccreditDiscountDetailModel implements Serializable {
  /** 开单明细记录ID */
  @ApiModelProperty(value = "开单明细记录ID", required = true)
  @NotNull(message = "开单明细记录ID不能为空！")
  private Integer orderDetailId;

  @ApiModelProperty(value = "实收金额", required = true)
  @NotNull(message = "实收金额不能为空！")
  @Min(value = 0, message = "输入金额不能小于0！")
  private BigDecimal actualAmount;
}
