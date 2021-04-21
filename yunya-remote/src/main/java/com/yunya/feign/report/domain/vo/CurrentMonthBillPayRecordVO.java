package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 门诊当月账单当月收费记录VO
 *
 * @author: chow
 * @date: 2021/1/7 13:54
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊当月账单当月收费记录VO")
@Data
@ToString
public class CurrentMonthBillPayRecordVO implements Serializable {
  /** 收费记录ID */
  @ApiModelProperty("收费记录ID")
  private Integer billPayId;
  /** 收费日期 */
  @Excel(name = "收费日期")
  @ApiModelProperty("收费日期")
  private String payeeDate;
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
  private String patientMobile;
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
  private BigDecimal orderAmount;
  /** 优惠金额 */
  @Excel(name = "优惠金额", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("优惠金额")
  private BigDecimal privilegeAmount;
  /** 实收金额 */
  @Excel(name = "实收金额", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("实收金额")
  private BigDecimal actualAmount;
  /** 本次收费金额 */
  @Excel(name = "本次收费金额", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("本次收费金额")
  private BigDecimal receivedAmount;
  /** 本次剩余欠费金额 */
  @Excel(name = "本次剩余欠费金额", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("本次剩余欠费金额")
  private BigDecimal stillOweAmount;
  /** 账单欠费金额（剩余欠费金额） */
  @Excel(name = "剩余欠费金额", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("剩余欠费金额")
  private BigDecimal billDebtAmount;
  /** 收费人ID */
  @ApiModelProperty("收费人ID")
  private Integer payeeId;
  /** 收费人姓名 */
  @Excel(name = "收费人")
  @ApiModelProperty("收费人姓名")
  private String payeeName;
}
