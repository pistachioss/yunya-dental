package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 医生所属欠费账单明细VO
 *
 * @author: chow
 * @date: 2020/12/10 13:42
 * @description:
 * @since: 1.0.0
 */
@ApiModel("医生所属欠费账单明细VO")
@Data
@ToString
public class DentistArrearsDetailVO implements Serializable {
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织名称 */
  @Excel(name = "开单门诊")
  @ApiModelProperty("组织名称")
  private String orgName;
  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者名称 */
  @Excel(name = "患者名称")
  @ApiModelProperty("患者名称")
  private String patientName;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String mobile;
  /** 原价合计 */
  @Excel(name = "原价合计", cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("原价合计")
  private BigDecimal originalAmount;
  /** 优惠金额 */
  @Excel(name = "优惠金额", cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("优惠金额")
  private BigDecimal privilegeAmount;
  /** 实收金额 */
  @Excel(name = "实收金额", cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("实收金额")
  private BigDecimal actualAmount;
  /** 已收金额 */
  @Excel(name = "已收金额", cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("已收金额")
  private BigDecimal receivedAmount;
  /** 剩余欠费合计 */
  @Excel(name = "剩余欠费金额", cellType = Excel.ColumnType.NUMERIC)
  @ApiModelProperty("剩余欠费金额")
  private BigDecimal remainingArrears;
}
