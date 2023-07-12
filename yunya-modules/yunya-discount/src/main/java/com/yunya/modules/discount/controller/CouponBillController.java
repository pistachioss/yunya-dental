package com.yunya.modules.discount.controller;

import com.yunya.feign.discount.domain.model.CouponBillModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.biz.CouponBillBiz;
import com.yunya.modules.discount.biz.CouponGoodsBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @auther: xy
 * @date: 2023/6/28
 */
@Api(tags = {"划扣卡-收费、账单"})
@RestController
public class CouponBillController {
    @Resource
    private CouponGoodsBiz couponGoodsBiz;
    @Resource
    private CouponBillBiz couponOrderBiz;

    @ApiOperation(value = "划扣卡下单")
    @PostMapping("/coupon/order")
    @CurrentUser
    public ResponseResult<Boolean> soldCard(@Valid @RequestBody CouponBillModel model) {
        couponOrderBiz.charge(model);
        return ResponseUtil.success();
    }

}
