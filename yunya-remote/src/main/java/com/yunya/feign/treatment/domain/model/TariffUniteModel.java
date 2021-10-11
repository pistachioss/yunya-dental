package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 价目表（商品）价格统一模型
 *
 * @author: chow
 * @date: 2021/10/11 13:40
 * @description:
 * @since: 1.0.0
 */
@Data
@ApiModel("价目表（商品）价格统一模型")
public class TariffUniteModel implements Serializable {
  /** 基础商品项目ID */
  @ApiModelProperty(value = "价目表（商品）项目ID", required = true)
  @NotNull(message = "项目ID不能为空")
  private Integer id;
  /** 单价 */
  @ApiModelProperty(value = "单价", required = true)
  @NotNull(message = "统一单价不能为空")
  private BigDecimal price;
}
