package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 门诊患者信息VO
 *
 * @author: chow
 * @date: 2020/12/8 14:07
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊患者信息VO")
@Data
@ToString
public class PatientDataStatisticsVO implements Serializable {

  public PatientDataStatisticsVO() {}

  public PatientDataStatisticsVO(boolean needInit) {
    if (needInit) {
      orgId = -1;
      abbreviation = "";
      firstVisitPerNum = 0;
      appointPerNum = 0;
      repeatVisitsPerNum = 0;
      appointPerTimes = 0;
      appointModifyPerTimes = 0;
      treatPerTimes = 0;
      perCapitaConsumption = BigDecimal.ZERO;
      averageConsumption = BigDecimal.ZERO;
      appointCancelPerTimes = 0;
      appointMissedPerTimes = 0;
      treatPerNum = 0;
      repeatVisitsPerTimes = 0;
      totalActualAmount = BigDecimal.ZERO;
    }
    new PatientDataStatisticsVO();
  }

  /** 门诊id */
  @ApiModelProperty("门诊id")
  private Integer orgId;

  /** 门诊名称 */
  @Excel(name = "门诊")
  @ApiModelProperty("门诊")
  private String abbreviation;

  /** 初诊人数 */
  @Excel(name = "初诊人数", cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("初诊人数")
  private Integer firstVisitPerNum;

  /** 预约人数 */
  @Excel(name = "预约人数", cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("预约人数")
  private Integer appointPerNum;

  /** 复诊人数 */
  @Excel(name = "复诊人数", cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("复诊人数")
  private Integer repeatVisitsPerNum;

  /** 预约人次 */
  @Excel(name = "预约人次", cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("预约人次")
  private Integer appointPerTimes;

  /** 改约人次 */
  @Excel(name = "改约人次", cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("改约人次")
  private Integer appointModifyPerTimes;

  /** 就诊人次 */
  @Excel(name = "就诊人次", cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("就诊人次")
  private Integer treatPerTimes;

  /** 人均消费 */
  @Excel(name = "人均消费", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("人均消费")
  private BigDecimal perCapitaConsumption;

  /** 次均消费 */
  @Excel(name = "次均消费", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("次均消费")
  private BigDecimal averageConsumption;

  /** 取消预约人次 */
  @Excel(name = "取消预约人次", cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("取消预约人次")
  private Integer appointCancelPerTimes;

  /** 失约人次 */
  @Excel(name = "失约人次", cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("失约人次")
  private Integer appointMissedPerTimes;

  /** 就诊人数 */
  @ApiModelProperty("就诊人数")
  private Integer treatPerNum;

  /** 复诊人次 */
  @ApiModelProperty("复诊人次")
  private Integer repeatVisitsPerTimes;

  /** 实收合计 */
  private BigDecimal totalActualAmount;
}
