package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 助手实收工作量明细VO
 *
 * @author: chow
 * @date: 2020/12/3 19:57
 * @description:
 * @since: 1.0.0
 */
@ApiModel(" ")
@Data
@ToString
public class AssistantActualWorkloadDetailVO implements Serializable {
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 账单编号 */
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 开单日期 */
  @ApiModelProperty("开单日期")
  private String orderDate;
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
  /** 实收工作量 */
  @ApiModelProperty("实收工作量")
  private BigDecimal actualWorkload;
}
