package com.yunya.discount.rest;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.discount.domain.query.CouponCommonInfoQuery;
import com.yunya.feign.discount.domain.vo.CouponCommonInfoVO;
import com.yunya.modules.discount.ClinicDiscountApplication;
import com.yunya.modules.discount.controller.CardController;
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
 * @Date: 2022/7/12 14:40
 * @since: 1.0.0
 */
@SpringBootTest(classes = ClinicDiscountApplication.class)
@RunWith(SpringRunner.class)
public class CardControllerTest {

    @Autowired
    private CardController cardController;

    @Test
    public void findCouponList() {
        CouponCommonInfoQuery query = new CouponCommonInfoQuery();
        query.setWhetherPage(false);
        query.setProductTypeId(14);
//        query.setType(3);
        PageInfo<CouponCommonInfoVO> data = cardController.findCouponList(query).getData();
        System.out.println(JSONObject.toJSON(data));
    }
}
