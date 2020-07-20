package com.yunya.modules.system.form.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简单介绍:</br> 菜单查询参数封装
 *
 * @author: chow
 * @date: 2020/6/24 10:20
 * @description:
 * @since: 1.0.0
 */
@ApiModel("菜单查询参数模型")
@Data
@ToString
public class SysMenuQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页", required = true)
  private Boolean whetherPage = true;

  @ApiModelProperty("页码")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  /** 菜单ID */
  @ApiModelProperty("菜单ID")
  private Integer id;
  /** 菜单标题 */
  @ApiModelProperty("菜单标题")
  private String title;
  /** 系统ID */
  @ApiModelProperty("系统ID(0-公司后台系统；1-门诊系统)")
  private Integer systemId;
  /** 菜单类型 */
  @ApiModelProperty("系统ID(0-web；1-app)")
  private Byte menuType;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
  /** 菜单类型 */
  @ApiModelProperty("菜单类型")
  private String type;
}
