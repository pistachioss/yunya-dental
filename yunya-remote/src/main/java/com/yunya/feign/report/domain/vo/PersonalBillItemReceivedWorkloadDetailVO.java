package com.yunya.feign.report.domain.vo;

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
 * 简介: 收费项目已收工作量明细VO
 *
 * @author: chow
 * @date: 2021/4/17 13:50
 * @description:
 * @since: 1.0.0
 */
@ApiModel("收费项目已收工作量明细VO")
@Data
@ToString
public class PersonalBillItemReceivedWorkloadDetailVO implements Serializable {
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
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生姓名 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生姓名")
  private String regDentistName;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者手机号 */
  @Excel(name = "患者手机号")
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 实收工作量 */
  @Excel(name = "实收工作量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("实收工作量")
  private BigDecimal itemReceivedWorkload;
  /** 执行人ID */
  @ApiModelProperty("执行人ID")
  private Integer executorId;
  /** 执行人姓名 */
  @Excel(name = "执行人")
  @ApiModelProperty("执行人姓名")
  private String executorName;
}
