package com.yunya.modules.system.form.query;

import com.yunya.framework.common.model.PageQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 简单介绍:</br> 品牌查询参数封装模型
 *
 * @author: chow
 * @date: 2020/5/28 14:48
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "品牌列表查询参数模型（可分页）", parent = PageQueryParams.class)
@EqualsAndHashCode(callSuper = true)
public class BrandQueryForm extends PageQueryParams {

  /** 品牌ID */
  @ApiModelProperty("品牌ID")
  private String id;
  /** 品牌名称 */
  @ApiModelProperty("品牌名称")
  private String name;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
