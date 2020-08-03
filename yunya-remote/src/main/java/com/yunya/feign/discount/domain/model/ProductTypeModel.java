package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介: 产品分类新增模型
 *
 * @author: chow
 * @date: 2020/7/30 16:45
 * @description:
 * @since: 1.0.0
 */
@ApiModel("产品分类新增参数模型")
@Data
@ToString
public class ProductTypeModel implements Serializable {
  /** 分类名称 */
  @ApiModelProperty(value = "产品分类名称", required = true)
  @NotBlank(message = "产品分类名称不能为空！")
  @Size(max = 25, message = "产品分类名称长度不能超过25个字符！")
  private String name;
}
