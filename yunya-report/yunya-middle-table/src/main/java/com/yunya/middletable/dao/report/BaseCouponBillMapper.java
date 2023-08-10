package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseCouponBill;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Set;

public interface BaseCouponBillMapper extends Mapper<BaseCouponBill> {
    void batchInsertSelective(@Param("baseBills") Set<BaseCouponBill> baseBills);
}