package com.yunya.modules.system.form.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简单介绍:</br> 岗位组查询参数封装模型
 *
 * @author: chow
 * @date: 2020/6/10 10:52
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "岗位组查询参数封装模型")
public class PostGroupQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页", required = true)
  private Boolean whetherPage = true;

  @ApiModelProperty("页码")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  /** 岗位组ID */
  @ApiModelProperty("岗位组ID")
  private Integer id;
  /** 岗位组名称 */
  @ApiModelProperty("岗位组名称")
  private String name;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
