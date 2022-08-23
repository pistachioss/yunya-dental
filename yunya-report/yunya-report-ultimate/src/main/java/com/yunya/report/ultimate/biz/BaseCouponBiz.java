package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.BaseCouponQueryForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.BaseCoupon;
import com.yunya.report.ultimate.mapper.BaseCouponMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/12/21 11:24
 * @since: 1.0.0
 */
@Service
public class BaseCouponBiz extends BaseBiz<BaseCouponMapper, BaseCoupon> {

    /**
     * 条件查询产品卡券列表
     *
     * @param couponIds
     * @return
     */
    public List<BaseCoupon> findBaseCouponListByCouponId(Collection<Integer> couponIds) {
        BaseCouponQueryForm queryForm = new BaseCouponQueryForm();
        queryForm.setCouponIds(couponIds);
        return findBaseCouponList(queryForm);
    }

    /**
     * 条件查询产品卡券列表
     *
     * @param query
     * @return
     */
    public List<BaseCoupon> findBaseCouponList(BaseCouponQueryForm query) {
        return mapper.selectBaseCouponList(query);
    }
}
