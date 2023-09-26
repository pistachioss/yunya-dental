package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.vo.UnChargeCouponVO;
import com.yunya.models.report.BaseCouponOrderVirtual;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.List;

public interface BaseCouponOrderVirtualMapper extends Mapper<BaseCouponOrderVirtual> {
    List<UnChargeCouponVO> listUnChargeCoupon(@Param("couponIds") Collection<Integer> couponIds);
}