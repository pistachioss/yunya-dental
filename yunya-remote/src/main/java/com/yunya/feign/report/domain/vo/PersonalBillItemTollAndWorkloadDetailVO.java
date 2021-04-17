package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 员工个人收费项目工作量明细VO
 *
 * @author: chow
 * @date: 2021/4/14 10:57
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工个人收费项目工作量明细VO")
@Data
@ToString
public class PersonalBillItemTollAndWorkloadDetailVO implements Serializable {
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 账单日期 */
  @Excel(name = "账单日期", dateFormat = "yyyy-MM-dd HH:mm")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生姓名 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生姓名")
  private String regDentistName;
  /** 已收工作量 */
  @Excel(name = "已收工作量", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("已收工作量")
  private BigDecimal receivedWorkload;
  /** 免单工作量 */
  @Excel(name = "其中免单工作量", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("免单工作量")
  private BigDecimal freePayWorkload;
  /** 补入工作量 */
  @Excel(name = "补入工作量", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("补入工作量")
  private BigDecimal supplyWorkload;
  /** 退费工作量 */
  @Excel(name = "退费工作量", scale = 2, cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("退费工作量")
  private BigDecimal refundWorkload;
  /** 执行人ID */
  @ApiModelProperty("执行人ID")
  private Integer executorId;
  /** 执行人姓名 */
  @Excel(name = "执行人")
  @ApiModelProperty("执行人姓名")
  private String executorName;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者手机号 */
  @Excel(name = "患者手机号")
  @ApiModelProperty("患者手机号")
  private String mobile;
}
