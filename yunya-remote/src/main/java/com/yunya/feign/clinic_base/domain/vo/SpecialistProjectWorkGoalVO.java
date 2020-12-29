package com.yunya.feign.clinic_base.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 专科项目工作目标VO
 *
 * @author: chow
 * @date: 2020/12/28 13:26
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科项目工作目标VO")
@Data
@ToString
public class SpecialistProjectWorkGoalVO implements Serializable {
  /** 日期 */
  @ApiModelProperty("日期")
  private String businessDate;
  /** 专科项目ID */
  @ApiModelProperty("专科项目ID")
  private Integer specialistProjectId;
  /** 专科项目名称 */
  @ApiModelProperty("专科项目名称")
  private String specialistProjectName;
  /** 专科目标数量 */
  @ApiModelProperty("专科目标数量")
  private Integer specialistProjectGoal;
  /** 专科完成信息VO */
  private SpecialistProjectCompletedInfoVO specialistProjectCompletedInfo;
  /** 专科完成百分比 */
  @ApiModelProperty("专科完成百分比")
  private Float percentageOfSpecialistProjectCompleted;
}
