package com.yunya.modules.discount.controller;

import com.yunya.feign.discount.domain.model.CouponOrderModel;
import com.yunya.feign.discount.domain.vo.CouponGoodsVO;
import com.yunya.feign.discount.domain.vo.CouponOrderVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.modules.discount.biz.CouponGoodsBiz;
import com.yunya.modules.discount.biz.CouponOrderBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @auther: xy
 * @date: 2023/6/28
 */
@Api(tags = {"划扣卡-列表、购买"})
@RestController
public class CouponOrderController {
    @Resource
    private CouponGoodsBiz couponGoodsBiz;
    @Resource
    private CouponOrderBiz couponOrderBiz;

    @ApiOperation(value = "划扣卡购买列表")
    @PostMapping("/coupon/goods/list")
    @CurrentUser
    public List<CouponGoodsVO> hkList() {
        return couponGoodsBiz.hkList();
    }

    @ApiOperation(value = "划扣卡下单")
    @PostMapping("/coupon/order")
    @CurrentUser
    public CouponOrderVO soldCard(@Valid @RequestBody CouponOrderModel model) {
        return couponOrderBiz.soldCard(model);
    }

    @ApiOperation(value = "划扣卡订单详情")
    @GetMapping("/coupon/order/{orderId}")
    public CouponOrderVO detail(@PathVariable(value = "orderId") Integer orderId) {
        return couponOrderBiz.detail(orderId);
    }
}
