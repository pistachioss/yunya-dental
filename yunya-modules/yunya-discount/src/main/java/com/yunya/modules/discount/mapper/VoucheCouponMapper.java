package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.VoucheCoupon;
import com.yunya.modules.discount.form.CouponCommonInfoFindDownForm;
import com.yunya.modules.discount.form.CouponCommonInfoQueryForm;
import com.yunya.modules.discount.vo.CouponCommonInfoVO;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface VoucheCouponMapper extends Mapper<VoucheCoupon> {

    List<CouponCommonInfoVO> findList(CouponCommonInfoQueryForm couponCommonInfoQueryForm);

    List<CouponCommonInfoVO> thirdParty();


    List<CouponCommonInfo> findDown(CouponCommonInfoFindDownForm couponCommonInfoFindDownForm);
}