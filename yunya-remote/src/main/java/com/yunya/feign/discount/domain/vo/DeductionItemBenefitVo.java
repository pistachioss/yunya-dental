package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "患者开单项目明细优惠返回")
public class DeductionItemBenefitVo extends PatientItemBenefitVo{
    @ApiModelProperty(value = "优惠划扣数量")
    private Integer quantity;
    @ApiModelProperty("套餐单价")
    private BigDecimal packageUnitPrice;
    @ApiModelProperty(value = "划扣金额")
    private BigDecimal deductionAmount;

}
