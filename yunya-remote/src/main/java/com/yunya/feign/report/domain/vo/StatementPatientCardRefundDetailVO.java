package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 对账单患者储值卡（会员卡、预付款）退费明细VO
 *
 * @author: chow
 * @date: 2021/1/12 19:31
 * @description:
 * @since: 1.0.0
 */
@ApiModel("对账单患者储值卡（会员卡、预付款）退费明细VO")
@Data
@ToString
public class StatementPatientCardRefundDetailVO implements Serializable {
  /** 退费记录ID */
  @ApiModelProperty("退费记录ID")
  private Integer refundRecordId;
  /** 充值日期 */
  @Excel(name = "充值日期")
  @ApiModelProperty("充值日期")
  private String refundDate;
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
  /** 患者储值卡（会员卡or预付卡）ID */
  @ApiModelProperty("患者储值卡（会员卡or预付卡）ID")
  private Integer patientCardId;
  /** 会员卡或预付卡卡号 */
  @Excel(name = "卡号")
  @ApiModelProperty("会员卡或预付卡卡号")
  private String patientCardNumber;
  /** 卡类型名称 */
  @Excel(name = "卡类型名称")
  @ApiModelProperty("卡类型名称")
  private String cardTypeName;
  /** 会员卡或预付卡余额（含赠金） */
  @Excel(name = "会员卡或预付卡余额（含赠金）", scale = 2)
  @ApiModelProperty("剩余余额（含赠金）")
  private BigDecimal restTotalAmount;
  /** 充值本金金额 */
  @Excel(name = "退本金金额", scale = 2)
  @ApiModelProperty("退本金金额")
  private BigDecimal refundPrincipleAmount;
  /** 充值赠金金额 */
  @Excel(name = "退赠送金额", scale = 2)
  @ApiModelProperty("退赠送金额")
  private BigDecimal refundBonusAmount;
  /** 现金 */
  @Excel(name = "现金", scale = 2)
  @ApiModelProperty("现金")
  private BigDecimal cashAmount;
  /** 支付宝 */
  @Excel(name = "支付宝", scale = 2)
  @ApiModelProperty("支付宝")
  private BigDecimal aliPayAmount;
  /** 微信 */
  @Excel(name = "微信", scale = 2)
  @ApiModelProperty("微信")
  private BigDecimal weChatAmount;
  /** 银行账户 */
  @Excel(name = "银行账户", scale = 2)
  @ApiModelProperty("银行账户")
  private BigDecimal bankAmount;
  /** 充值人ID */
  @ApiModelProperty("退费操作人ID")
  private Integer refundOperatorId;
  /** 充值人姓名 */
  @Excel(name = "退费人")
  @ApiModelProperty("退费人姓名")
  private String refundOperatorName;
}
