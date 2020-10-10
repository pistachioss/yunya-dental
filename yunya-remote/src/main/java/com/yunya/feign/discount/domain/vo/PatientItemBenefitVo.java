package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/9/14
 */
@Getter
@Setter
@ApiModel(value = "患者开单项目明细优惠返回")
public class PatientItemBenefitVo {
    @ApiModelProperty(value = "订单明细id")
    private Integer orderDetailId;
    @ApiModelProperty(value = "项目类型（0-价目表；1-商品；）")
    private Integer type;
    @ApiModelProperty(value = "项目id")
    private Integer itemId;
    @ApiModelProperty(value = "折扣率")
    private BigDecimal benefitDiscountRate;
    @ApiModelProperty(value = "实收金额")
    private BigDecimal actualAmount;
    @ApiModelProperty(value = "项目使用优惠明细")
    private List<ItemUseBenefitVo> itemBenefitList;
}
