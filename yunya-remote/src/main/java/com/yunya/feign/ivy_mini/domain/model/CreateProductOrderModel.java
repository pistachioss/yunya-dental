package com.yunya.feign.ivy_mini.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
@ApiModel(description = "创建产品订单参数")
public class CreateProductOrderModel {
    @ApiModelProperty(value = "产品类型（0-商品 1-虚拟服务）", required = true)
    @NotNull
    private Integer productType;
    @ApiModelProperty(value = "价目id", required = true)
    @NotNull
    private Integer productId;
    @ApiModelProperty(value = "数量", required = true)
    @NotNull
    private Integer quantity;
    @ApiModelProperty(value = "收货地址ID", required = true)
    @NotNull
    private Integer fansReceiveAddressId;
    @ApiModelProperty("支付方式（1->支付宝；2->微信）")
    private Integer payType;
    @ApiModelProperty("配送方式（0->自提 1->配送）")
    private Integer deliveryType;
    @ApiModelProperty("备注")
    private String remark;
}
