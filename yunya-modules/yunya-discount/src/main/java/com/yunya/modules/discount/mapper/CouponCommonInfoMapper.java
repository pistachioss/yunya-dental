package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.bo.*;
import com.yunya.models.discount.*;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.*;

public interface CouponCommonInfoMapper extends Mapper<CouponCommonInfo> {

    /**
     * 查询产品生成分配
     * @param couponName
     * @param couponTypeList
     * @return
     */
    List<GenerateAllocatePageBo> listBatchAllocateByParam(@Param("couponName") String couponName, @Param("couponTypeList") List<Integer> couponTypeList);
}