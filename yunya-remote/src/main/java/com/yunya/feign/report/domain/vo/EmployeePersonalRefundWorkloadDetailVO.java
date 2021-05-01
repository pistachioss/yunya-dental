package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 员工退费工作量明细VO
 *
 * @author: chow
 * @date: 2020/12/1 15:24
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工退费工作量明细VO")
@Data
@ToString
public class EmployeePersonalRefundWorkloadDetailVO implements Serializable {
  /** 退费记录ID */
  @ApiModelProperty("退费记录ID")
  private Integer refundId;
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
  /** 退费日期 */
  @Excel(name = "退费日期")
  @ApiModelProperty("退费日期")
  private String refundDate;
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
  /** 实收金额 */
  @Excel(name = "退费工作量", cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("退费工作量")
  private BigDecimal refundWorkload;
  /** 员工ID */
  @ApiModelProperty("员工ID")
  private Integer employeeId;
}
