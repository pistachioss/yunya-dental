package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 助手配诊退费记录明细VO
 *
 * @author: chow
 * @date: 2020/12/4 11:21
 * @description:
 * @since: 1.0.0
 */
@ApiModel("助手配诊退费记录明细VO")
@Data
@ToString
public class AssistantRefundDetailVO implements Serializable {
  /** 退费记录ID */
  @ApiModelProperty("退费记录ID")
  private Integer refundId;
  /** 退费日期 */
  @ApiModelProperty("退费日期")
  private String refundDate;
  /** 账单编号 */
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者手机号 */
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 配诊医生ID */
  @ApiModelProperty("配诊医生ID")
  private Integer matchingDentistId;
  /** 医生姓名 */
  @ApiModelProperty("医生姓名")
  private String matchingDentistName;
  /** 退费金额 */
  @ApiModelProperty("退费金额")
  private BigDecimal refundAmount;
}
