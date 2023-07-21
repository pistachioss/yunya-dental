package com.yunya.modules.discount.controller;

import com.yunya.feign.discount.domain.model.CouponOrderModel;
import com.yunya.feign.discount.domain.vo.CouponGoodsVO;
import com.yunya.feign.discount.domain.vo.CouponOrderVO;
import com.yunya.feign.discount.domain.vo.DeductionCategoryVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.biz.CouponGoodsBiz;
import com.yunya.modules.discount.biz.CouponOrderBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

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
    @GetMapping("/coupon/goods/list")
    @CurrentUser
    public ResponseResult<List<CouponGoodsVO>> hkList(@NotNull @RequestParam Integer categoryId) {
        return ResponseUtil.success(couponGoodsBiz.hkList(categoryId));
    }

    @ApiOperation(value = "划扣卡分类")
    @PostMapping("/coupon/goods/category")
    @CurrentUser
    public ResponseResult<Set<DeductionCategoryVO>> category() {
        return ResponseUtil.success(couponGoodsBiz.category());
    }

    @ApiOperation(value = "划扣卡下单")
    @PostMapping("/coupon/order")
    @CurrentUser
    public ResponseResult<CouponOrderVO> soldCard(@Valid @RequestBody CouponOrderModel model) {
        return ResponseUtil.success(couponOrderBiz.soldCard(model));
    }

    @ApiOperation(value = "划扣卡订单详情")
    @GetMapping("/coupon/order/{orderId}")
    public ResponseResult<CouponOrderVO> detail(@PathVariable(value = "orderId") Integer orderId) {
        return ResponseUtil.success(couponOrderBiz.detail(orderId));
    }

    @ApiOperation(value = "购买按钮跳转")
    @GetMapping("/coupon/order/click")
    @CurrentUser
    public ResponseResult<Integer> click(@NotNull @RequestParam Integer patientId) {
        return ResponseUtil.success(couponOrderBiz.click(patientId));
    }

    @ApiOperation(value = "删除订单")
    @PostMapping("/coupon/order/delete")
    @CurrentUser
    public ResponseResult<Boolean> delete(@NotNull @RequestParam Integer orderId) {
        couponOrderBiz.delete(orderId);
        return ResponseUtil.success(true);
    }
}
