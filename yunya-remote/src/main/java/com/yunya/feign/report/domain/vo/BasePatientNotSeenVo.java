package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介:患者报表-未复诊预约且未提醒Vo
 *
 * @author: WY
 * @date: 2020/10/27 20:36
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("未复诊预约且未提醒Vo")
public class BasePatientNotSeenVo implements Serializable {
  /** 就诊记录ID */
  @ApiModelProperty("就诊记录ID")
  private Integer treatmentId;
  /** 末次就诊日期 */
  @Excel(name = "末次就诊日期")
  @ApiModelProperty(value = "末次就诊日期")
  private String lastVisitDate;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者名称 */
  @Excel(name = "患者名称")
  @ApiModelProperty(value = "患者名称")
  private String name;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty(value = "手机号")
  private String mobile;
  /** 初复诊 */
  @Excel(name = "初复诊", readConverterExp = "0=初诊,1=复诊")
  @ApiModelProperty(value = "初复诊")
  private Byte treatType;
  /** 医生ID */
  @ApiModelProperty("末诊医生ID")
  private Integer dentistId;
  /** 末次接诊医生 */
  @Excel(name = "末次接诊医生")
  @ApiModelProperty(value = "末次接诊医生")
  private String employeeName;
}
