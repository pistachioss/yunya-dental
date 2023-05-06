package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 基础价目表修改参数模型
 *
 * @author: chow
 * @date: 2020/8/5 16:19
 * @description:
 * @since: 1.0.0
 */
@ApiModel("基础价目表修改参数模型")
@Data
@ToString
public class BaseTariffForm implements Serializable {
  @ApiModelProperty(value = "价目表分类ID", required = true)
  @NotNull(message = "价目表分类ID不能为空！")
  private Integer tariffCategoryId;

  /** 项目编码 */
  @NotBlank(message = "基础价目表编码不能为空")
  @ApiModelProperty(value = "基础价目表编码", required = true)
//  @Size(min = 6, max = 6, message = "基础价目表编码长度必须是6个字符")
  private String itemNumber;

  /** 项目名称 */
  @NotBlank(message = "基础价目表名称不能为空")
  @ApiModelProperty(value = "项目名称", required = true)
  private String name;

  /** 项目英文名称 */
  @ApiModelProperty("基础价目表英文名称")
  private String englishName;

  /** 单位 */
  @ApiModelProperty(value = "单位", required = true)
  @NotBlank(message = "基础价目表单位不能为空！")
  private String unit;

  /** 价格 */
  @ApiModelProperty(value = "价格", required = true)
  @NotNull(message = "基础价目表价格不能为空！")
  @Min(value = 0, message = "价格不能小于0！")
  private BigDecimal price;

  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;

  /** 门诊基础价目表单价列表 */
  @ApiModelProperty(value = "门诊基础价目表单价列表", required = true)
  private List<ClinicItemPriceForm> clinicItemPriceForms;
}
