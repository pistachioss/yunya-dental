package com.yunya.feign.ivy_mini.domain.query;

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
@ApiModel(description = "确认产品订单")
public class ConfirmProductQuery {
    @ApiModelProperty(value = "产品类型（0-商品 1-虚拟服务）", required = true)
    @NotNull
    private Integer productType;
    @ApiModelProperty(value = "配送方式（0->自提 1->配送）")
    private Integer deliveryType;
    @ApiModelProperty(value = "价目id", required = true)
    @NotNull
    private Integer productId;
    @ApiModelProperty(value = "数量", required = true)
    @NotNull
    private Integer quantity;
}
