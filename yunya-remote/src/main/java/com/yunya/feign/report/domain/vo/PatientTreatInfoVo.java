package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介: 患者就诊信息VO
 *
 * @author: WY
 * @date: 2020/12/16 17:02
 * @description:
 * @since: 1.0.0
 */
@ApiModel(value = "PatientDataFirstVisitVo", description = "患者就诊信息VO")
@Data
@ToString
public class PatientTreatInfoVo implements Serializable {
  /** 就诊记录ID */
  @ApiModelProperty("就诊记录ID")
  private Integer treatmentId;
  /** 初诊日期 */
  @ApiModelProperty("就诊日期")
  private String treatDate;
  /** 末诊时间 */
  @ApiModelProperty("末诊时间")
  private Date treatStartTime;
  /** 接诊医生ID */
  @ApiModelProperty("接诊医生ID")
  private Integer treatDentistId;
  /** 初诊医生 */
  @ApiModelProperty("接诊医生")
  private String treatDentistName;
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 初诊门诊 */
  @ApiModelProperty("就诊门诊")
  private String treatOutpatient;
  /** 患者id */
  @ApiModelProperty("患者id")
  private Integer patientId;
}
