package com.yunya.feign.discount.domain.bo;

import com.google.common.collect.Lists;
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
public class OrderItemUseBo {
	private Integer orderDetailId;
	private Integer itemId;
	private Integer quantity;
	//应收原价
	private BigDecimal receivableAmount;
	//0-价目表；1-商品
	private Integer type;
	private BigDecimal benefitDiscountRate;
	//订单优惠金额
	private BigDecimal benefitAmount;
	//上一个使用的优惠券是否共用
	private Integer preMixAble;
	private List<ItemUseBenefitBo> itemUseBenefitBos;
	private List<OrderItemChangeBo> changeBos;

	public static OrderItemUseBo getInstance(Integer quantity) {
		OrderItemUseBo bo = new OrderItemUseBo();
		bo.setItemUseBenefitBos(Lists.newArrayList());
		bo.setChangeBos(initChangeBos(quantity));
		bo.setPreMixAble(1);
		return bo;
	}

	private static List<OrderItemChangeBo> initChangeBos(Integer quantity) {
		List<OrderItemChangeBo> changeBos = Lists.newArrayListWithCapacity(quantity);
		for (int i = 1; i <= quantity; i++) {
			changeBos.add(OrderItemChangeBo.getInstance(i));
		}
		return changeBos;
	}
}
