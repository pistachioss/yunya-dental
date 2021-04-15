package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 员工补入工作量明细VO
 *
 * @author: chow
 * @date: 2020/12/1 09:56
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工补入工作量明细VO")
@Data
@ToString
public class EmployeePersonalSupplyWorkloadDetailVO implements Serializable {
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
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
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 原价合计 */
  @Excel(name = "原价合计", cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("原价合计")
  private BigDecimal totalOriginalAmount;
  /** 优惠金额 */
  @Excel(name = "优惠金额", cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("优惠金额")
  private BigDecimal privilegeAmount;
  /** 实收金额 */
  @Excel(name = "实收金额", cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("实收金额")
  private BigDecimal actualAmount;
  /** 实收工作量 */
  @Excel(name = "补入工作量", cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("补入工作量")
  private BigDecimal supplyWorkload;
}
