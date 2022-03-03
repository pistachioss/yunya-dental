package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简单介绍:</br> 字典类型/明细Form
 *
 * @author: chow
 * @date: 2020/6/4 09:24
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "字典类型(明细)修改参数模型")
public class DictForm implements Serializable {
  /** 字典类型ID */
  @ApiModelProperty(value = "字典类型ID")
  private Integer dictionaryTypeId;
  /** 字典名称 */
  @ApiModelProperty(value = "字典名称", required = true)
  @NotBlank(message = "字典名称不能为空")
  @Size(max = 50, message = "字典名称长度不能超过50个字符")
  private String name;

  /** 字典明细英文名称 */
  @ApiModelProperty(value = "字典明细英文名称")
  private String englishName;

  /** 是否可用 */
  @ApiModelProperty(value = "是否启用")
  private Boolean inservice;
}
