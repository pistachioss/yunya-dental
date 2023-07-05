package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.CouponOrder;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;

public interface CouponOrderMapper extends Mapper<CouponOrder> {
    String selectOrderNumberByOrgId(@Param("orgId") Integer orgId, @Param("date") Date date);
}