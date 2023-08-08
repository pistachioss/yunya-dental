package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseCouponBillDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BaseCouponBillDetailMapper extends Mapper<BaseCouponBillDetail> {
    void deleteByBillId(@Param("billId") Integer billId);
}