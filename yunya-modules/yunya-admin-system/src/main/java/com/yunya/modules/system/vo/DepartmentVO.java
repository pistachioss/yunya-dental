package com.yunya.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: chow
 * @date: 2020/5/29 13:56
 * @description:
 * @since: 1.0.0
 */
@ApiModel("部门信息VO")
@Data
@ToString
public class DepartmentVO implements Serializable {
  /** 部门ID */
  @ApiModelProperty("部门ID")
  private Integer id;
  /** 部门名称 */
  @ApiModelProperty("部门名称")
  private String name;
  /** 部门类型 */
  @ApiModelProperty("部门类型(0-门诊部门；1-公司部门）")
  private Byte type;
  /** 自定义部门排序 */
  @ApiModelProperty("自定义部门排序")
  private Integer orderNum;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
