package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.CouponAllocate;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.time.*;

public interface CouponAllocateMapper extends Mapper<CouponAllocate> {

    /**
     * 查询已生成卡券数量
     */
    int  countGenerateByParam(@Param("couponId") Integer couponId, @Param("submitDate")LocalDateTime sumitDate);
}