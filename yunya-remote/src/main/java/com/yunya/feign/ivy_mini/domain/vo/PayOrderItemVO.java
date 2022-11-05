package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(description = "订单项目明细返回")
public class PayOrderItemVO {
    @ApiModelProperty(value = "产品id")
    private Integer productId;
    @ApiModelProperty(value = "产品图片")
    private String productPic;
    @ApiModelProperty(value = "产品价格")
    private BigDecimal productPrice;
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "产品数量")
    private Integer productQuantity;
    @ApiModelProperty(value = "库存是否足够")
    private boolean isStock;
    @ApiModelProperty(value = "产品类型（0-商品 1-虚拟服务）")
    private Integer productType;
}