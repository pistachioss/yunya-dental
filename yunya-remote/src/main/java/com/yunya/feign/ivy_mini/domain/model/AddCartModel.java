package com.yunya.feign.ivy_mini.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
@ApiModel(description = "添加购物车参数")
public class AddCartModel {
    @ApiModelProperty(value = "产品id", required = true)
    @NotNull
    private Integer productId;
    @ApiModelProperty(value = "数量", required = true)
    @NotNull
    private Integer quantity;
    @ApiModelProperty(value = "产品名称", required = true)
    @NotBlank
    private String productName;
    @ApiModelProperty(value = "产品价格", required = true)
    @NotNull
    private BigDecimal productPrice;
    @ApiModelProperty(value = "商品分类", required = true)
    @NotNull
    private Integer productCategoryId;
    @ApiModelProperty(value = "商品主图", required = true)
    @NotBlank
    private String productPic;
}
