package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 就诊记录报表VO
 *
 * @author: chow
 * @date: 2020/10/26 15:50
 * @description:
 * @since: 1.0.0
 */
@ApiModel("就诊记录报表VO")
@Data
@ToString
public class TreatmentRecordReportVO implements Serializable {
  /** 门诊ID */
  @ApiModelProperty("门诊ID")
  private Integer orgId;
  /** 门诊名称 */
  @ApiModelProperty("门诊名称")
  private String orgName;
  /** 就诊日期 */
  @Excel(name = "就诊日期")
  @ApiModelProperty("就诊日期")
  private String treatDate;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 性别（0-男；1-女） */
  @ApiModelProperty("性别（0-男；1-女)")
  private Byte gender;
  /** 初/复诊（0-初诊；1-复诊） */
  @Excel(name = "初复诊", readConverterExp = "0=初诊,1=复诊")
  @ApiModelProperty("初/复诊（0-初诊；1-复诊）")
  private Byte treatType;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生姓名 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生姓名")
  private String regDentistName;
  /** 预约时间 */
  @Excel(name = "预约时间")
  @ApiModelProperty("预约时间")
  private String appointTime;
  /** 预约时长 */
  @Excel(name = "预约时长(分钟)")
  @ApiModelProperty("预约时长")
  private Integer appointDuration;
  /** 预约内容 */
  @Excel(name = "预约内容")
  @ApiModelProperty("预约内容")
  private String appointContent;
  /** 挂号时间 */
  @Excel(name = "挂号时间")
  @ApiModelProperty("挂号时间")
  private String regTime;
  /** 开始接诊时间 */
  @Excel(name = "开始接诊时间")
  @ApiModelProperty("开始接诊时间")
  private String treatStartTime;
  /** 治疗完成时间 */
  @Excel(name = "治疗完成时间")
  @ApiModelProperty("治疗完成时间")
  private String treatEndTime;
  /** 挂号看诊等待时长 */
  @Excel(name = "挂号看诊等待时长(分钟)")
  @ApiModelProperty("挂号看诊等待时长")
  private Integer treatWaitingTime;
  /** 看诊时长 */
  @Excel(name = "看诊时长(分钟)")
  @ApiModelProperty("看诊时长")
  private Integer treatExpendTime;
}
