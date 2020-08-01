package com.yunya.feign.discount.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介: 产品分类修改参数模型
 *
 * @author: chow
 * @date: 2020/7/30 17:06
 * @description:
 * @since: 1.0.0
 */
@ApiModel("产品分类修改参数模型")
@Data
@ToString
public class ProductTypeForm implements Serializable {
  /** 分类名称 */
  @ApiModelProperty(value = "产品分类名称", required = true)
  @NotBlank(message = "产品分类名称不能为空！")
  @Size(max = 25, message = "产品分类名称长度不能超过25个字符！")
  private String name;
  /** 是否启用 */
  @ApiModelProperty(value = "是否启用", required = true)
  private Boolean inservice;
}
