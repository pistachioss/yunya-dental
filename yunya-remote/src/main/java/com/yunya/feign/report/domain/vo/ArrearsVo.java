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
 * 简介:欠费查询QueryForm
 *
 * @author: WY
 * @date: 2020/10/28 10:57
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("欠费查询Vo")
public class ArrearsVo implements Serializable {

  /** 账单id */
  @ApiModelProperty(value = "账单id")
  private Integer billId;

  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;

  /** 挂号医生 */
  @Excel(name = "挂号医生")
  @ApiModelProperty(value = "挂号医生")
  private String employeeName;

  /** 开单日期 */
  @ApiModelProperty(value = "订单日期")
  private String orderDate;

  /** 订单编号 */
  @ApiModelProperty("订单编号")
  private String orderNum;

  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;

  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty(value = "账单编号")
  private String billNum;

  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;

  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty(value = "患者姓名")
  private String name;

  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty(value = "手机号")
  private String mobile;

  /** 原价合计 */
  @Excel(name = "原价合计", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty(value = "原价合计")
  private BigDecimal originalPrice;

  /** 优惠金额 */
  @Excel(name = "优惠金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty(value = "优惠金额")
  private BigDecimal privilegeAmount;

  /** 应收金额 */
  @Excel(name = "应收金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty(value = "应收金额")
  private BigDecimal actualAmount;

  /** 实收金额 */
  @Excel(name = "实收金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty(value = "实收金额")
  private BigDecimal receivedAmount;

  /** 剩余欠费金额 */
  @Excel(name = "剩余欠费金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty(value = "剩余欠费金额")
  private BigDecimal debtAmount;
}
