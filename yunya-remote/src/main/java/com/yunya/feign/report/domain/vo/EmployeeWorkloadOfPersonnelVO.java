package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介: 员工工作量VO
 *
 * @author: chow
 * @date: 2020/10/29 16:02
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工工作量VO模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmployeeWorkloadOfPersonnelVO extends EmployeeWorkloadOfOperationVO
    implements Serializable {
  /** 奖金系数 */
  @Excel(name = "奖金系数")
  @ApiModelProperty("奖金系数")
  private BigDecimal bonusCoefficient;
  /** 应收奖金基数（元） */
  @Excel(name = "应收奖金基数（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("应收奖金基数（元）= 应收工作量+补入工作量-退费工作量-加工费-大额材料费-基本工作量")
  private BigDecimal actualBonusBase;
  /** 实收奖金基数（元） */
  @Excel(name = "实收奖金基数（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("实收奖金基数（元）= 实收工作量+补入工作量-退费工作量-加工费-大额材料费-基本工作量")
  private BigDecimal receivedBonusBase;
  /** 应收奖金（元） */
  @Excel(name = "应收奖金（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("应收奖金（元）= 应收奖金基数*奖金系数")
  private BigDecimal actualBonus;
  /** 实收奖金（元） */
  @Excel(name = "实收奖金（元）", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("实收奖金（元）= 实收奖金基数*奖金系数")
  private BigDecimal receivedBonus;
}
