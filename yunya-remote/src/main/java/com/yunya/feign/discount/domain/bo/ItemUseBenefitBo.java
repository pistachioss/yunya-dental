package com.yunya.feign.discount.domain.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2020/9/14
 */
@Getter
@Setter
@ApiModel(value = "项目使用优惠")
public class ItemUseBenefitBo implements Serializable {
    @ApiModelProperty(value = "优惠券使用顺序，按照id递增")
    private Integer id;
    @ApiModelProperty(value = "记录哪一个数量使用了优惠")
    private Integer itemIndex;
    @ApiModelProperty(value = "优惠id（包含会员卡id，卡券id）")
    private Integer benefitId;
    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;
    @ApiModelProperty(value = "优惠类型（0：会员卡 1：优惠券）")
    private Integer benefitType;
    @ApiModelProperty(value = "优惠券类型（0：代金券 1：折扣券 2：兑换券 3：套餐券）")
    private Integer couponType;
    @ApiModelProperty(value = "优惠名称")
    private String benefitName;
    @ApiModelProperty(value = "优惠金额")
    private BigDecimal benefitAmount;

    public ItemUseBenefitBo() {
        this.id = 0;
    }
}
