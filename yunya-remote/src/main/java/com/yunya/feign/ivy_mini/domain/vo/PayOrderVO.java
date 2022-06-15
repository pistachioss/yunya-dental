package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "订单")
public class PayOrderVO {
    @ApiModelProperty(value = "订单id")
    private Integer orderId;
    @ApiModelProperty(value = "订单号")
    private Integer orderSn;
    @ApiModelProperty(value = "支付方式（1->支付宝；2->微信）")
    private Byte payType;
    @ApiModelProperty(value = "下单时间")
    private Date orderDate;
}