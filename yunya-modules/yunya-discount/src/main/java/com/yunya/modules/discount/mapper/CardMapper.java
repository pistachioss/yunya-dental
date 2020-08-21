package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.*;
import org.apache.ibatis.annotations.*;

import java.util.*;


@Mapper
public interface CardMapper extends tk.mybatis.mapper.common.Mapper<Card> {

    /**
     * 根据分配id查询卡券生成数量
     */
    int countByAllocateId(@Param("couponAllocateIds") List<Integer> couponAllocateIds);

    int getMaxNumByCouponId(@Param("couponId") Integer couponId);
}