package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：优惠信息响应模型
 *
 * @author: chenlin
 * @Description: 优惠信息响应模型
 * @Date: 2021/1/21 16:43
 * @since: 1.0.0
 */
@ApiModel("优惠信息响应模型")
@ToString
@Data
public class BaseBenefitInfoVO implements Serializable {
    @ApiModelProperty("订单详情id")
    private Integer orderDetailId;

    @ApiModelProperty("卡券id")
    private Integer cardId;

    @ApiModelProperty("销售渠道")
    private String saleChannelName;

    @ApiModelProperty("优惠金额")
    private BigDecimal benefitAmount;

    @ApiModelProperty("产品")
    private String couponName;

    @ApiModelProperty("卡号")
    private String cardNumber;
}
