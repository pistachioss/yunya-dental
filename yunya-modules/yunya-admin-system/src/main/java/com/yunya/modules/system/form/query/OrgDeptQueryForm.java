package com.yunya.modules.system.form.query;

import com.yunya.framework.common.model.PageQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 简单介绍:</br> 组织部门参数
 *
 * @author: chow
 * @date: 2020/6/5 11:25
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "组织部门列表查询参数模型", parent = PageQueryParams.class)
public class OrgDeptQueryForm extends PageQueryParams {
  /** 组织ID */
  @NotNull(message = "组织ID不能为空！")
  @ApiModelProperty(value = "组织ID", required = true)
  private Integer companyId;
  /** 组织部门ID */
  @ApiModelProperty("组织部门主键ID")
  private Integer id;
  /** 部门名称 */
  @ApiModelProperty("组织部门名称")
  private String name;
  /** 部门类型 */
  @ApiModelProperty("部门类型，可多选")
  private Byte[] types;
}
