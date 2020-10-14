package com.yunya.middletable.service.discount;

import com.yunya.middletable.dao.discount.CardMapper;
import com.yunya.middletable.dao.discount.CouponMapper;
import com.yunya.middletable.dao.discount.PackageCouponItemMapper;
import com.yunya.middletable.dao.discount.SpecialPackageCouponItemMapper;
import com.yunya.middletable.dao.discount.VoucherDiscountItemMapper;

import javax.annotation.Resource;

/**
 * @author xiangyang
 * @date 2020/10/14
 */
public class DiscountServiceImpl {
	@Resource
	private CouponMapper couponMapper;
	@Resource
	private CardMapper cardMapper;
	@Resource
	private PackageCouponItemMapper packageCouponItemMapper;
	@Resource
	private SpecialPackageCouponItemMapper specialPackageCouponItemMapper;
	@Resource
	private VoucherDiscountItemMapper voucherDiscountItemMapper;


}
