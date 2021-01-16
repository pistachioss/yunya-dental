package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 专科项目完成情况VO
 *
 * @author: chow
 * @date: 2021/1/15 19:42
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科项目完成情况VO")
@Data
@ToString
public class SpecialistProjectCompletedInfoVO implements Serializable {
  /** 专科项目ID */
  @ApiModelProperty("专科项目ID")
  private Integer specialistProjectId;
  /** 专科项目名称 */
  @ApiModelProperty("专科项目名称")
  private String specialistProjectName;
  /** 专科项目目标数量 */
  @ApiModelProperty("专科项目目标数量")
  private Integer specialistProjectGoalCount;
  /** 专科项目完成数量 */
  @ApiModelProperty("专科项目完成数量")
  private Integer specialistProjectCompletedCount;
  /** 完成百分比 */
  @ApiModelProperty("完成百分比")
  private BigDecimal specialistProjectCompletedPercentage;
}
