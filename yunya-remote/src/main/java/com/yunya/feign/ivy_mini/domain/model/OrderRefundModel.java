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
@ApiModel(description = "订单退款参数 ")
public class OrderRefundModel {
    @ApiModelProperty(value = "订单id", required = true)
    @NotNull
    private Integer orderId;
    @ApiModelProperty(value = "客服审核状态（1->退款；2->拒绝）", required = true)
    @NotNull
    private Integer status;
    @ApiModelProperty(value = "拒绝原因")
    private String rejectReason;
}
