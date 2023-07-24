package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2020/9/14
 */
@Data
@ApiModel(value = "项目使用优惠")
public class ItemUseBenefitVo implements Serializable {
    @ApiModelProperty(value = "优惠id（包含会员卡id，卡券id）")
    private Integer benefitId;
    @ApiModelProperty(value = "优惠类型（0：会员卡 1：优惠券 2：授权折扣）")
    private Integer benefitType;
    @ApiModelProperty(value = "优惠券类型（0：代金券 1：折扣券 2：兑换券 3：套餐券 5：划扣券 99：会员卡）")
    private Integer couponType;
    @ApiModelProperty(value = "优惠券名称")
    private String benefitName;
    @ApiModelProperty(value = "优惠金额")
    private BigDecimal benefitAmount;
    @ApiModelProperty("会员卡号")
    private String cardNumber;

}
