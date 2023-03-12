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
 * 简介: 账单优惠VO模型
 *
 * @author: chow
 * @date: 2020/11/25 11:09
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单优惠VO模型")
@Data
@ToString
public class BillOfDiscountDetailVO implements Serializable {
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty(value = "账单日期")
  private String orderDate;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 开单门诊ID */
  @ApiModelProperty("开单门诊ID")
  private Integer orgId;
  /** 开单门诊 */
  @Excel(name = "开单门诊")
  @ApiModelProperty("开单门诊")
  private String orgName;
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
  private BigDecimal originalAmount;
  /** 优惠金额 */
  @Excel(name = "优惠金额", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("优惠金额")
  private BigDecimal discountAmount;
  /** 应收金额 */
  @Excel(name = "应收金额", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("应收金额")
  private BigDecimal actualAmount;
  /** 优惠类型 */
  @Excel(name = "优惠类型", readConverterExp = "0=未使用优惠,1=产品优惠（仅价目表）,2=授权折扣,3=产品优惠（含商品表）")
  @ApiModelProperty("优惠类型:0-未使用优惠；1-产品优惠（仅价目表）；2-授权折扣；3-产品优惠（含商品表）")
  private Byte privilegeType;
  /** 收费人ID */
  @ApiModelProperty("收费人ID")
  private Integer payeeId;
  /** 收费人姓名 */
  @Excel(name = "收费人")
  @ApiModelProperty("收费人姓名")
  private String payeeName;
}
