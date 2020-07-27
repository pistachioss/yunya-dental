package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/7/27 20:13
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("岗位分类新增参数模型")
public class PostGroupModel implements Serializable {
  /** 上级岗位分类ID */
  @ApiModelProperty(value = "上级岗位组ID", required = true)
  @NotNull(message = "上级岗位组ID不能为空！")
  private Integer parentId;

  /** 岗位分类名称 */
  @ApiModelProperty(value = "岗位组名称", required = true)
  @NotBlank(message = "岗位组名称不能为空")
  @Size(max = 50, message = "岗位组名称长度不能超过50个字符")
  private String name;

  /** 自定义排序 */
  @ApiModelProperty(value = "自定义排序为空", required = true)
  @NotNull(message = "自定义排序不能为空")
  private Integer orderNum;

  /** 允许操作（编辑/删除） */
  @ApiModelProperty(value = "字段是否允许操作（编辑/删除）")
  private Boolean allowOperation;
}
