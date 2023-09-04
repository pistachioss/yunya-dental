package com.yunya.feign.treatment.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 简介: 项目组合列表查询参数模型
 *
 * @author: chow
 * @date: 2020/8/6 12:50
 * @description:
 * @since: 1.0.0
 */
@ApiModel("项目组合列表查询参数模型")
@Data
@ToString
public class TariffPackageQueryForm extends PageQuery {
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  
  /** 组合id */
  @ApiModelProperty(value = "组合id")
  private Integer packageId;

  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
