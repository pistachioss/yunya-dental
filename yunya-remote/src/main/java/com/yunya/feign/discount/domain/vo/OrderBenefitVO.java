package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author xiangyang
 * @date 2023/07/5
 */
@Data
@ApiModel(value = "订单优惠返回")
public class OrderBenefitVO {
	@ApiModelProperty(value = "其他卡券项目明细")
	private List<OrderBenefitDetailVo> itemBenefit;
    @ApiModelProperty(value = "划扣卡券项目明细")
    private List<OrderBenefitDetailVo> deductionList;
}
