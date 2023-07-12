package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

/**
 * @author: xy
 * @date 20232/6/28
 **/
@Data
@ApiModel(description = "创建划扣收费")
public class CouponBillModel {
    /** 账单（开单）记录ID */
    @ApiModelProperty(value = "订单ID ", required = true)
    @NotNull
    private Integer orderId;
    /** 预付款账户 */
    @ApiModelProperty("预付款账户")
    private Set<CardPrepaymentModel> prepaymentAccountModels;
    /** 会员卡账户 */
    private Set<CardMemberModel> memberAccountModels;
    /** 其他支付方式 */
    @ApiModelProperty("其他支付方式")
    private Set<CardPaymentModel> paymentModels;
    /** 挂帐金额 */
    @ApiModelProperty(value = "挂帐金额", required = true)
    @NotNull(message = "挂帐金额不能为空！")
    private BigDecimal outstandingAmount;
    /** 发票信息 */
    @ApiModelProperty("发票信息")
    @Valid
    private CardInvoiceModel invoiceModel;

}
