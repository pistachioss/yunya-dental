package com.yunya.feign.ivy_mini.domain.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemBO extends ProductBO{
    /**
     * 订单id
     */
    private Integer orderId;
    /**
     * 订单号
     */
    private Integer orderSn;
    /**
     * 购买数量
     */
    private Integer productQuantity;
}