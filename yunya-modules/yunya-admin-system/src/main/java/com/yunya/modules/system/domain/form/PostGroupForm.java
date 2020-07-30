package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简单介绍:</br> 岗位组修改参数封装模型
 *
 * @author: chow
 * @date: 2020/6/10 13:43
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "岗位组修改参数封装模型")
public class PostGroupForm implements Serializable {
  /** 岗位组父ID */
  @ApiModelProperty(value = "上级岗位组ID", required = true)
  @NotNull(message = "上级岗位组ID为空")
  private Integer parentId;
  /** 岗位组名称 */
  @ApiModelProperty(value = "岗位组名称", required = true)
  @NotBlank(message = "岗位组名称为空")
  private String name;
  /** 自定义排序 */
  @ApiModelProperty(value = "自定义排序", required = true)
  @NotNull(message = "自定义排序为空")
  private Integer orderNum;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
