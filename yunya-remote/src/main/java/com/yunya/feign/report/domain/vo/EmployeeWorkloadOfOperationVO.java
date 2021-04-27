package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 运营报表员工工作量VO
 *
 * @author: chow
 * @date: 2020/12/4 16:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("运营报表员工工作量VO")
@Data
@ToString
public class EmployeeWorkloadOfOperationVO implements Serializable {
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 员工ID */
  @ApiModelProperty("员工ID")
  private Integer employeeId;
  /** 岗位 */
  @Excel(name = "岗位")
  @ApiModelProperty("岗位")
  private String postNames;
  /** 员工姓名 */
  @Excel(name = "员工")
  @ApiModelProperty("员工姓名")
  private String employeeName;
  /** 在职状态 */
  @ApiModelProperty("在职状态（0-试用；1-转正；2-离职）")
  private Byte workStatus;
  /** 实收工作量（元） */
  @Excel(name = "实收工作量（元）", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("实收工作量（元）")
  private BigDecimal actualWorkload;
  /** 已收工作量（元） */
  @Excel(name = "已收工作量（元）", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("已收工作量（元）")
  private BigDecimal receivedWorkload;
  /** 免单支付工作量（元） */
  @Excel(name = "免单支付工作量（元）", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("免单支付工作量（元）")
  private BigDecimal freePaymentWorkload;
  /** 补入工作量（元） */
  @Excel(name = "补入工作量（元）", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("补入工作量（元）")
  private BigDecimal supplementWorkload;
  /** 退费工作量（元） */
  @Excel(name = "退费工作量（元）", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("退费工作量（元）")
  private BigDecimal refundWorkload;
  /** 加工费（元） */
  @Excel(name = "加工费（元）", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("加工费（元）")
  private BigDecimal processingFee;
  /** 正畸加工费（元） */
  @Excel(name = "正畸加工费（元）", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("正畸加工费（元）")
  private BigDecimal orthodonticsFee;
  /** 基本工作量（元） */
  @Excel(name = "基本工作量（元）", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("基本工作量（元）")
  private BigDecimal baseWorkload;
  /** 大额材料费（元） */
  @Excel(name = "大额材料费（元）", scale = 2, cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
  @ApiModelProperty("大额材料费（元）")
  private BigDecimal largeMaterialCost;
}
