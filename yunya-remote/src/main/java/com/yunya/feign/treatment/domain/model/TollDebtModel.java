package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * 简介: 收欠费参数模型
 *
 * @author: chow
 * @date: 2020/9/9 16:03
 * @description:
 * @since: 1.0.0
 */
@ApiModel("收欠费参数模型")
@Data
@ToString
public class TollDebtModel implements Serializable {

  /** 账单（开单）记录ID */
  @ApiModelProperty(value = "就诊记录ID ", required = true)
  @NotNull(message = "就诊记录ID不能为空！")
  private Integer treatmentRecordId;

  /** 普通优惠信息 */
  private GeneralDiscountModel generalDiscountModel;

  /** 授权折扣信息 */
  private AccreditDiscountModel accreditDiscountModel;

  /** 预付款账户 */
  private Set<PrepaymentAccountModel> prepaymentAccountModels;

  /** 会员卡账户 */
  private Set<MemberAccountModel> memberAccountModels;

  /** 其他支付方式 */
  private Set<PaymentModel> paymentModels;

  /** 挂帐金额 */
  @ApiModelProperty(value = "挂帐金额", required = true)
  @NotNull(message = "挂帐金额不能为空！")
  @Min(value = 0, message = "输入金额不能小于0！")
  private BigDecimal outstandingAmount;

  /** 发票信息 */
  private InvoiceModel invoiceModel;
}
