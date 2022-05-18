package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/18
 * @description:
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "订单详情VO")
public class OrderWechatDetailVO {
    @ApiModelProperty(value = "id")
    private Integer id;
    /**
     * 微信用户id
     */
    @ApiModelProperty(value = "微信用户id")
    private Integer fansId;
    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private String orderSn;
    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    private Date crtTIme;
    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     */
    @ApiModelProperty(value = "单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单")
    private Byte status;
    /**
     * 物流公司(配送方式)
     */
    @ApiModelProperty(value = "物流公司(配送方式)")
    private String deliveryCompany;
    @ApiModelProperty(value = "收货信息")
    private String receivingInformation;

    @ApiModelProperty(value = "商品列表")
    private List<OrderItemVO> topGradeList;

    /**
     * 应付金额（实际支付金额）
     */
    @ApiModelProperty(value = "应付金额（实际支付金额）")
    private BigDecimal payAmount;

    @ApiModelProperty(value = "物流单号")
    private String deliverySn;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "订单备注")
    private String remark;

}
