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
 * 简介: 开单记录VO模型
 *
 * @author: chow
 * @date: 2020/10/27 10:30
 * @description:
 * @since: 1.0.0
 */
@ApiModel("数据记录-账单记录-开单记录VO模型")
@Data
@ToString
public class BillOfOrderRecordVO implements Serializable {
  /** 开单日期 */
  @Excel(name = "订单日期")
  @ApiModelProperty("订单日期")
  private String orderDate;
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 订单编号 */
  @Excel(name = "订单编号")
  @ApiModelProperty("订单编号")
  private String orderNum;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 性别（0-男；1-女） */
  @ApiModelProperty("性别（0-男；1-女)")
  private Byte gender;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生姓名 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生姓名")
  private String regDentistName;
  /** 原价合计 */
  @Excel(name = "原价合计", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("原价合计")
  private BigDecimal orderAmount;
  /** 优惠金额 */
  @Excel(name = "优惠金额", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("优惠金额")
  private BigDecimal privilegeAmount;
  /** 应收金额 */
  @Excel(name = "应收金额", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("应收金额")
  private BigDecimal actualAmount;
  /** 实收金额 */
  @Excel(name = "实收金额", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("实收金额")
  private BigDecimal receivedAmount;
  /** 收费状态 */
  @Excel(name = "收费状态", readConverterExp = "0=未收费,1=已收费")
  @ApiModelProperty("收费状态0-为收费，1-已收费")
  private Byte chargeStatus;
  /** 开单人ID */
  @ApiModelProperty("开单人ID")
  private Integer billerId;
  /** 开单人姓名 */
  @Excel(name = "开单人")
  @ApiModelProperty("开单人姓名")
  private String biller;
}
