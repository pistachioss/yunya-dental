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
@ApiModel(description = "创建产品订单参数（商品详情）")
public class CreateProductOrderModel extends CreateOrderBaseModel{
    @ApiModelProperty(value = "价目id", required = true)
    @NotNull
    private Integer productId;
    @ApiModelProperty(value = "数量", required = true)
    @NotNull
    private Integer quantity;
    @ApiModelProperty(value = "收货地址ID")
    private Integer fansReceiveAddressId;

}
