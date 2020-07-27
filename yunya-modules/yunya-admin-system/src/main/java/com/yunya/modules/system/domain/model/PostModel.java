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
 * 简介: 岗位新增参数模型
 *
 * @author: chow
 * @date: 2020/7/27 19:51
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("岗位新增参数模型")
public class PostModel implements Serializable {
  /** 岗位名称 */
  @ApiModelProperty(value = "岗位名称", required = true)
  @NotBlank(message = "岗位名称不能为空！")
  @Size(max = 50, message = "名称长度不能超过50个字符")
  private String name;

  /** 岗位组ID */
  @ApiModelProperty(value = "岗位组ID", required = true)
  @NotNull(message = "岗位组ID不能为空！")
  private Integer postGroupId;

  /** 自定义排序 */
  @ApiModelProperty("自定义排序")
  private Integer orderNum;

  /** 允许操纵（编辑/删除） */
  @ApiModelProperty(value = "字段是否允许操作（编辑/删除）")
  private Boolean allowOperation;
}
