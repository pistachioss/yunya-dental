package com.yunya.modules.system.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

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
@ApiModel(value = "组织部门列表查询参数模型")
public class OrgDeptQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页", required = true)
  private Boolean whetherPage = true;

  @ApiModelProperty("页码")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  /** 组织ID */
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
