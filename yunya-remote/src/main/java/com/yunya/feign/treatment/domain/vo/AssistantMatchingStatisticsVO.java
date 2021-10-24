package com.yunya.feign.treatment.domain.vo;

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
 * 简介: 助手配诊统计VO
 *
 * @author: chow
 * @date: 2020/12/2 11:01
 * @description:
 * @since: 1.0.0
 */
@ApiModel("助手配诊统计VO")
@Data
@ToString
public class AssistantMatchingStatisticsVO implements Serializable {
  /** 组织ID */
  private Integer orgId;
  @ApiModelProperty("门诊名称")
  @Excel(name = "门诊名称")
  private String orgName;
  /** 员工ID */
  @ApiModelProperty("员工ID")
  private Integer assistantId;
  /** 员工姓名 */
  @Excel(name = "助手")
  @ApiModelProperty("员工姓名")
  private String assistantName;
  /** 助手1配诊时长 */
  @Excel(name = "助手1配诊时长（分钟）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("助手1配诊时长")
  private Integer treatMatchingTimeAsAssistant1;
  /** 助手1配诊应收工作量 */
  @Excel(name = "助手1配诊应收工作量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("助手1配诊应收工作量")
  private BigDecimal treatMatchingActualWorkloadAsAssistant1;
  /** 助手1配诊退费工作量 */
  @Excel(name = "助手1配诊退费金额", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("助手1配诊退费工作量")
  private BigDecimal treatMatchingRefundWorkloadAsAssistant1;
  /** 助手2配诊时长 */
  @Excel(name = "助手2配诊时长（分钟）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("助手2配诊时长")
  private Integer treatMatchingTimeAsAssistant2;
  /** 助手2配诊应收工作量 */
  @Excel(name = "助手2配诊应收工作量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("助手2配诊应收工作量")
  private BigDecimal treatMatchingActualWorkloadAsAssistant2;
  /** 助手2配诊退费工作量 */
  @Excel(name = "助手2配诊退费金额", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("助手2配诊退费工作量")
  private BigDecimal treatMatchingRefundWorkloadAsAssistant2;
  /** 巡回配诊时长 */
  @Excel(name = "巡回配诊时长（分钟）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("巡回配诊时长")
  private Integer treatMatchingTimeAsAssistant3;
  /** 巡回配诊应收工作量 */
  @Excel(name = "巡回配诊应收工作量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("巡回配诊应收工作量")
  private BigDecimal treatMatchingActualWorkloadAsAssistant3;
  /** 巡回配诊退费工作量 */
  @Excel(name = "巡回配诊退费金额", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("巡回配诊退费工作量")
  private BigDecimal treatMatchingRefundWorkloadAsAssistant3;
}
