package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseCouponRefundDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BaseCouponRefundDetailMapper extends Mapper<BaseCouponRefundDetail> {
    void deleteByRefundId(@Param("refundId") Integer refundId);
}