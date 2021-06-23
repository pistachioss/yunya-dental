package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.bo.*;
import com.yunya.models.discount.*;
import com.yunya.modules.discount.form.*;
import com.yunya.modules.discount.vo.*;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.time.*;
import java.util.*;

public interface CouponAllocateMapper extends Mapper<CouponAllocate> {

    /**
     * 查询已生成卡券数量
     */
    int countGeneratedByParam(@Param("couponId") Integer couponId, @Param("submitDate") LocalDateTime sumitDate);

    int insertAll(List<CouponAllocateForm> list);

    List<CouponAllocateVO> findVOList(Integer id);

    List<CouponAllocateDetailVO> findVODetailList(CouponAllocateDetailForm couponAllocateDetailForm);

    /**
     * 更新优惠券分配信息状态
     */
    void updateAllocateByIds(@Param("couponAllocateIds") List<Integer> couponAllocateIds, @Param("submitDate") LocalDateTime submitDate
            , @Param("allocateUserId") Integer allocateUserId, @Param("allocateDate") LocalDateTime allocateDate);


    OrgCouponAllocateBo getOrgAllocateByParam(@Param("couponId") Integer couponId, @Param("orgId") Integer orgId);

    /**
     * 查询是否有生成分配的配给计划
     */
    int findAllocate(CouponAllocate couponAllocate);

    int countCouponAllocate(@Param("couponId") Integer couponId);
}