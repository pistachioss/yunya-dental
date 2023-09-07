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
public class ClinicEmployeeWorkloadOfOperationVO extends ClinicEmployeBonusCoefficientVO implements Serializable {
  /** 员工工作量（元） */
  @Excel(name = "员工工作量（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("员工工作量（元）")
  private BigDecimal employeeWorkload;
  /** 应收工作量（元） */
  @Excel(name = "应收工作量（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("应收工作量（元）")
  private BigDecimal actualWorkload;
  /** 实收工作量（元） */
  @Excel(name = "实收工作量（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("实收工作量（元）")
  private BigDecimal receivedWorkload;
  /** 划扣工作量 */
  @Excel(name = "划扣工作量（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("划扣工作量")
  private BigDecimal swipeWorkload;
  /** 免单支付工作量（元） */
  @Excel(name = "免单支付工作量（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("免单支付工作量（元）")
  private BigDecimal freePaymentWorkload;
  /** 补入工作量（元） */
  @Excel(name = "补入工作量（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("补入工作量（元）")
  private BigDecimal supplementWorkload;
  /** 划扣补入工作量 */
  @Excel(name = "划扣补入工作量（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("划扣补入工作量")
  private BigDecimal swipeCouponWorkload;
  /** 退费工作量（元） */
  @Excel(name = "退费工作量（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("退费工作量（元）")
  private BigDecimal refundWorkload;
  /** 加工费（元） */
  @Excel(name = "加工费（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("加工费（元）")
  private BigDecimal processingFee;
  /** 正畸加工费（元） */
  @Excel(name = "正畸加工费（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("正畸加工费（元）")
  private BigDecimal orthodonticsFee;
  /** 基本工作量（元） */
  @Excel(name = "基本工作量（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("基本工作量（元）")
  private BigDecimal baseWorkload;
  /** 大额材料费（元） */
  @Excel(name = "大额材料费（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("大额材料费（元）")
  private BigDecimal largeMaterialCost;
}
