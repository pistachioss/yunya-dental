package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class EmployeeWorkloadVO implements Serializable {
  /** 员工ID */
  @ApiModelProperty("员工ID")
  private Integer userId;
  /** 岗位 */
  @Excel(name = "岗位")
  @ApiModelProperty("岗位")
  private String postNames;
  /** 员工姓名 */
  @Excel(name = "员工")
  @ApiModelProperty("员工姓名")
  private String employeeName;
  /** 实收工作量（元） */
  @Excel(name = "实收工作量（元）")
  @ApiModelProperty("实收工作量（元）")
  private BigDecimal actualWorkload;
  /** 已收工作量（元） */
  @Excel(name = "已收工作量（元）")
  @ApiModelProperty("已收工作量（元）")
  private BigDecimal receivedWorkload;
  /** 补入工作量（元） */
  @Excel(name = "补入工作量（元）")
  @ApiModelProperty("补入工作量（元）")
  private BigDecimal supplementWorkload;
  /** 退费工作量（元） */
  @Excel(name = "退费工作量（元）")
  @ApiModelProperty("退费工作量（元）")
  private BigDecimal refundWorkload;
  /** 加工费（元） */
  @Excel(name = "加工费（元）")
  @ApiModelProperty("加工费（元）")
  private BigDecimal processingFee;
  /** 大额材料费（元） */
  @Excel(name = "大额材料费（元）")
  @ApiModelProperty("大额材料费（元）")
  private BigDecimal largeMaterialCost;
  /** 基本工作量（元） */
  @Excel(name = "基本工作量（元）")
  @ApiModelProperty("基本工作量（元）")
  private BigDecimal baseWorkload;
  /** 奖金系数 */
  @Excel(name = "奖金系数")
  @ApiModelProperty("奖金系数")
  private BigDecimal bonusCoefficient;
  /** 实收奖金基数（元） */
  @Excel(name = "实收奖金基数（元）")
  @ApiModelProperty("实收奖金基数（元）")
  private BigDecimal actualBonusBase;
  /** 已收奖金基数（元） */
  @Excel(name = "已收奖金基数（元）")
  @ApiModelProperty("已收奖金基数（元）")
  private BigDecimal receivedBonusBase;
  /** 实收奖金（元） */
  @Excel(name = "实收奖金（元）")
  @ApiModelProperty("实收奖金（元）")
  private BigDecimal actualBonus;
  /** 已收奖金（元） */
  @Excel(name = "已收奖金（元）")
  @ApiModelProperty("已收奖金（元）")
  private BigDecimal receivedBonus;
}
