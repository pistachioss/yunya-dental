package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 对账单患者卡（会员卡、预付款充值信息VO）
 *
 * @author: chow
 * @date: 2021/1/9 13:56
 * @description:
 * @since: 1.0.0
 */
@ApiModel("对账单患者卡（会员卡、预付款充值信息VO）")
@Data
@ToString
public class StatementPatientCardRechargeDetailVO implements Serializable {
  /** 充值记录ID */
  @ApiModelProperty("充值记录ID")
  private Integer rechargeRecordId;
  /** 充值日期 */
  @ApiModelProperty("充值日期")
  private String rechargeDate;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 手机号 */
  @ApiModelProperty("手机号")
  private String patientMobile;
  /** 会员卡或预付卡卡号 */
  @ApiModelProperty("会员卡或预付卡卡号")
  private String patientCardNumber;
  /** 充值本金金额 */
  @ApiModelProperty("充值本金金额")
  private BigDecimal rechargePrincipleAmount;
  /** 充值赠金金额 */
  @ApiModelProperty("充值赠金金额")
  private BigDecimal rechargeBonusAmount;
  /** 会员卡或预付卡余额（含赠金） */
  @ApiModelProperty("会员卡或预付卡余额（含赠金）")
  private BigDecimal totalAmount;
  /** 充值人ID */
  @ApiModelProperty("充值人ID")
  private Integer rechargeOperatorId;
  /** 充值人姓名 */
  @ApiModelProperty("充值人ID")
  private String rechargeOperatorName;
  /** 支付方式列表 */
  private List<StatementPaymentVO> statementPayments;
}
