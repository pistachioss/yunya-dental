package com.yunya.feign.treatment.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 就诊完成患者信息VO
 *
 * @author: chow
 * @date: 2020/8/21 17:50
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class TreatmentCompletedPatientInfoVO implements Serializable {
  /** 接诊记录ID */
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
  private String patientKind;
  /** 患者过敏原 */
  private String allergen;
  /** 欠费总额 */
  private BigDecimal arrears;
  /** 会员图标 */
  private String memberIcon;
  /** 患者备注 */
  private String patientRemark;
  /********************************  预约信息 *********************************/
  /** 预约id */
  private Integer appointmentId;
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
  /******************************** 挂号信息 *******************************/
  /** 挂号ID */
  private Integer registeredId;
  /** 挂号医生id */
  private Integer regDentistId;
  /** 挂号医生姓名 */
  private String regDentistName;
  /** 挂号助手id */
  private Integer regAssistantId;
  /** 挂号助手名称 */
  private String regAssistantName;
  /** 挂号科室ID */
  private Integer regDeptRoomId;
  /** 挂号科室名称 */
  private String regDeptRoomName;
  /** 挂号时间 */
  private String regTime;
  /******************************* 接诊信息 ********************************/
  /** 接诊医生ID */
  private Integer treatDentistId;
  /** 接诊医生姓名 */
  private String treatDentistName;
  /** 开始接诊时间 */
  private String treatStartTime;
  /** 后续预约 */
  private Integer nextAppointment;
  /** 后续随访 */
  private Integer nextInterview;
  /** 书写病历 */
  private Boolean medicalRecordCompleted;
  /***************************** 开单记录信息 *******************************/
  /** 开单记录ID */
  private Integer orderRecordId;
  /** 原价合计 */
  private BigDecimal originalPrice;
  /** 账单状态 */
  private Byte status;
}
