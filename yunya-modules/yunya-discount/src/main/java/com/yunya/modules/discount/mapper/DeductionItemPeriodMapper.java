package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.DeductionItemPeriod;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface DeductionItemPeriodMapper extends Mapper<DeductionItemPeriod> {
    List<DeductionItemPeriod> selectItem(@Param("couponIds") Collection<Integer> couponIds, @Param("date") Date date);
}