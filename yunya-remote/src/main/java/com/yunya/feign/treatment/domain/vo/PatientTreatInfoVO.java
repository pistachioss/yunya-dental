package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 患者就诊信息（初诊、末诊）
 *
 * @author: chow
 * @date: 2020/11/21 16:22
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者就诊信息（初诊、末诊）")
@Data
@ToString
public class PatientTreatInfoVO implements Serializable {
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 就诊次数 */
  @ApiModelProperty("就诊次数")
  private Integer treatTimes;
  /** 初诊门诊ID */
  @ApiModelProperty("初诊门诊ID")
  private Integer firstTreatOrgId;
  /** 初诊门诊名称 */
  @ApiModelProperty("初诊门诊名称")
  private String firstTreatOrgName;
  /** 初诊日期 */
  @ApiModelProperty("初诊日期")
  private String firstTreatDate;
  /** 初诊医生ID */
  @ApiModelProperty("初诊医生ID")
  private Integer firstTreatDentistId;
  /** 初诊医生姓名 */
  @ApiModelProperty("初诊医生姓名")
  private String firstTreatDentistName;
  /** 末诊门诊ID */
  @ApiModelProperty("末诊门诊ID")
  private Integer lastTreatOrgId;
  /** 末诊门诊名称 */
  @ApiModelProperty("末诊门诊名称")
  private String lastTreatOrgName;
  /** 末诊日期 */
  @ApiModelProperty("末诊日期")
  private String lastTreatDate;
  /** 末诊医生ID */
  @ApiModelProperty("末诊医生ID")
  private Integer lastTreatDentistId;
  /** 末诊医生姓名 */
  @ApiModelProperty("末诊医生姓名")
  private String lastTreatName;
}
