package com.yunya.feign.system.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 简单介绍:</br> 字典信息查询参数Form
 *
 * @author: chow
 * @date: 2020/6/3 17:18
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "字典查询参数模型")
public class DictQueryForm extends PageQuery {
  /** 字典ID */
  @ApiModelProperty("主键ID")
  private Integer id;
  /** 字典类型ID */
  @ApiModelProperty("字典类型ID（查询字典数据传入）")
  private Integer dictTypeId;

  @ApiModelProperty("字典类型名称（精确查询）")
  private String dictTypeName;
  /** 字典名称 */
  @ApiModelProperty("字典名称")
  private String name;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
