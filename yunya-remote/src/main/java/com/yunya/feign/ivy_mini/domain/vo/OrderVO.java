package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/17
 * @description:
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "订单管理VO")
public class OrderVO {

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "收货人姓名")
    private String receiverName;
    @ApiModelProperty(value = "收货人电话")
    private String receiverPhone;
    /**
     * 微信用户id
     */
    @ApiModelProperty(value = "微信用户id")
    private Integer fansId;
    @ApiModelProperty("openID")
    private String openId;
    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private String orderSn;


    /**
     * 应付金额（实际支付金额）
     */
    @ApiModelProperty(value = "应付金额（实际支付金额）")
    private BigDecimal payAmount;


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

    /**
     * 确认收货状态：0->未确认；1->已确认
     */
    @ApiModelProperty(value = "确认收货状态：0->未确认；1->已确认")
    private Byte confirmStatus;
    @ApiModelProperty(value = "商品列表")
    private String topGradeList;
    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    private Date crtTIme;
    @ApiModelProperty(value = "收货信息")
    private String receivingInformation;


}
