package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/9/14
 */
@Data
@ApiModel(value = "患者订单优惠返回")
public class PatientOrderBenefitVo {
    @ApiModelProperty(value = "优惠总额")
    private BigDecimal benefitTotalAmount;
    @ApiModelProperty(value = "项目使用优惠")
    private List<PatientItemBenefitVo> itemList;
    @ApiModelProperty(value = "划扣项目使用优惠")
    private List<DeductionItemBenefitVo> deductionList;
}
