package com.yunya.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 简单介绍:</br> 系统岗位关系参数封装模型
 *
 * @author: chow
 * @date: 2020/6/27 23:10
 * @description:
 * @since: 1.0.0
 */
@ApiModel("岗位授权参数封装模型")
@Data
@ToString
public class SysPostResourceForm implements Serializable {
  /** 岗位ID */
  @ApiModelProperty(value = "岗位ID", required = true)
  private Integer postId;
  /**岗位组ID*/
  @ApiModelProperty(value = "岗位组ID")
  private Integer postGroupId;
  /** 权限列表 */
  @ApiModelProperty("权限列表")
  @NotEmpty(message = "权限列表不能为空")
  private List<ResourceForm> elements;
}
