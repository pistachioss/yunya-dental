package com.yunya.feign.ivy_mini.domain.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemBO {
    /**
     * 订单id
     */
    private Integer orderId;
    /**
     * 订单号
     */
    private Integer orderSn;
    /**
     * 商品id
     */
    private Integer productId;
    /**
     * 商品号码
     */
    private String productSn;
    private String productPic;
    /**
     * 商品价格
     */
    private BigDecimal productPrice;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 购买数量
     */
    private Integer productQuantity;
    /**
     * 商品分类id
     */
    private Integer productCategoryId;
    /**
     * 商品分类名称
     */
    private String productCategoryName;
    private Integer stock;
}