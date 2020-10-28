package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 就诊配诊记录VO
 *
 * @author: chow
 * @date: 2020/10/28 10:44
 * @description:
 * @since: 1.0.0
 */
@ApiModel("配诊记录VO")
@Data
@ToString
public class TreatmentMatchingRecordVO implements Serializable {
  /** 就诊记录ID */
  private Integer treatmentId;
  /** 助手1ID */
  private Integer assistant1Id;
  /** 助手1姓名 */
  private String assistant1Name;
  /** 助手2ID */
  private Integer assistant2Id;
  /** 助手2姓名 */
  private String assistant2Name;
  /** 助手2ID */
  private Integer assistant3Id;
  /** 助手3姓名 */
  private String assistant3Name;
  /** 配诊医生ID */
  private Integer matchingDentistId;
  /** 配诊医生姓名 */
  private String matchingDentistName;
  /** 患者ID */
  private Integer patientId;
  /** 患者姓名 */
  private Integer patientName;
  /** 手机号 */
  private String mobile;
  /** 配诊日期 */
  private String matchingDate;
  /** 开始接诊时间 */
  private String treatStartTime;
  /** 治疗完成时间 */
  private String treatEndTime;
  /** 配诊时长 */
  private Integer matchingDuration;
}
