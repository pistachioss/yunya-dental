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
@ApiModel(value = "划扣退费详情")
public class CouponRefundQuery {
    @ApiModelProperty(value = "订单id", required = true)
    @NotNull
    private Integer orderId;
    @ApiModelProperty(value = "礼包id", required = true)
    @NotNull
    private Integer couponId;
    @ApiModelProperty(value = "卡券id", required = true)
    @NotNull
    private Integer cardId;
}
