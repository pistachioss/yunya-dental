package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseCouponOrderVirtual;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;

public interface BaseCouponOrderVirtualMapper extends Mapper<BaseCouponOrderVirtual> {
    void deleteByBillId(@Param("orderId") Integer orderId);

    void batchInsertSelective(@Param("virtuals") Collection<BaseCouponOrderVirtual> virtuals);
}