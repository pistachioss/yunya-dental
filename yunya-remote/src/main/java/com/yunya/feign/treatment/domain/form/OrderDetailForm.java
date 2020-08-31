package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介: 开单明细修改参数模型
 *
 * @author: chow
 * @date: 2020/8/21 14:54
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单明细修改参数模型")
@Data
@ToString
public class OrderDetailForm implements Serializable {
  @ApiModelProperty("开单明细ID")
  private Integer orderDetailId;

  @ApiModelProperty(value = "开单类型（0-价目表；1-商品；）", required = true)
  @NotNull(message = "开单类型不能为空！")
  private Byte type;

  @ApiModelProperty(value = "开单项目ID(非门诊价目表（商品）项目ID)", required = true)
  @NotNull(message = "开单项目ID不能为空！")
  private Integer billingItemId;

  @ApiModelProperty(value = "数量", required = true)
  @NotNull(message = "开单项目数量不能为空！")
  @Min(value = 1, message = "开单项目数量不能小于1！")
  private Integer quantity;

  @ApiModelProperty("牙位（多个用'，'隔开）")
  private String toothBit;

  @ApiModelProperty("执行人ID（type为0时必传！）")
  private Integer executorId;

  @ApiModelProperty("备注")
  @Size(max = 150, message = "开单备注最多150个字符！")
  private String remarks;
}
