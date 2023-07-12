package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.CouponOrderVirtual;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CouponOrderVirtualMapper extends Mapper<CouponOrderVirtual> {
    void insertList(@Param("List") List<CouponOrderVirtual> virtuals);
}