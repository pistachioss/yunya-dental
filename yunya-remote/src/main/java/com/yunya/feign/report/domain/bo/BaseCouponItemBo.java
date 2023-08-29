package com.yunya.feign.report.domain.bo;

import com.google.common.collect.Lists;
import com.yunya.models.discount.DeductionItemPeriod;
import com.yunya.models.discount.PackageCouponItem;
import com.yunya.models.discount.SpecialPackageCouponItem;
import com.yunya.models.discount.VoucherDiscountItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author xiangyang
 * @date 2020/10/19
 */
@Getter
@Setter
public class BaseCouponItemBo {
	private List<VoucherDiscountItem> voucherDiscountItems;
	private List<PackageCouponItem> exchangeItems;
	private List<SpecialPackageCouponItem> packageItems;
    private List<DeductionItemPeriod> periods;

	public static BaseCouponItemBo getInstance() {
		BaseCouponItemBo baseCouponItemBo = new BaseCouponItemBo();
		baseCouponItemBo.setVoucherDiscountItems(Lists.newArrayList());
		baseCouponItemBo.setExchangeItems(Lists.newArrayList());
		baseCouponItemBo.setPackageItems(Lists.newArrayList());
        baseCouponItemBo.setPeriods(Lists.newArrayList());
		return baseCouponItemBo;
	}
}
