package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author xiangyang
 * @date 2023/7/17
 */
@Data
@ApiModel(value = "订单详情")
public class CouponOrderQuery {
    @ApiModelProperty(value = "订单id", required = true)
    @NotNull
    private Integer orderId;
}
