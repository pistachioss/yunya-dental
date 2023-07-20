package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @author: chenlin
 * @date: 2023/7/19 11:22
 * @description: 订单项目划扣明细
 * @since: 1.0.0
 */
@ApiModel("订单项目划扣明细")
public class OrderDetailSwipeVO extends OrderDetailVO {
    
    /** 礼包id */
    @ApiModelProperty("礼包id")
    private Integer couponId;

    /** 所用划扣卡名称 */
    @ApiModelProperty("所用划扣卡名称")
    private String couponName;
    
    /** 卡券id */
    @ApiModelProperty("卡券id")
    private Integer cardId;

    /** 套餐单价 */
    @ApiModelProperty("套餐单价")
    private BigDecimal packageUnitPrice;

    /** 数量 */
    @ApiModelProperty("划扣数量")
    private Integer count;

    /** 划扣金额 */
    @ApiModelProperty("划扣金额")
    private BigDecimal saleAmount;
    
    /** 单个补入工作量 */
    @ApiModelProperty("单个划扣补入工作量")
    private BigDecimal workload;

    /** 原价 */
    @ApiModelProperty("划扣原价")
    private BigDecimal price;
}
