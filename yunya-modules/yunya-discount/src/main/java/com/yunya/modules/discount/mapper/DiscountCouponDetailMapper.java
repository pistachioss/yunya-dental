package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.DiscountCouponDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DiscountCouponDetailMapper extends Mapper<DiscountCouponDetail> {
    /**
     * 批量新增
     *
     * @param discountCouponDetails
     */
    void batchInsert(@Param("list") List<DiscountCouponDetail> discountCouponDetails);
}