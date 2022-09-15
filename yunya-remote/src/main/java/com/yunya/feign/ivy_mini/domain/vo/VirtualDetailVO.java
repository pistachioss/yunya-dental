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
@ApiModel(description = "虚拟服务详情返回")
public class VirtualDetailVO {
    @ApiModelProperty(value = "产品id")
    private Integer productId;
    @ApiModelProperty(value = "分类id")
    private Integer categoryId;
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "商品图片")
    private List<String> productPics;
    @ApiModelProperty(value = "销售价格")
    private BigDecimal productPrice;
    @ApiModelProperty(value = "库存")
    private Integer stock;
    @ApiModelProperty(value = "商品详情")
    private List<String> detailHtml;
    @ApiModelProperty(value = "产品类型（0-商品 1-虚拟服务）")
    private Integer productType;
}
