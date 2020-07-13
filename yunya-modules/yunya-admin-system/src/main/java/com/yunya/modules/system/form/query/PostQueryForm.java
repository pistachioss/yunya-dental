package com.yunya.modules.system.form.query;

import com.yunya.framework.common.model.PageQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "岗位列表查询参数模型", parent = PageQueryParams.class)
public class PostQueryForm extends PageQueryParams {
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
