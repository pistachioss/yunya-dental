package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 字典明细新增参数模型
 *
 * @author: chow
 * @date: 2020/7/27 19:37
 * @description:
 * @since: 1.0.0
 */
@ApiModel("字典明细新增参数模型")
@Data
@ToString
public class DictionaryItemModel implements Serializable {
  /** 字典类型ID */
  @ApiModelProperty(value = "字典类型ID", required = true)
  @NotNull(message = "字典类型为空")
  private Integer dictionaryTypeId;

  /** 字典选项 */
  @ApiModelProperty(value = "字典明细名称", required = true)
  @NotBlank(message = "字典名称为空！")
  private String name;

  /** 字典明细英文名称 */
  @ApiModelProperty(value = "字典明细英文名称")
  private String englishName;

  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
