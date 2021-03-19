package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 开单数量及金额统计明细VO
 *
 * @author: chow
 * @date: 2020/12/7 14:26
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单数量及金额统计明细VO")
@Data
@ToString
public class BillItemStatisticsDetailVO implements Serializable {
  /** 门诊*/
  @Excel(name = "门诊")
  @ApiModelProperty("门诊")
  private String abbreviation;

  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 开单数量 */
  @Excel(name = "开单数量")
  @ApiModelProperty("开单数量")
  private Integer quantity;
  /** 实收金额 */
  @Excel(name = "实收金额")
  @ApiModelProperty("实收金额")
  private BigDecimal amount;
  /** 挂号医生 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生")
  private String regDentistName;
  /** 执行人 */
  @Excel(name = "执行人")
  @ApiModelProperty("执行人")
  private String executorName;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String mobile;
}
