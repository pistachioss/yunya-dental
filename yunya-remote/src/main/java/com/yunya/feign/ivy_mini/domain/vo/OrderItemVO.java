package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/18
 * @description:
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "订单管理详情-商品列表VO")
public class OrderItemVO {

    /**
     * 商品价格
     */
    @ApiModelProperty(value =  "商品价格")
    private BigDecimal productPrice;

    /**
     * 商品名称
     */
    @ApiModelProperty(value =  "商品名称")
    private String productName;

    /**
     * 购买数量
     */
    @ApiModelProperty(value =  "购买数量")
    private Integer productQuantity;

}
