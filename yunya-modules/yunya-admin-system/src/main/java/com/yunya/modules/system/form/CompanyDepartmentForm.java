package com.yunya.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简单介绍:</br> 组织部门参数form
 *
 * @author: chow
 * @date: 2020/6/2 20:48
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("组织部门修改参数模型")
public class CompanyDepartmentForm implements Serializable {
  /** 上级部门ID */
  @ApiModelProperty("上级组织部门ID")
  private Integer parentId;
  /** 自定义排序 */
  @ApiModelProperty(value = "自定义排序", required = true)
  @NotNull(message = "自定义排序为空")
  private Integer orderNum;
}
