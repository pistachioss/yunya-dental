package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 简介: 价目表分类修改参数模型
 *
 * @author: chow
 * @date: 2020/7/31 15:00
 * @description:
 * @since: 1.0.0
 */
@ApiModel("价目表分类修改参数模型")
@Data
@ToString
public class BaseTariffCategoryForm {
  @ApiModelProperty(value = "价目表分类名称", required = true)
  @NotBlank(message = "价目表分类名称不能为空")
  @Size(max = 25, message = "价目表分类名称长度不能超过25个字符")
  private String name;

  @ApiModelProperty(value = "编号不能为空", required = true)
  @Size(min = 6, max = 6, message = "价目表分类编号长度必须是6位！")
  @NotBlank(message = "价目表分类编号不能为空")
  private String number;

  @ApiModelProperty(value = "是否启用", required = true)
  private Boolean inservice;
}
