package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 助手配诊明细VO
 *
 * @author: chow
 * @date: 2020/12/3 14:47
 * @description:
 * @since: 1.0.0
 */
@ApiModel("助手配诊明细VO")
@Data
@ToString
public class EmployeeTreatMatchingDetailVO implements Serializable {
  /** 就诊记录ID */
  @ApiModelProperty("就诊记录ID")
  private Integer treatmentId;
  /** 就诊日期 */
  @ApiModelProperty("就诊日期")
  private String treatDate;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者手机号 */
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 开始接诊时间 */
  @ApiModelProperty("开始接诊时间")
  private String treatStartTime;
  /** 结束接诊时间 */
  @ApiModelProperty("结束接诊时间")
  private String treatEndTime;
  /** 配诊时长 */
  @ApiModelProperty("配诊时长")
  private Integer matchDuration;
}
