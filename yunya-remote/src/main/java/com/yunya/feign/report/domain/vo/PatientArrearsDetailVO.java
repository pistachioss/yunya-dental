package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 患者欠费账单详情VO
 *
 * @author: chow
 * @date: 2020/12/10 10:26
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者欠费账单详情VO")
@Data
@ToString
public class PatientArrearsDetailVO implements Serializable {
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer ordId;
  /** 组织名称 */
  @ApiModelProperty("组织名称")
  private String orgName;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生 */
  @ApiModelProperty("挂号医生")
  private String regDentistName;
  /** 账单日期 */
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 账单编号 */
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 原价合计 */
  @ApiModelProperty("原价合计")
  private BigDecimal originalAmount;
  /** 优惠金额 */
  @ApiModelProperty("优惠金额")
  private BigDecimal privilegeAmount;
  /** 实收金额 */
  @ApiModelProperty("实收金额")
  private BigDecimal actualAmount;
  /** 已收金额 */
  @ApiModelProperty("已收金额")
  private BigDecimal receivedAmount;
  /** 剩余欠费合计 */
  @ApiModelProperty("剩余欠费合计")
  private BigDecimal remainingArrears;
}
