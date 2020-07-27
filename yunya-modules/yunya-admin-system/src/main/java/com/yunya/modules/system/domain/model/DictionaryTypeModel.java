package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/7/27 19:43
 * @description:
 * @since: 1.0.0
 */
@ApiModel("字典分类新增参数模型")
@Data
@ToString
public class DictionaryTypeModel implements Serializable {
  /** 字典名称 */
  @ApiModelProperty(value = "字典类型名称", required = true)
  @NotBlank(message = "字典类型名称为空！")
  @Size(max = 50, message = "字典类型名称不能超过50个字符")
  private String name;
}
