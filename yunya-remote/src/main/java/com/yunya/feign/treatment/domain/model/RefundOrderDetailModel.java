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
 * 简介: 退费订单明细模型
 *
 * @author: chow
 * @date: 2020/9/21 19:28
 * @description:
 * @since: 1.0.0
 */
@ApiModel("退费订单明细模型")
@Data
@ToString
public class RefundOrderDetailModel implements Serializable {

  /** 订单明细ID */
  @ApiModelProperty(value = "订单明细ID", required = true)
  @NotNull(message = "退费订单明细ID不能为空！")
  private Integer orderDetailId;
  /** 退费金额 */
  @ApiModelProperty(value = "退费金额", required = true)
  @NotNull(message = "退费金额不能为空！")
  @Min(value = 0, message = "退费金额不能为负数！")
  private BigDecimal refundAmount;
}
