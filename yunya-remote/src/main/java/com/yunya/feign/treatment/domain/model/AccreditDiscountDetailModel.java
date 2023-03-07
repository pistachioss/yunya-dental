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
  /** 开单类型（0-价目表；1-商品；） */
  @ApiModelProperty("开单类型（0-价目表；1-商品；）")
  private Byte type;
  /** 开单项目ID */
  @ApiModelProperty("开单项目ID")
  private Integer billingItemId;
  /** 开单数量 */
  @ApiModelProperty(value = "数量", required = true)
  @NotNull(message = "开单项目数量不能为空！")
  @Min(value = 1, message = "数量不能小于0")
  private Integer quantity;
  /** 实收金额 */
  @ApiModelProperty(value = "实收金额", required = true)
  @NotNull(message = "实收金额不能为空！")
  @Min(value = 0, message = "输入金额不能小于0！")
  private BigDecimal actualAmount;

  /** 折扣率 */
  @ApiModelProperty(value = "折扣率", required = true)
  @NotNull(message = "折扣率不能为空")
  private BigDecimal discountRate;
}
