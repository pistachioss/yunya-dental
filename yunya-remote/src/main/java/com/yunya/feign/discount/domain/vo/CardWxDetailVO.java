package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/20
 * @description:
 */
@Data
@ApiModel(value = "小程序端待使用产品-产品详情")
public class CardWxDetailVO {

    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;
    /**
     * 可使用门诊
     */
    @ApiModelProperty("可使用门诊列表")
    private String useableClinic;

    @ApiModelProperty("是否可混合使用优惠 0.不可以共用 1.可以共用")
    private Integer mixable;

    @ApiModelProperty("使用方式 0:一次使用 1:多次使用")
    private Byte useWay;

    @ApiModelProperty("账单单词使用限制数量")
    private Integer limitCount;
}
