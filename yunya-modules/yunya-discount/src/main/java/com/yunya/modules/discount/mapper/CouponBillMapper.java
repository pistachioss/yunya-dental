package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.CouponBill;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;

public interface CouponBillMapper extends Mapper<CouponBill> {
    String selectBillNumberByOrgId(@Param("orgId") Integer orgId, @Param("date") Date date);
}