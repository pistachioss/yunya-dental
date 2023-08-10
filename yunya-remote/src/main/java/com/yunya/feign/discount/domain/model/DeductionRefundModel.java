package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author xiangyang
 * @date 2023/8/29
 */
@Data
@ApiModel(value = "划扣卡券退费对象")
public class DeductionRefundModel {
    @ApiModelProperty(value = "卡券id", required = true)
    @NotNull
    private Integer cardId;
    @ApiModelProperty(value = "礼包id", required = true)
    @NotNull
    private Integer couponId;
    @ApiModelProperty(value = "订单id", required = true)
    @NotNull
    private Integer orderId;
    @ApiModelProperty(value = "订单明id", required = true)
    @NotNull
    private Integer orderDetailId;
    @ApiModelProperty(value = "退款金额", required = true)
    @NotNull
    private BigDecimal refundAmount;
    /** 退费会员账户信息 */
    @ApiModelProperty("退费会员账户信息")
    private CardMemberRefundModel memberRefundModel;
    /** 退费预付款账户信息 */
    @ApiModelProperty("退费预付款账户信息")
    private List<CardPrepaymentRefundModel> prepaymentRefundModel;
    /** 其他退费入账方式信息 */
    @ApiModelProperty("其他退费入账方式信息")
    private List<CardPaymentModel> refundPaymentModels;
    /** 退费原因 */
    @ApiModelProperty("退费原因")
    @Size(max = 1000, message = "最多可输入1000个字符！")
    private String refundReason;
    /** 退附件列表 */
    @ApiModelProperty("退费附件列表")
    private List<String> refundAnnex;
}
