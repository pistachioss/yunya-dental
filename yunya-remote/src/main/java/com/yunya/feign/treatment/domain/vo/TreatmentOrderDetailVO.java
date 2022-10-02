package com.yunya.feign.treatment.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介: 就诊开单明细VO
 *
 * @author: chenlin
 * @date: 2022/9/27 11:20
 * @description:
 * @since: 1.0.0
 */
@ApiModel("就诊开单明细VO")
@Data
@ToString
public class TreatmentOrderDetailVO implements Serializable {
  /** 就诊记录ID */
  @ApiModelProperty("就诊记录ID")
  private Integer treatmentId;

  /** 开单记录ID */
  @ApiModelProperty("开单记录ID")
  private Integer orderRecordId;

  /** 患者id */
  @ApiModelProperty("患者id")
  private Integer patientId;

  /** 患者名称 */
  @ApiModelProperty("患者名称")
  private String patientName;

  /** 手机号 */
  @ApiModelProperty("手机号")
  private String mobile;

  /** 患者姓名拼音 */
  @ApiModelProperty("患者姓名拼音")
  private String pinyinName;

  /** 病历编号 */
  @ApiModelProperty("病历编号")
  private String medicalNumber;

  /** 项目名称列表 */
  @ApiModelProperty("项目名称列表")
  private String itemNames;

  /** 开单时间 */
  @ApiModelProperty("开单时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
  private Date orderDate;

  /** 挂号医生id */
  @ApiModelProperty("挂号医生id")
  private Integer dentistId;

  /** 挂号医生 */
  @ApiModelProperty("挂号医生")
  private String dentistName;

  /** 初复诊类型：0-初诊，1-复诊 */
  @ApiModelProperty("初复诊类型：0-初诊，1-复诊")
  private Integer treatType;
}
