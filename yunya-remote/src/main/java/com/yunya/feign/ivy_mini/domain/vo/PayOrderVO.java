package com.yunya.feign.ivy_mini.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(description = "订单")
public class PayOrderVO{
    @ApiModelProperty(value = "订单id")
    private Integer orderId;
    @ApiModelProperty(value = "订单号")
    private String orderSn;
    @ApiModelProperty(value = "订单状态（0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->申请退款；6-退款成功；7-退款失败）")
    private Byte status;
    @ApiModelProperty(value = "支付方式（1->支付宝；2->微信）")
    private Byte payType;
    @ApiModelProperty(value = "下单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date orderDate;
    @ApiModelProperty(value = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date payDate;
    @ApiModelProperty(value = "应付金额（实际支付金额）")
    private BigDecimal payAmount;
    @ApiModelProperty(value = "配送方式（0->自提 1->配送）")
    private Byte deliveryType;
    @ApiModelProperty(value = "产品类型（0-商品 1-虚拟服务）")
    private Byte productType;
    @ApiModelProperty(value = "剩余时间")
    private String remainDate;
    @ApiModelProperty(value = "货物状态（0-未收到货，1-已收到货）")
    private Integer orderStatus;
    @ApiModelProperty(value = "退款原因")
    private String returnReason;
    @ApiModelProperty(value = "关闭时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date closeDate;
    @ApiModelProperty(value = "商家回复")
    private String mchReply;
}