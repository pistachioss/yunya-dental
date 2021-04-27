package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 应收账款余额VO
 *
 * @author: chow
 * @date: 2020/11/25 18:17
 * @description:
 * @since: 1.0.0
 */
@ApiModel("应收账款余额VO")
@Data
@ToString
public class BillRestReceivableAmountVO implements Serializable {
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生姓名 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生姓名")
  private String regDentistName;
  /** 原价合计 */
  @Excel(name = "原价合计", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("原价合计")
  private BigDecimal totalOriginalAmount;
  /** 实收金额 */
  @Excel(name = "实收金额", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("实收金额")
  private BigDecimal totalActualAmount;
  /** 账单剩余欠费余额 */
  @Excel(name = "账单剩余欠费余额", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("账单剩余欠费余额")
  private BigDecimal billReceivableAmount;
}
