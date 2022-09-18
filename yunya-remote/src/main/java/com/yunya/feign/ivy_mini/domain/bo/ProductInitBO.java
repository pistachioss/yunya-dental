package com.yunya.feign.ivy_mini.domain.bo;

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
@ApiModel(description = "热销产品返回")
public class ProductInitBO {

    @ApiModelProperty(value = "产品id")
    private Integer productId;

    @ApiModelProperty(value = "产品类型（0-商品 1-虚拟服务）")
    private Integer productType;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "封面")
    private String productPic;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "销售价格")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "已售数量")
    private Integer soldQuantity;
}
