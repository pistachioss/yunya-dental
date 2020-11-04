package com.yunya.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 字典类型VO
 *
 * @author: chow
 * @date: 2020/6/3 17:48
 * @description:
 * @since: 1.0.0
 */
@ApiModel("字典类型VO")
@Data
@ToString
public class DictionaryTypeVO implements Serializable {
  /** 字典类型ID */
  @ApiModelProperty("字典类型ID")
  private Integer id;
  /** 字典类型名称 */
  @ApiModelProperty("字典类型名称")
  private String name;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
