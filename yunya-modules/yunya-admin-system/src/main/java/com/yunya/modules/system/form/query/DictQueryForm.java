package com.yunya.modules.system.form.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

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
public class DictQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页", required = true)
  private Boolean whetherPage = true;

  @ApiModelProperty("页码")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
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
