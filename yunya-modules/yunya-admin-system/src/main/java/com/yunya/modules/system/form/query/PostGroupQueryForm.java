package com.yunya.modules.system.form.query;

import com.yunya.framework.common.model.PageQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "岗位组查询参数封装模型")
public class PostGroupQueryForm extends PageQueryParams {
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
