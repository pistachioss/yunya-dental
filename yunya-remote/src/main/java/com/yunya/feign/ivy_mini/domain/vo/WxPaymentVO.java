package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/6/21 15:38
 **/
@Data
@ApiModel(value="WxPayment",description="微信调起支付数据")
public class WxPaymentVO {
    @ApiModelProperty(value="小程序id")
    private String appId;
    @ApiModelProperty(value="时间戳")
    private String timeStamp;
    @ApiModelProperty(value="随机字符串")
    private String nonceStr;
    @ApiModelProperty(value="订单详情扩展字符串")
    private String _package;
    @ApiModelProperty(value="签名方式")
    private String signType;
    @ApiModelProperty(value="签名")
    private String paySign;
}
