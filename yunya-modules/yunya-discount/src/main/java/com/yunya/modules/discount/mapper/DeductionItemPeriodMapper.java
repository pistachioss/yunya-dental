package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.DeductionItemPeriod;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface DeductionItemPeriodMapper extends Mapper<DeductionItemPeriod> {
    List<DeductionItemPeriod> selectItem(Collection<Integer> couponIds, Date date);
}