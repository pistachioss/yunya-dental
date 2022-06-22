package com.yunya.feign.ivy_mini.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
public class CreateOrderBaseModel {
    @ApiModelProperty(value = "产品类型（0-商品 1-虚拟服务）", required = true)
    @NotNull
    private Integer productType;
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
