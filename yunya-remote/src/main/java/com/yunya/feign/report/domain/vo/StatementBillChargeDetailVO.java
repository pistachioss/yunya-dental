package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 对账单账单收费明细VO
 *
 * @author: chow
 * @date: 2020/12/17 16:07
 * @description:
 * @since: 1.0.0
 */
@ApiModel("对账单账单收费明细VO")
@Data
@ToString
public class StatementBillChargeDetailVO implements Serializable {
  /** 账单收费记录ID */
  @ApiModelProperty("账单收费记录ID")
  private Integer billPayId;
  /** 收费日期 */
  @Excel(name = "收费日期")
  @ApiModelProperty("收费（收欠费）日期")
  private String payeeDate;
  /** 收费诊所ID */
  @ApiModelProperty("收费诊所ID")
  private Integer payeeOrgId;
  /** 收费诊所名称 */
  @Excel(name = "代收门诊")
  @ApiModelProperty("代收门诊")
  private String payeeOrgName;
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 开单门诊ID */
  @ApiModelProperty("开单门诊ID")
  private Integer billOrgId;
  /** 开单门诊名称 */
  @Excel(name = "开单门诊")
  @ApiModelProperty("开单门诊名称")
  private String billOrgName;
  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String patientMobile;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生")
  private String regDentistName;
  /** 原价合计 */
  @Excel(name = "原价合计", scale = 2)
  @ApiModelProperty("原价合计")
  private BigDecimal originalAmount;
  /** 优惠金额 */
  @Excel(name = "优惠金额", scale = 2)
  @ApiModelProperty("优惠金额")
  private BigDecimal privilegeAmount;
  /** 实收金额 */
  @Excel(name = "实收金额", scale = 2)
  @ApiModelProperty("实收金额")
  private BigDecimal actualAmount;
  /** 已收金额 */
  @Excel(name = "已收金额", scale = 2)
  @ApiModelProperty("已收金额")
  private BigDecimal totalReceivedAmount;
  /** 本次收费金额 */
  @Excel(name = "本次收费金额", scale = 2)
  @ApiModelProperty("本次收费金额")
  private BigDecimal receivedAmount;
  /** 收费人ID */
  @ApiModelProperty("收费人ID")
  private Integer payeeId;
  /** 收费人姓名 */
  @Excel(name = "收费人")
  @ApiModelProperty("收费人姓名")
  private String payeeName;
  /** 支付方式列表 */
  private List<StatementPaymentVO> statementPayments;
}
