package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "订单收货地址")
public class PayReceiveAddressVO {
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "收货人")
    private String receiverName;
    @ApiModelProperty(value = "收货人电话")
    private String receiverPhone;
    @ApiModelProperty(value = "详细地址(街道)")
    private String detailAddress;
    @ApiModelProperty(value = "配送方式（0->自提 1->配送）")
    private Byte deliveryType;
}