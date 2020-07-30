package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 组织部门新增参数模型
 *
 * @author: chow
 * @date: 2020/7/27 19:06
 * @description:
 * @since: 1.0.0
 */
@ApiModel("组织部门新增参数模型")
@Data
@ToString
public class CompanyDepartmentModel implements Serializable {
  /** 公司父部门ID */
  @ApiModelProperty("组织父部门ID")
  private Integer parentId;

  /** 公司ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID为空！")
  private Integer companyId;

  /** 部门模版ID */
  @ApiModelProperty(value = "部门模版ID", required = true)
  @NotNull(message = "部门模版ID为空！")
  private Integer departmentId;

  /** 组织部门排序 */
  @ApiModelProperty(value = "自定义排序", required = true)
  @NotNull(message = "组织部门自定义排序为空")
  private Integer orderNum;
}
