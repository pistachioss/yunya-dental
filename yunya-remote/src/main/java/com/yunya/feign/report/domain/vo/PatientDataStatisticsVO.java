package com.yunya.feign.report.domain.vo;

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
  /** 初诊人数 */
  @ApiModelProperty("初诊人数")
  private Integer firstVisitPerNum;
  /** 复诊人数 */
  @ApiModelProperty("复诊人数")
  private Integer repeatVisitsPerNum;
  /** 就诊人数 */
  @ApiModelProperty("就诊人数")
  private Integer treatPerNum;
  /** 预约人数 */
  @ApiModelProperty("预约人数")
  private Integer appointPerNum;
  /** 复诊人次 */
  @ApiModelProperty("复诊人次")
  private Integer repeatVisitsPerTimes;
  /** 就诊人次 */
  @ApiModelProperty("就诊人次")
  private Integer treatPerTimes;
  /** 预约人次 */
  @ApiModelProperty("预约人次")
  private Integer appointPerTimes;
  /** 改约人次 */
  @ApiModelProperty("改约人次")
  private Integer appointModifyPerTimes;
  /** 取消预约人次 */
  @ApiModelProperty("取消预约人次")
  private Integer appointCancelPerTimes;
  /** 失约人次 */
  @ApiModelProperty("失约人次")
  private Integer appointMissedPerTimes;
  /** 人均消费 */
  @ApiModelProperty("人均消费")
  private BigDecimal perCapitaConsumption;
  /** 次均消费 */
  @ApiModelProperty("次均消费")
  private BigDecimal averageConsumption;
}
