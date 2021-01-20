package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 患者预约信息VO
 *
 * @author: chow
 * @date: 2021/1/20 13:09
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者预约信息VO")
@Data
@ToString
public class PatientAppointmentInfoVO implements Serializable {
  /** 预约次数 */
  @ApiModelProperty("预约次数")
  private Integer totalReservation;
  /** 履约次数 */
  @ApiModelProperty("履约次数")
  private Integer totalPerformance;
  /** 失约次数 */
  @ApiModelProperty("失约次数")
  private Integer totalMissedAppointment;
  /** 就诊次数 */
  @ApiModelProperty("就诊次数")
  private Integer numberOfVisits;
}
