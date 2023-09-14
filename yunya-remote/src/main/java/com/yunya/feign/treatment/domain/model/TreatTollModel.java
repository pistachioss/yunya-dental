package com.yunya.feign.treatment.domain.model;

import com.yunya.feign.treatment.domain.query.OrderPrivilegeQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
public class TreatTollModel extends OrderPrivilegeQuery {

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
