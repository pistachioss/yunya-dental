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
@ApiModel(description = "创建虚拟订单参数")
public class CreateVirtualOrderModel {
    @ApiModelProperty(value = "价目id", required = true)
    @NotNull
    private Integer productId;
    @ApiModelProperty(value = "数量", required = true)
    @NotNull
    private Integer quantity;
    @ApiModelProperty(value = "商品分类", required = true)
    @NotNull
    private Integer productCategoryId;
    @ApiModelProperty("支付方式（1->支付宝；2->微信）")
    private Byte payType;
    @ApiModelProperty("备注")
    private String remark;
}
