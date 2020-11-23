package com.yunya.modules.system.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介: 门诊入账方式列表查询参数模型
 *
 * @author: chow
 * @date: 2020/7/27 13:51
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门诊入账方式列表查询参数模型")
public class ClinicAccountItemQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页,默认分页")
  private Boolean whetherPage = true;
  @ApiModelProperty("页码，默认第1页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;
  @ApiModelProperty("每页显示数量，默认显示10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  /** 组织入账方式ID */
  @ApiModelProperty("组织入账方式ID")
  private Integer id;
  /** 入账方式分类ID */
  @ApiModelProperty(value = "入账方式分类ID",required = true)
  private Integer accountTypeId;
  /** 入账方式ID */
  @ApiModelProperty(value = "入账方式ID")
  private Integer accountItemId;
  /** 组织ID */
  @ApiModelProperty(value = "组织ID",required = true)
  private Integer orgId;
  /** 是否启用 */
  @ApiModelProperty("是否启用，过滤门诊不启用的入账方式传true,不过滤则不传")
  private Boolean inservice;
}
