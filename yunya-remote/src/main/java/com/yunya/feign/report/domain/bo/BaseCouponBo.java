package com.yunya.feign.report.domain.bo;

import com.yunya.models.discount.DiscountCoupon;
import com.yunya.models.discount.RechargeCard;
import com.yunya.models.discount.VoucheCoupon;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * @author xiangyang
 * @date 2020/10/15
 */
@Getter
@Setter
@ToString
public class BaseCouponBo {
	private Map<Integer, RechargeCard> rechargeMap;
	private Map<Integer, VoucheCoupon> voucherMap;
	private Map<Integer, DiscountCoupon> discountMap;
	private Map<Integer, String> productTypeMap;




}
