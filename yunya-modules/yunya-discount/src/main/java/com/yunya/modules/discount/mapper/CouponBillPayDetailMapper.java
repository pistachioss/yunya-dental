package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.vo.CouponRefundAccountVO;
import com.yunya.models.discount.CouponBillPayDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CouponBillPayDetailMapper extends Mapper<CouponBillPayDetail> {
    List<CouponRefundAccountVO> listPayAccount(@Param("orderId") Integer orderId);
}