package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/11/5
 */
@Data
@ApiModel(value = "订单优惠明细返回")
public class OrderBenefitDetailVo {
	@ApiModelProperty(value = "订单明细id")
	private Integer orderDetailId;
	@ApiModelProperty(value = "项目优惠金额")
	private BigDecimal itemBenefitAmount;
	@ApiModelProperty(value = "项目使用优惠明细")
	private List<ItemUseBenefitVo> itemBenefitList;
}
