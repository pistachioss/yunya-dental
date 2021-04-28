package com.yunya.feign.clinic_base.domain.vo;

import com.yunya.feign.treatment.domain.vo.SpecialistProjectTariffCompletedInfoVO;
import com.yunya.framework.common.annation.Excel;
import com.yunya.framework.common.annation.Excels;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

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
  /** 专科项目ID */
  @ApiModelProperty("专科项目ID")
  private Integer specialistProjectId;
  /** 专科项目名称 */
  @Excel(name = "专科项目", isMerge = true)
  @ApiModelProperty("专科项目名称")
  private String specialistProjectName;
  /** 日期 */
  @Excel(name = "日期")
  @ApiModelProperty("日期")
  private String businessDate;
  /** 专科目标数量 */
  @Excel(name = "目标数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("专科目标数量")
  private Integer specialistProjectGoal;
  /** 专科完成信息VO */
  @Excels({
    @Excel(
        name = "完成数量",
        targetAttr = "specialistProjectCompleted",
        type = EXPORT,
        cellType = NUMERIC,
        isStatistics = true)
  })
  private SpecialistProjectTariffCompletedInfoVO specialistProjectCompletedInfo;
  /** 专科完成百分比 */
  @Excel(name = "完成百分比", suffix = "%")
  @ApiModelProperty("专科完成百分比")
  private Float percentageOfSpecialistProjectCompleted;
}
