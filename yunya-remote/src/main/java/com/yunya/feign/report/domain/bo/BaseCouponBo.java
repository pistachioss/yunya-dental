package com.yunya.feign.report.domain.bo;

import com.google.common.collect.Maps;
import com.yunya.models.discount.DiscountCoupon;
import com.yunya.models.discount.PackageCoupon;
import com.yunya.models.discount.RechargeCard;
import com.yunya.models.discount.SpecialPackageCoupon;
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
	private Map<Integer, PackageCoupon> packageCouponMap;
	private Map<Integer, SpecialPackageCoupon> specialPackageCouponMap;
	private Map<Integer, String> productTypeMap;

	public static BaseCouponBo getInstance() {
		BaseCouponBo baseCouponBo = new BaseCouponBo();
		baseCouponBo.setRechargeMap(Maps.newHashMap());
		baseCouponBo.setVoucherMap(Maps.newHashMap());
		baseCouponBo.setDiscountMap(Maps.newHashMap());
		baseCouponBo.setPackageCouponMap(Maps.newHashMap());
		baseCouponBo.setSpecialPackageCouponMap(Maps.newHashMap());
		baseCouponBo.setProductTypeMap(Maps.newHashMap());
		return baseCouponBo;
	}



}
