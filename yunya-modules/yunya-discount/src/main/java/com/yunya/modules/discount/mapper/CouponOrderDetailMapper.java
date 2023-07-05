package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.CouponOrderDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CouponOrderDetailMapper extends Mapper<CouponOrderDetail> {
    void insertList(@Param("List") List<CouponOrderDetail> cardList);
}