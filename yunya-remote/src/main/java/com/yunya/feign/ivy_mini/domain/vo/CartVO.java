package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
@ApiModel(description = "购物车列表")
public class CartVO {
    @ApiModelProperty(value = "产品子项")
    private List<CartItemVO> itemList;
    @ApiModelProperty(value = "购物车总价")
    private BigDecimal totalPrice;
}
