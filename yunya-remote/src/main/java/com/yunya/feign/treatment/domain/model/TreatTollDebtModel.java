package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * 简介: 收欠费参数添加模型
 *
 * @author: chow
 * @date: 2023/7/04 09:43
 * @description:
 * @since: 1.0.0
 */
@ApiModel("收欠费参数添加模型")
@Data
@ToString
public class TreatTollDebtModel implements Serializable {

  /** 账单（开单）记录ID */
  @ApiModelProperty(value = "开单记录ID ", required = true)
  @NotNull(message = "开单记录ID不能为空！")
  private Integer orderRecordId;
  
  /** 折扣方式（0-不使用优惠；1-优惠；）*/
  @ApiModelProperty(value = "折扣类型（0-不使用优惠；1-优惠；）")
  private Byte discountType = 0;

  /** 普通优惠信息 */
  @ApiModelProperty("普通优惠信息")
  private GeneralDiscountModel generalDiscountModel;

  /** 预付款账户 */
  @ApiModelProperty("预付款账户")
  private Set<PrepaymentAccountModel> prepaymentAccountModels;

  /** 会员卡账户 */
  private Set<MemberAccountModel> memberAccountModels;

  /** 其他支付方式 */
  @ApiModelProperty("其他支付方式")
  private Set<PaymentModel> paymentModels;

  /** 挂帐金额 */
  @ApiModelProperty(value = "挂帐金额", required = true)
  @NotNull(message = "挂帐金额不能为空！")
  private BigDecimal outstandingAmount;

  /** 发票信息 */
  @ApiModelProperty("发票信息")
  @Valid private InvoiceModel invoiceModel;
}
