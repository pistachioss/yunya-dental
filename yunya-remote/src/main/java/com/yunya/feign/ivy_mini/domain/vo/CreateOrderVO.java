package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(description = "下单返回")
public class CreateOrderVO {
    @ApiModelProperty(value = "订单")
    private PayOrderVO orderVO;
    @ApiModelProperty(value = "订单项目明细")
    private List<PayOrderItemVO> itemVO;
    @ApiModelProperty(value = "收货地址")
    private PayReceiveAddressVO addressVO;
    @ApiModelProperty(value = "应付金额（实际支付金额）")
    private BigDecimal payAmount;
    @ApiModelProperty(value = "预付单信息")
    private WxPaymentVO paymentVO;
}