package com.yunya.feign.discount.domain.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2020/11/10
 */
@Data
public class OrderItemChangeBo {
	private Integer index;
	//已优惠金额
	private BigDecimal discountedAmount;

	public static OrderItemChangeBo getInstance(Integer index) {
		OrderItemChangeBo bo = new OrderItemChangeBo();
		bo.setIndex(index);
		bo.setDiscountedAmount(BigDecimal.ZERO);
		return bo;
	}
}
