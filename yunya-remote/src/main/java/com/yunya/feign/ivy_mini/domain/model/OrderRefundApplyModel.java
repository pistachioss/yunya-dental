package com.yunya.feign.ivy_mini.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
@ApiModel(description = "订单申请退款参数 ")
public class OrderRefundApplyModel {
    @ApiModelProperty(value = "订单id", required = true)
    @NotNull
    private Integer orderId;
    @ApiModelProperty(value = "用户收货状态(0-未收到货 1-收到货)", required = true)
    @NotNull
    private Integer status;
    @ApiModelProperty(value = "退款原因", required = true)
    @NotBlank
    private String refundReason;
}
