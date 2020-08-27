package com.yunya.modules.discount.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.discount.VoucherDiscountItem;
import com.yunya.modules.discount.form.PackageCouponItemForm;
import com.yunya.modules.discount.form.SpecialPackageCouponItemForm;
import com.yunya.modules.discount.mapper.VoucherDiscountItemMapper;
import com.yunya.modules.discount.form.VoucherDiscountItemForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 杨柳絮
 * @className VoucherDiscountItemBiz
 * @description
 * @date 2020/8/20 15:01
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VoucherDiscountItemBiz extends BaseBiz<VoucherDiscountItemMapper, VoucherDiscountItem> {

   public int saveVouAndDis(List<VoucherDiscountItemForm> list){
        return mapper.saveVouAndDis(list);
    }
    public int savePackage(List<PackageCouponItemForm> list){
        return mapper.savePackage(list);
    }
    public int saveSpecial(List<SpecialPackageCouponItemForm> list){
        return mapper.saveSpecial(list);
    }
}
