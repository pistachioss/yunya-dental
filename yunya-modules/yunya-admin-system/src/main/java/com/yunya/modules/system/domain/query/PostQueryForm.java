package com.yunya.modules.system.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.List;

/**
 * 简单介绍:</br> 岗位查询参数form(可分页)
 *
 * @author: chow
 * @date: 2020/6/3 13:50
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "岗位列表查询参数模型")
public class PostQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页", required = true)
  private Boolean whetherPage = true;

  @ApiModelProperty("页码")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  /** 岗位ID */
  @ApiModelProperty("岗位ID")
  private Integer id;
  /** 岗位名称 */
  @ApiModelProperty("岗位名称")
  private String name;
  /** 岗位类型 */
  @ApiModelProperty("岗位组ID")
  private Integer postGroupId;
  /** 岗位类型ID列表 */
  @ApiModelProperty(hidden = true)
  private List<Integer> groupIds;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
