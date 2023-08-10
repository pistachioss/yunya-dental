package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseCouponBillPayDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BaseCouponBillPayDetailMapper extends Mapper<BaseCouponBillPayDetail> {
    /**
     * 根据账单收费记录ID删除账单收费记录支付明细
     *
     * @param billPayId 收费记录ID
     */
    void deleteByBillPayId(@Param("billPayId") Integer billPayId);
}