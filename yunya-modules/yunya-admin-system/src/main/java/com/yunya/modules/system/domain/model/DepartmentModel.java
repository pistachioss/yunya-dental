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
 * 简介: 部门模版新增参数模型
 *
 * @author: chow
 * @date: 2020/7/27 19:21
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("部门模版新增参数模型")
public class DepartmentModel implements Serializable {
  /** 部门名 */
  @ApiModelProperty(value = "部门名称", required = true)
  @Size(max = 50, message = "名称长度不能超过50个字符")
  @NotBlank(message = "部门名称不能为空！")
  private String name;

  /** 部门类型(0-门诊部门；1-公司部门） */
  @ApiModelProperty(value = "部门类型（0-门诊部门；1-公司部门）", required = true)
  @NotNull(message = "部门类型不能为空！")
  private Byte type;

  /** 自定义排序 */
  @ApiModelProperty("自定义排序")
  private Integer orderNum;
}
