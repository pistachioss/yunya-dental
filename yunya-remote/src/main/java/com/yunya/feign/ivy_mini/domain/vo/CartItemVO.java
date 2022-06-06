package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
@ApiModel(description = "购物车列表")
public class CartItemVO {
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "产品id")
    private Integer productId;
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "封面")
    private String productPic;
    @ApiModelProperty(value = "销售价格")
    private BigDecimal productPrice;
    @ApiModelProperty(value = "数量")
    private Integer quantity;
}
