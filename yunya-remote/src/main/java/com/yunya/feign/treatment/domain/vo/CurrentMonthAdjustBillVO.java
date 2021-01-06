package com.yunya.feign.treatment.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 门诊当月调整账单VO
 *
 * @author: chow
 * @date: 2021/1/6 14:21
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊当月调整账单VO")
@Data
@ToString
public class CurrentMonthAdjustBillVO implements Serializable {
  /** 调整日期 */
  @Excel(name = "调整日期")
  @ApiModelProperty("调整日期")
  private String adjustDate;
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 账单日期 */
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 账单编号 */
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 当月/非当月账单 */
  @ApiModelProperty("当月/非当月账单")
  private String currentBill;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者手机号 */
  @ApiModelProperty("患者手机号")
  private String patientMobile;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生姓名 */
  @ApiModelProperty("挂号医生姓名")
  private String regDentistName;
  /** 调整人ID */
  @ApiModelProperty("调整人ID")
  private Integer operatorId;
  /** 调整人姓名 */
  @ApiModelProperty("调整人姓名")
  private String operatorName;
}
