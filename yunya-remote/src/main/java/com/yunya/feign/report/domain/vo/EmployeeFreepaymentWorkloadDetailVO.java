package com.yunya.feign.report.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介: 员工免单支付工作量明细VO
 *
 * @author: chenl
 * @date: 2021/03/04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工免单支付工作量明细VO")
@Data
@ToString
public class EmployeeFreepaymentWorkloadDetailVO implements Serializable {

  /** 账单收费ID */
  private Integer billPayId;

  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;

  /** 组织id */
  @ApiModelProperty("组织id")
  private Integer orgId;

  /** 员工id */
  @ApiModelProperty("员工id")
  private Integer employeeId;

  /** 收费时间 */
  @Excel(name = "收费日期", dateFormat = "yyyy-MM-dd")
  @ApiModelProperty("收费日期")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private String chargeDate;

  /** 收费门诊 */
  @Excel(name = "收费门诊")
  @ApiModelProperty("收费门诊")
  private String abbreviation;

  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;

  /** 账单日期 */
  //  @Excel(name = "订单日期", dateFormat = "yyyy-MM-dd HH:mm")
  @ApiModelProperty("订单日期")
  private String orderDate;

  /** 账单日期 */
  @Excel(name = "账单日期", dateFormat = "yyyy-MM-dd")
  @ApiModelProperty("账单日期")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private String billDate;

  /** 患者 */
  @Excel(name = "患者")
  @ApiModelProperty("患者")
  private String patientName;

  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String mobile;

  /** 免单支付工作量 */
  @Excel(name = "免单支付工作量", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("免单支付工作量")
  private BigDecimal freePaymentWorkload;
}
