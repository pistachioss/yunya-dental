package com.yunya.modules.system.form.query;

import com.yunya.framework.common.model.PageQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 简单介绍:</br> 部门模版查询参数封装
 *
 * @author: chow
 * @date: 2020/5/29 13:40
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "部门模版列表查询参数模型（可分页）", parent = PageQueryParams.class)
public class DepartmentQueryForm extends PageQueryParams {
  /** 部门ID */
  @ApiModelProperty("部门模版ID")
  private Integer id;
  /** 部门名称 */
  @ApiModelProperty("部门模版名称")
  private String name;
  /** 部门类型 */
  @ApiModelProperty("部门类型数组,example:[0,1]；0-门诊/1-公司")
  private Byte[] types;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
