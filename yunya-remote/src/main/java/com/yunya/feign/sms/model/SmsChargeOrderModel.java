package com.yunya.feign.sms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：短信充值订单添加模型
 *
 * @author: chenlin
 * @Description: 短信充值订单添加模型
 * @Date: 2020/12/12 14:13
 * @since: 1.0.0
 */
@ApiModel("短信充值订单添加模型")
@ToString
@Data
public class SmsChargeOrderModel implements Serializable {

    /**
     * 组织id（门诊或公司）
     */
    @ApiModelProperty("组织id（门诊或公司）")
    private Integer orgId;

    /**
     * 短信条数
     */
    @ApiModelProperty(value = "短信条数", required = true)
    @NotNull
    private Integer smsNum;

    /**
     * 短信价格
     */
    @ApiModelProperty(value = "短信价格", required = true)
    @NotNull
    private BigDecimal price;

    /**
     * 采商订单号
     */
    @ApiModelProperty("采商订单号")
    private String cbOrderNo;

    /**
     * 消费者id
     */
    @ApiModelProperty("消费者id")
    private String buyerId;

    /**
     * 付款方式：ALIPAY-支付宝；WECHAT-微信
     */
    @ApiModelProperty("付款方式：ALIPAY-支付宝；WECHAT-微信")
    private String paymentChannel;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称", required = true)
    @NotBlank
    private String smsGoods;

    /**
     * 订单状态：0-等待付款，1-付款成功，2-付款失败，3-已关闭
     */
    @ApiModelProperty("订单状态：0-等待付款，1-付款成功，2-付款失败，3-已关闭")
    private Byte orderStatus;
}
