package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.VoucherDiscountItem;
import com.yunya.modules.discount.form.VoucherDiscountItemForm;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface VoucherDiscountItemMapper extends Mapper<VoucherDiscountItem> {

    int saveVouAndDis(List<VoucherDiscountItemForm>list);

}