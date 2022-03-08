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
 * 简介: 员工看诊情况信息VO
 *
 * @author: chow
 * @date: 2020/12/7 16:11
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工看诊情况信息VO")
@Data
@ToString
public class EmployeeDiagnosisInfoVO implements Serializable {
  /** 门诊ID */
  @ApiModelProperty("门诊ID")
  private Integer orgId;
  /** 员工ID */
  @ApiModelProperty("员工ID")
  private Integer employeeId;
  /** 医生姓名 */
  @Excel(name = "医生")
  @ApiModelProperty("医生姓名")
  private String employeeName;
  /** 排班时长(分钟） */
  @Excel(name = "排班时长(分钟）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("排班时长")
  private Integer totalShiftTime;
  /** 治疗时长（分钟） */
  @Excel(name = "治疗时长（分钟）", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("治疗时长")
  private Integer totalTreatmentTime;
  /** 初诊人数 */
  @Excel(name = "初诊人数", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("初诊人数")
  private Integer firstVisitNum;
  /** 就诊人次 */
  @Excel(name = "就诊人次", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("就诊人次")
  private Integer treatVisitsTimes;
  /** 次均消费 */
  @Excel(name = "次均消费", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("次均消费")
  private BigDecimal averageConsumption;
  /** 就诊人数 */
  @Excel(name = "就诊人数", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("就诊人数")
  private Integer treatVisitsNum;
  /** 人均消费 */
  @Excel(name = "人均消费", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("人均消费")
  private BigDecimal perCapitaConsumption;
  /** 预约人次 */
  @Excel(name = "预约人次", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("预约人次")
  private Integer appointmentsNum;
  /** 到诊人次 */
  @Excel(name = "到诊人次", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("到诊人次")
  private Integer visitArrivedTimes;
  /** 改约人次 */
  @Excel(name = "改约人次", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("改约人次")
  private Integer appointmentModifyTimes;
  /** 取消预约人次 */
  @Excel(name = "取消预约人次", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("取消预约人次")
  private Integer appointmentCancelTimes;
}
