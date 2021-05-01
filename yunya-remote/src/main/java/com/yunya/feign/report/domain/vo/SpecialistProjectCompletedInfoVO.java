package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

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
  @Excel(name = "专科项目")
  @ApiModelProperty("专科项目名称")
  private String specialistProjectName;

  /** 门诊id */
  @ApiModelProperty("门诊id")
  private Integer orgId;

  /** 门诊 */
  @Excel(name = "门诊")
  @ApiModelProperty("门诊")
  private String abbreviation;

  /** 专科项目完成数量 */
  @Excel(name = "完成数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("专科项目完成数量")
  private Integer specialistProjectCompletedCount;

  /** 专科项目目标数量 */
  @Excel(name = "目标数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("专科项目目标数量")
  private Integer specialistProjectGoalCount;

  /** 完成百分比 */
  @ApiModelProperty("完成百分比")
  private BigDecimal specialistProjectCompletedPercentage;
}
