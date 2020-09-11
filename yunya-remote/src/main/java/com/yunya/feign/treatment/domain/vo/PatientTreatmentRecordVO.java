package com.yunya.feign.treatment.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 患者就诊记录信息VO
 *
 * @author: chow
 * @date: 2020/9/11 11:00
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientTreatmentRecordVO implements Serializable {
  /** 就诊记录ID */
  private Integer treatmentRecordId;
  /** 就诊日期 */
  private String treatmentDate;
  /** 初/复诊 */
  private Byte firstVisit;
  /** 接诊医生ID */
  private Integer dentistId;
  /** 医生姓名 */
  private String dentistName;
  /** 就诊诊所ID */
  private Integer orgId;
  /** 诊所名称 */
  private String orgName;
  /** 助手1ID */
  private Integer assistantId1;
  /** 助手1姓名 */
  private String assistantName1;
  /** 助手2ID */
  private Integer assistantId2;
  /** 助手2姓名 */
  private String assistantName2;
  /** 助手3ID */
  private Integer assistantId3;
  /** 助手2姓名 */
  private String assistantName3;
  /** 就诊状态 */
  private Byte treatmentStatus;
  /** 开单记录ID */
  private Integer orderRecordId;
  /** 原价合计 */
  private BigDecimal originalPrice;
  /** 账单记录ID */
  private Integer billRecordId;
  /** 实收金额 */
  private BigDecimal actualReceivableAmount;
  /** 本单欠费金额 */
  private BigDecimal debtAmount;
}
