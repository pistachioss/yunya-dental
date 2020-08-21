package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.CouponAllocate;
import com.yunya.modules.discount.form.CouponAllocateForm;
import com.yunya.modules.discount.vo.CouponAllocateDetailVO;
import com.yunya.modules.discount.vo.CouponAllocateVO;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

import java.time.*;

public interface CouponAllocateMapper extends Mapper<CouponAllocate> {

    /**
     * 查询已生成卡券数量
     */
    int  countGenerateByParam(@Param("couponId") Integer couponId, @Param("submitDate")LocalDateTime sumitDate);
    int insertAll(List<CouponAllocateForm> list);
    List<CouponAllocateVO> findVOList(Integer id);
    List<CouponAllocateDetailVO> findVODetailList(CouponAllocateVO couponAllocateVO);
}