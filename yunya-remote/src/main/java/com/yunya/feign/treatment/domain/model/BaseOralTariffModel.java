package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 商品项目新增参数模型
 *
 * @author: chow
 * @date: 2020/8/3 10:25
 * @description:
 * @since: 1.0.0
 */
@ApiModel("商品项目新增参数模型")
@Data
@ToString
public class BaseOralTariffModel implements Serializable {

  @ApiModelProperty(value = "商品分类ID", required = true)
  @NotNull(message = "商品分类分类ID不能为空！")
  private Integer oralTariffCategoryId;

  /** 项目编码 */
  @NotBlank(message = "商品项目编码不能为空")
  @ApiModelProperty(value = "商品项目编码", required = true)
  @Size(min = 6, max = 6, message = "商品项目编码长度必须是6个字符")
  private String itemNumber;

  /** 项目名称 */
  @NotBlank(message = "商品项目名称不能为空")
  @ApiModelProperty(value = "项目名称", required = true)
  private String name;

  /** 项目英文名称 */
  @ApiModelProperty("商品项目英文名称")
  private String englishName;

  /** 单位 */
  @ApiModelProperty(value = "单位", required = true)
  @NotBlank(message = "商品项目单位不能为空！")
  private String unit;

  /** 价格 */
  @ApiModelProperty(value = "价格", required = true)
  @NotNull(message = "商品项目价格不能为空！")
  @Min(value = 0, message = "价格不能小于0！")
  private BigDecimal price;

  /** 门诊商品项目单价列表 */
  @ApiModelProperty(value = "门诊商品项目单价列表", required = true)
  private List<ClinicItemPriceModel> clinicItemPriceModels;

  /** 数量 */
  @ApiModelProperty("数量")
  private Integer quantity;

  /** 调价方式: 0, 手动调价; 1, 自动调价 */
  @ApiModelProperty("调价方式: 0, 手动调价; 1, 自动调价")
  private Boolean adjust;

  /** 是否计算绩效:0否,1是 */
  @ApiModelProperty("是否计算绩效")
  private Boolean achie;
}
