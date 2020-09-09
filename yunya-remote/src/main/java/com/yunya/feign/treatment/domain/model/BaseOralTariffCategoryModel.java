package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介: 商品分类新增参数模型
 *
 * @author: chow
 * @date: 2020/8/3 09:52
 * @description:
 * @since: 1.0.0
 */
@ApiModel("商品分类新增参数模型")
@Data
@ToString
public class BaseOralTariffCategoryModel implements Serializable {
  @ApiModelProperty(value = "商品分类名称", required = true)
  @NotBlank(message = "商品分类名称不能为空")
  @Size(max = 25, message = "商品分类名称长度不能超过25个字符")
  private String name;

  @ApiModelProperty(value = "商品分类编号", required = true)
  @Size(min = 6, max = 6, message = "商品分类编号长度必须是6位！")
  @NotBlank(message = "商品分类编号不能为空")
  private String number;
}
