package com.yunya.feign.treatment.domain.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 患者就诊信息数据传输对象
 *
 * @author: chow
 * @date: 2020/10/12 11:09
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientTreatmentInfoDTO implements Serializable {
  /** 患者id */
  private Integer patientId;
  /** 医生id */
  private Integer dentistId;
  /** 助手ID */
  private Integer assistantId;
  /** 诊疗状态 */
  private Byte treatStatus;
  /** 患者预约id */
  private Integer appointmentId;
  /** 患者挂号id */
  private Integer regId;
  /** 患者就诊记录id */
  private Integer treatmentRecordId;
  /** 账单id */
  private Integer billRecordId;
  /** 预约状态 */
  private Byte appointState;
  /** 预约时间 */
  private String appointTime;
  /** 预约医生id */
  private Integer appointDentistId;
  /** 挂号医生id */
  private Integer rgDentistId;
  /** 挂号时间 */
  private String rgTime;
  /** 就诊开始时间 */
  private String treatStartTime;
  /** 就诊结束时间 */
  private String treatEndTime;
}
