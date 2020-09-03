package com.yunya.feign.appointment.vo;

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
public class AppointmentUnDonePatientInfoVO implements Serializable {
  /** 预约ID */
  private Integer id;

  /** 诊所id */
  private Integer orgId;

  /** 初/复诊 */
  private Byte firstVisit;

  /******************************* 患者信息 ********************************/
  /** 患者id */
  private Integer patientId;

  /** 患者姓名 */
  private String patientName;

  /** 患者手机号 */
  private String mobile;

  /** 患者出生日期 */
  private String birthday;

  /** 性别 */
  private Byte gender;

  /** 年龄 */
  private Integer age;

  /** 患者病历号 */
  private String medicalNumber;

  /** 患者类型 */
  private Integer patientKind;

  /** 患者过敏原 */
  private String allergen;

  /** 患者过敏原描述 */
  private String allergenDescription;

  /** 欠费 */
  private BigDecimal arrears;

  /** 会员图标 */
  private String memberIcon;

  /** 患者备注 */
  private String patientRemark;

  /********************************  预约信息 *********************************/
  /** 预约医生id */
  private Integer appointDentistId;

  /** 预约医生姓名 */
  private String appointDentistName;

  /** 预约助手id */
  private Integer appointAssistantId;

  /** 预约助手姓名 */
  private String appointAssistantName;

  /** 预约科室ID */
  private Integer appointDeptRoomId;

  /** 预约科室名称 */
  private String appointDeptRoomName;

  /** 预约时间 */
  private String appointTime;

  /** 预约时长 */
  private Integer appointDuration;

  /** 预约内容 */
  private String appointContent;

  /** 预约备注 */
  private String appointRemark;

  /** 预约类型（初/复诊） */
  private Byte appointType;

  /** 预约状态 */
  private Byte appointStatus;

  /** 预约确认 */
  private Boolean confirmStatus;
}
