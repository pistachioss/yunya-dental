package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介：
 *
 * @author: chenlin @Description: @Date: 2021/3/12 16:03
 * @since: 1.0.0
 */
@Data
@ToString
public class PersonalWorkloadVO implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织id")
  private Integer orgId;

  @ApiModelProperty("门诊")
  @Excel(name = "门诊")
  private String abbreviation;
  /** 员工ID */
  @ApiModelProperty("员工ID")
  private Integer employeeId;
  /** 员工姓名 */
  @Excel(name = "员工")
  @ApiModelProperty("员工姓名")
  private String employeeName;
  /** 在职状态 */
  @ApiModelProperty("在职状态（0-试用；1-转正；2-离职）")
  private Byte workStatus;
  /** 实收工作量（元） */
  @Excel(name = "实收工作量（元）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("实收工作量（元）")
  private BigDecimal actualWorkload;
  /** 已收工作量（元） */
  @Excel(name = "已收工作量（元）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("已收工作量（元）")
  private BigDecimal receivedWorkload;
  /** 免单支付工作量（元） */
  @Excel(name = "免单支付工作量（元）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("免单支付工作量（元）")
  private BigDecimal freePaymentWorkload;
  /** 补入工作量（元） */
  @Excel(name = "补入工作量（元）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("补入工作量（元）")
  private BigDecimal supplementWorkload;
  /** 退费工作量（元） */
  @Excel(name = "退费工作量（元）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("退费工作量（元）")
  private BigDecimal refundWorkload;
  /** 加工费（元） */
  @Excel(name = "加工费（元）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("加工费（元）")
  private BigDecimal processingFee;
  /** 正畸加工费（元） */
  @Excel(name = "正畸加工费（元）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("正畸加工费（元）")
  private BigDecimal orthodonticsFee;
  /** 基本工作量（元） */
  @Excel(name = "基本工作量（元）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("基本工作量（元）")
  private BigDecimal baseWorkload;
  /** 大额材料费（元） */
  @Excel(name = "大额材料费（元）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("大额材料费（元）")
  private BigDecimal largeMaterialCost;
}
