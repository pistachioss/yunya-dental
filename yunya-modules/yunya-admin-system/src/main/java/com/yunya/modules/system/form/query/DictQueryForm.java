package com.yunya.modules.system.form.query;

import com.yunya.framework.common.model.PageQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "字典查询参数模型", parent = PageQueryParams.class)
public class DictQueryForm extends PageQueryParams {
  /** 字典ID */
  @ApiModelProperty("主键ID")
  private Integer id;
  /** 字典类型ID */
  @ApiModelProperty("字典类型ID（查询字典数据传入）")
  private Integer dictTypeId;
  /** 字典名称 */
  @ApiModelProperty("字典名称")
  private String name;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
