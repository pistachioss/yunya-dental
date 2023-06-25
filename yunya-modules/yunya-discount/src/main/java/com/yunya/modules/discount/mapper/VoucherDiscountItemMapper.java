package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.vo.VouDisAfterOptimizationVO;
import com.yunya.models.discount.VoucherDiscountItem;
import com.yunya.modules.discount.form.PackageCouponItemForm;
import com.yunya.modules.discount.form.SpecialPackageCouponItemForm;
import com.yunya.modules.discount.form.VoucherDiscountItemForm;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface VoucherDiscountItemMapper extends Mapper<VoucherDiscountItem> {

    int saveVouAndDis(List<VoucherDiscountItemForm>list);

    int savePackage(List<PackageCouponItemForm> list);

    int saveSpecial(List<SpecialPackageCouponItemForm> list);

    List<VouDisAfterOptimizationVO> findListAfterOptimization(VoucherDiscountItem voucherDiscountItem);

    int saveDeductionPeriod(List<SpecialPackageCouponItemForm> list);
}