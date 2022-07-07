package com.yunya.report.ultimate.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.CouponStatisticsQuery;
import com.yunya.feign.report.domain.vo.CouponStatisticsVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/7/5 17:36
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DiscountControllerTest {

    @Autowired
    private DiscountController discountController;

    @Test
    public void test() {
        String param = "{\"couponCategoryIds\":[],\"couponName\":\"\",\"couponTypes\":[],\"pageNum\":1,\"pageSize\":12,\"whetherPage\":true}";
        CouponStatisticsQuery query = JSONObject.parseObject(param, CouponStatisticsQuery.class);
        PageInfo<CouponStatisticsVo> data = discountController.getCouponStatistics(query).getData();
        System.out.println(JSONObject.toJSON(data));
    }
}
