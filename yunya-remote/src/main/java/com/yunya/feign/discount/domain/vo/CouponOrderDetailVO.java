package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "划扣订单详情")
public class CouponOrderDetailVO {
    @ApiModelProperty("礼包id")
    private Integer couponId;
    @ApiModelProperty("订单详情id")
    private Integer orderDetailId;
    @ApiModelProperty("礼包名称")
    private String couponName;
    @ApiModelProperty("原价")
    private BigDecimal price;
    @ApiModelProperty("套餐价")
    private BigDecimal packagePrice;
    @ApiModelProperty("售卖单价")
    private BigDecimal saleAmount;
    @ApiModelProperty("应收金额")
    private BigDecimal receivableAmount;
    @ApiModelProperty(value = "数量")
    private Integer quantity;
    @ApiModelProperty(value = "销售渠道")
    private String channelName;
    @ApiModelProperty(value = "操作人")
    private String executorName;
    @ApiModelProperty(value = "咨询师")
    private String consulterName;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "咨询师id")
    private Integer executorId;
    @ApiModelProperty(value = "操作人id")
    private Integer consulterId;
}
