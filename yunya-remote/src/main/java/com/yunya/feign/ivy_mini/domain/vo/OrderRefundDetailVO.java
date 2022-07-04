package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/7/1 17:01
 **/
@Data
@ApiModel(description = "订单退款详情")
public class OrderRefundDetailVO {
    @ApiModelProperty(value = "订单详情")
    private OrderFrontVO orderVO;
    @ApiModelProperty(value = "订单状态（0-未收到货，1-已收到货）")
    private Byte orderStatus;
    @ApiModelProperty(value = "退款原因")
    private String returnReason;
}
