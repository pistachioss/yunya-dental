package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 员工已收工作量明细VO
 *
 * @author: chow
 * @date: 2020/11/30 17:38
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工已收工作量明细VO")
@Data
@ToString
public class EmployeePersonalReceivedWorkloadDetailVO implements Serializable {
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 收费记录ID */
  @ApiModelProperty("收费记录ID")
  private Integer billPayId;
  /** 收费时间 */
  @Excel(name = "收费日期")
  @ApiModelProperty("收费时间")
  private String chargeDate;
  /** 收费时间 */
  @Excel(name = "收费门诊")
  @ApiModelProperty("收费门诊")
  private String orgName;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 账单日期 */
  @Excel(name = "订单日期")
  @ApiModelProperty("订单日期")
  private String orderDate;
  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者名称")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String mobile;
  /** 本次已收工作量 */
  @Excel(name = "本次已收工作量", cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("本次已收工作量")
  private BigDecimal receivedWorkload;
}
