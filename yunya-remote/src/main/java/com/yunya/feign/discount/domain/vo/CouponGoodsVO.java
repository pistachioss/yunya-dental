package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "礼包商品VO类")
public class CouponGoodsVO {
    @ApiModelProperty("礼包id")
    private Integer id;
    @ApiModelProperty("礼包名称")
    private String name;
    @ApiModelProperty("原价")
    private BigDecimal price;
    @ApiModelProperty("套餐单价")
    private BigDecimal packageUnitPrice;
    @ApiModelProperty("分类id")
    private Integer categoryId;
    @ApiModelProperty("分类名称")
    private String categoryName;
}
