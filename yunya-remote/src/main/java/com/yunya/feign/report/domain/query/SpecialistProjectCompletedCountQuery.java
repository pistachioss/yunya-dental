package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 专科项目完成数量查询参数模型
 *
 * @author: chow
 * @date: 2021/1/15 20:57
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科项目完成数量查询参数模型")
@Data
@ToString
public class SpecialistProjectCompletedCountQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  private Integer orgId;
  /** 价目表ID列表 */
  @ApiModelProperty(value = "价目表ID列表", required = true)
  private String[] tariffIds;
  /** 开始时间 */
  @ApiModelProperty(value = "开始时间", required = true)
  private String startDate;
  /** 结束时间 */
  @ApiModelProperty(value = "结束时间", required = true)
  private String endDate;
}
