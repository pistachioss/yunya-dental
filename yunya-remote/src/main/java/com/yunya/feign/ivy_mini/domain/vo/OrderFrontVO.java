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
@ApiModel(description = "我的订单列表预览")
public class OrderFrontVO {
    @ApiModelProperty(value = "订单id")
    private Integer orderId;
    @ApiModelProperty(value = "下单时间")
    private String orderDate;
    @ApiModelProperty(value = "订单状态（0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->退款中）")
    private Integer orderStatus;
    @ApiModelProperty(value = "预览图（3张）")
    private List<String> productPic;
    @ApiModelProperty("实付价")
    private BigDecimal payAmount;
    @ApiModelProperty("总价")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "产品数量（累计）")
    private Integer totalQuantity;
    @ApiModelProperty(value = "产品件数")
    private Integer productPieces;
    @ApiModelProperty(value = "产品价格（订单只有一个产品有价格）")
    private BigDecimal productPrice;
    @ApiModelProperty(value = "产品名称")
    private String productName;
}
