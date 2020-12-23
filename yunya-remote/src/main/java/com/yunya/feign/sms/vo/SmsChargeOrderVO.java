package com.yunya.feign.sms.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简介：短信充值订单响应模型
 *
 * @author: chenlin
 * @Description: 短信充值订单响应模型
 * @Date: 2020/12/12 13:41
 * @since: 1.0.0
 */
@ApiModel("短信充值订单响应模型")
@ToString
@Data
public class SmsChargeOrderVO implements Serializable {
    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    private Integer id;

    /**
     * 组织id（门诊或公司）
     */
    @ApiModelProperty("组织id（门诊或公司）")
    private Integer orgId;

    /**
     * 短信条数
     */
    @ApiModelProperty("短信条数")
    private Integer smsNum;

    /**
     * 短信价格
     */
    @ApiModelProperty("短信价格")
    private BigDecimal price;

    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String orderNo;

    /**
     * 采商订单号
     */
    @ApiModelProperty("采商订单号")
    private String cbOrderNo;


    /**
     * 支付宝或微信的支付交易订单号
     */
    @ApiModelProperty("支付宝或微信的支付交易订单号")
    private String outOrderNo;

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
    @ApiModelProperty("商品名称")
    private String smsGoods;

    /**
     * 订单状态：0-等待付款，1-付款成功，2-付款失败，3-已关闭
     */
    @ApiModelProperty("订单状态：0-等待付款，1-付款成功，2-付款失败，3-已关闭")
    private Byte orderStatus;

    /**
     * 充值人id
     */
    @ApiModelProperty("充值人id")
    private Integer crtId;

    /**
     * 充值人
     */
    @ApiModelProperty("充值人")
    private String crtUser;

    @ApiModelProperty
    private Date crtTime;

    /**
     * 充值时间
     */
    @ApiModelProperty("充值时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date payTime;

    /**
     * 充值二维码
     */
    @ApiModelProperty("充值二维码")
    private String qrcode;
}
