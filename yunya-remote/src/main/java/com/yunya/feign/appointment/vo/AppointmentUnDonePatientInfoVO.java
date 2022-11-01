package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 预约未完成患者信息VO
 *
 * @author: chow
 * @date: 2020/8/17 10:26
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "AppointmentUnDonePatientInfoVO", description = "预约未完成患者信息VO")
public class AppointmentUnDonePatientInfoVO implements Serializable {
  /** 预约ID */
  @ApiModelProperty("预约ID")
  private Integer id;
  /** 诊所id */
  @ApiModelProperty("诊所id")
  private Integer orgId;
  /** 初/复诊 */
  @ApiModelProperty("初/复诊;0-初诊，1-复诊")
  private Byte firstVisit;

  /******************************* 患者信息 ********************************/
  /** 患者id */
  @ApiModelProperty("患者id")
  private Integer patientId;
  /** 患者姓名 */
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者手机号 */
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 患者出生日期 */
  @ApiModelProperty("患者生日")
  private String birthday;
  /** 性别 */
  @ApiModelProperty("性别;0-男，1-女")
  private Byte gender;
  /** 年龄 */
  @ApiModelProperty("年龄")
  private Integer age;
  /** 患者病历号 */
  @ApiModelProperty("病历号")
  private String medicalNumber;
  /** 患者类型 */
  @ApiModelProperty("患者类型")
  private String patientKind;
  /** 患者过敏原 */
  @ApiModelProperty("过敏原")
  private String allergen;
  /** 患者过敏原描述 */
  @ApiModelProperty("过敏原描述")
  private String allergenDescription;
  /** 欠费 */
  @ApiModelProperty("欠费")
  private BigDecimal arrears;
  /** 会员卡名 */
  @ApiModelProperty("会员卡类型名称")
  private String memberCardName;
  /** 会员图标 */
  @ApiModelProperty("会员卡图标")
  private String memberIcon;
  /** 患者备注 */
  @ApiModelProperty("患者备注")
  private String patientRemark;
  /** 沟通标识 */
  @ApiModelProperty("沟通标识")
  private Boolean isCommunicate;

  /********************************  预约信息 *********************************/
  /** 预约医生id */
  @ApiModelProperty("预约医生id")
  private Integer appointDentistId;
  /** 预约医生姓名 */
  @ApiModelProperty("预约医生姓名")
  private String appointDentistName;
  /** 预约助手id */
  @ApiModelProperty("预约助手id")
  private Integer appointAssistantId;
  /** 预约助手姓名 */
  @ApiModelProperty("预约助手姓名")
  private String appointAssistantName;
  /** 预约科室ID */
  @ApiModelProperty("预约科室ID")
  private Integer appointDeptRoomId;
  /** 预约科室名称 */
  @ApiModelProperty("预约科室名称")
  private String appointDeptRoomName;
  /** 预约时间 */
  @ApiModelProperty("预约时间")
  private String appointTime;
  /** 预约时长 */
  @ApiModelProperty("预约时长")
  private Integer appointDuration;
  /** 预约内容 */
  @ApiModelProperty("预约内容")
  private String appointContent;
  /** 预约备注 */
  @ApiModelProperty("预约备注")
  private String appointRemark;
  /** 预约类型（初/复诊） */
  @ApiModelProperty("预约类型（0-初/1-复诊） ")
  private Byte appointType;
  /** 预约状态 */
  @ApiModelProperty("预约状态:0-预约未到，1-履约，2，取消预约，3-失约")
  private Byte appointStatus;
  /** 预约确认 */
  @ApiModelProperty("预约确认(0-未确认，1-已确认)")
  private Boolean confirmStatus;
  /** 预约创建时间 */
  @ApiModelProperty("预约创建时间")
  private String appointmentCrtTime;
}
