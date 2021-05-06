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
 * 简介: 对账单账单退费明细VO
 *
 * @author: chow
 * @date: 2021/1/12 14:29
 * @description:
 * @since: 1.0.0
 */
@ApiModel("对账单账单退费明细VO")
@Data
@ToString
public class StatementBillRefundDetailVO implements Serializable {
  /** 退费记录ID */
  @ApiModelProperty("退费记录ID")
  private Integer refundId;
  /** 退费日期 */
  @Excel(name = "退费日期")
  @ApiModelProperty("退费日期")
  private String refundDate;
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
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
  /** 患者手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("患者手机号")
  private String patientMobile;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生姓名 */
  @Excel(name = "医生")
  @ApiModelProperty("挂号医生姓名")
  private String regDentistName;
  /** 原价合计 */
  @Excel(name = "原价合计", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("原价合计")
  private BigDecimal originalAmount;
  /** 优惠金额 */
  @Excel(name = "优惠金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("优惠金额")
  private BigDecimal privilegeAmount;
  /** 应收金额 */
  @Excel(name = "应收金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("应收金额")
  private BigDecimal actualAmount;
  /** 实收金额 */
  @Excel(name = "实收金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("实收金额")
  private BigDecimal totalReceivedAmount;
  /** 本次退费金额 */
  @Excel(name = "本次退费金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("本次退费金额")
  private BigDecimal refundAmount;
  /** 会员卡本金 */
  @Excel(name = "会员卡本金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("会员卡本金")
  private BigDecimal memberPrincipleAmount;
  /** 会员卡赠金 */
  @Excel(name = "会员卡赠金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("会员卡赠金")
  private BigDecimal memberBonusAmount;
  /** 预付款本金 */
  @Excel(name = "预付款本金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("预付款本金")
  private BigDecimal prepaidPrincipleAmount;
  /** 预付款赠金 */
  @Excel(name = "预付款赠金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("预付款赠金")
  private BigDecimal prepaidBonusAmount;
  /** 现金 */
  @Excel(name = "现金", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("现金")
  private BigDecimal cashAmount;
  /** 支付宝 */
  @Excel(name = "支付宝", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("支付宝")
  private BigDecimal aliPayAmount;
  /** 微信 */
  @Excel(name = "微信", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("微信")
  private BigDecimal weChatAmount;
  /** 银行账户 */
  @Excel(name = "银行账户", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("银行账户")
  private BigDecimal bankAmount;
  /** 退费操作人ID */
  @ApiModelProperty("退费操作人ID")
  private Integer refundOperatorId;
  /** 退费操作人姓名 */
  @Excel(name = "退费人")
  @ApiModelProperty("退费操作人姓名")
  private String refundOperatorName;
}
