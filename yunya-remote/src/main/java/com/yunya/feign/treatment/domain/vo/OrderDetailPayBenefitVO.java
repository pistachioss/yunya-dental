package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: chenlin
 * @date: 2023/8/9 14:49
 * @description: 订单明细优惠数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("订单明细优惠数据模型")
public class OrderDetailPayBenefitVO implements Serializable {

    /** 订单明细id */
    @ApiModelProperty("订单明细id")
    private Integer orderDetailId;

    /** 优惠金额 */
    @ApiModelProperty("优惠金额")
    private BigDecimal privilegeAmount;
    
    /** 数量 */
    @ApiModelProperty("数量")
    private Integer quantity;

    /** 划扣套餐价 */
    @ApiModelProperty("划扣套餐价")
    private BigDecimal packageTotalAmount;
    
    /** 补入工作量 */
    @ApiModelProperty("补入工作量")
    private BigDecimal couponWorkload;

    /** 划扣补入工作量 */
    @ApiModelProperty("划扣补入工作量")
    private BigDecimal swipeCouponWorkload;
}
