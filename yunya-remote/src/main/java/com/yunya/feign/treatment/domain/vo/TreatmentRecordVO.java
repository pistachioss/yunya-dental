package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 就诊记录信息VO
 *
 * @author: chow
 * @date: 2020/9/27 11:20
 * @description:
 * @since: 1.0.0
 */
@ApiModel("就诊记录信息VO")
@Data
@ToString
public class TreatmentRecordVO implements Serializable {
  /** 就诊记录ID */
  @ApiModelProperty("就诊记录ID")
  private Integer id;
  /** 门诊id */
  @ApiModelProperty("门诊id")
  private Integer orgId;
  /** 组织名称 */
  @ApiModelProperty("组织名称")
  private String orgName;
  /** 患者id */
  @ApiModelProperty("患者id")
  private Integer patientId;
  /** 患者名称 */
  @ApiModelProperty("患者名称")
  private String patientName;
  /** 患者手机号 */
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 患者预约id */
  @ApiModelProperty("患者预约id")
  private Integer appointmentId;
  /** 挂号表id */
  @ApiModelProperty("挂号表id")
  private Integer registeredId;
  /** 主治医生id */
  @ApiModelProperty("主治医生id")
  private Integer dentistId;
  /** 接诊医生名称 */
  @ApiModelProperty("接诊医生名称")
  private String dentistName;
  /** 0：初诊，1：复诊 */
  @ApiModelProperty("0：初诊，1：复诊")
  private Byte firstVisit;
  /** 诊疗状态(0-接诊中;1-已开单;2-接诊完成3-已结账) */
  @ApiModelProperty("诊疗状态(0-接诊中;1-已开单;2-接诊完成3-已结账)")
  private Byte status;
  /** 就诊日期 */
  @ApiModelProperty("就诊日期")
  private String treatDate;
  /** 就诊开始时间（医生点击开始接诊的时间） */
  @ApiModelProperty("就诊开始时间（医生点击开始接诊的时间）")
  private String treatStartTime;
  /** 就诊结束时间(医生端点击就诊完成的时间) */
  @ApiModelProperty("就诊结束时间(医生端点击就诊完成的时间)")
  private String treatEndTime;
  /** 是否书写电子病历（0-否；1-是） */
  @ApiModelProperty("是否书写电子病历（0-否；1-是）")
  private Boolean medicalRecordCompleted;
  /** 备注 */
  @ApiModelProperty("备注")
  private String remarks;
  /** 是否有效、是否启用、是否可见 */
  @ApiModelProperty("是否有效、是否启用、是否可见")
  private Boolean inservice;
}
