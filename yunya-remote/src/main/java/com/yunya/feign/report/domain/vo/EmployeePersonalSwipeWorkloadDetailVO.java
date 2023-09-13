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
 * 简介: 员工划扣工作量明细VO
 *
 * @author: chow
 * @date: 2020/11/30 17:38
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工划扣工作量明细VO")
@Data
@ToString
public class EmployeePersonalSwipeWorkloadDetailVO implements Serializable {
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 收费时间 */
  @Excel(name = "收费日期", dateFormat = "yyyy-MM-dd")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @ApiModelProperty("收费时间")
  private String chargeDate;
  /** 收费时间 */
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 账单日期 */
  @Excel(name = "账单日期", dateFormat = "yyyy-MM-dd")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 患者 */
  @Excel(name = "患者")
  @ApiModelProperty("患者")
  private String patientName;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String mobile;
  /** 划扣工作量 */
  @Excel(name = "划扣工作量", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("划扣工作量")
  private BigDecimal swipeWorkload;
}
