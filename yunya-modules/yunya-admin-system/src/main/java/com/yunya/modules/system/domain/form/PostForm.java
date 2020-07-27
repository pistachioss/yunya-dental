package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简单介绍:</br> 岗位参数form
 *
 * @author: chow
 * @date: 2020/6/3 13:04
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("岗位修改参数模型")
public class PostForm implements Serializable {
  /** 岗位名称 */
  @NotBlank(message = "岗位名称不能为空")
  @ApiModelProperty(value = "岗位名称", required = true)
  @Size(max = 50, message = "岗位名称长度不能超过50个字符")
  private String name;
  /** 岗位类型 */
  @NotNull(message = "岗位组ID不能为空")
  @ApiModelProperty(value = "岗位组ID", required = true)
  private Integer postGroupId;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
