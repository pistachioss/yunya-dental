package com.yunya.feign.discount.domain.bo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2020/10/11
 */
@Getter
@Setter
public class CouponItemUseBo {
	private Integer couponId;
	private Integer itemId;
	private Integer type;
	private Integer cardId;
	private Integer originCount;
	private Integer useCount;
	/**
	 * 是否可用优惠券
	 */
	private Integer usable;
	private BigDecimal packageUnitPrice;
}
