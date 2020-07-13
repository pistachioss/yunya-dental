package com.yunya.modules.system.form.query;

import com.yunya.framework.common.model.PageQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 系统功能按钮查询参数封装
 *
 * @author: chow
 * @date: 2020/6/24 17:09
 * @description:
 * @since: 1.0.0
 */
@ApiModel("菜单按钮查询参数封装模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class SysElementQueryForm extends PageQueryParams implements Serializable {
  /** 按钮ID */
  @ApiModelProperty("按钮ID")
  private Integer id;
  /** 菜单ID */
  @ApiModelProperty(value = "菜单ID", required = true)
  private Integer menuId;
  /** 资源名称 */
  @ApiModelProperty("按钮名称")
  private String name;
}
