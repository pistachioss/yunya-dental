package com.yunya.modules.discount.controller;

import com.yunya.models.discount.DiscountCoupon;
import com.yunya.modules.discount.biz.DiscountCouponBiz;
import com.yunya.modules.discount.form.DiscountQueryForm;
import com.yunya.modules.discount.form.DiscountUpdateForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-14 17:23
 */
@Api(tags = "折扣券")
@RestController
@RequestMapping("/discount_coupon")
public class DiscountCouponController {
    @Autowired
    private DiscountCouponBiz discountCouponBiz;

    /**
     * 新增折扣券
     *
     * @param discountCoupon
     * @return
     */
    @PostMapping
    @ApiOperation("新增折扣券")
    public ResponseResult save(@RequestBody @Valid DiscountCoupon discountCoupon) {
        return ResponseUtil.success(discountCouponBiz.saveDiscountCoupon(discountCoupon));
    }

    /**
     * 修改折扣券
     *
     * @param discountUpdateForm
     * @return
     */
    @PutMapping
    @ApiOperation("修改折扣券")
    public ResponseResult update(@RequestBody DiscountUpdateForm discountUpdateForm) {
        discountCouponBiz.updateDiscountCoupon(discountUpdateForm);
        return ResponseUtil.success();
    }

    /**
     * 删除折扣券
     *
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除折扣券")
    public ResponseResult delete(@PathVariable("id") Integer id) {
        discountCouponBiz.deleteDiscountCoupon(id);
        return ResponseUtil.success();
    }

    /**
     * 获取对象
     *
     * @return
     */
    @GetMapping("/{id}}")
    @ApiOperation("获取对象")
    public ResponseResult findOne(@PathVariable("id") Integer id) {
        return ResponseUtil.success(discountCouponBiz.selectById(id));
    }

    /**
     * 获取列表
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("获取列表")
    public ResponseResult list() {
        return ResponseUtil.success(discountCouponBiz.selectListAll());
    }

//    /**
//     * 查询列表
//     *
//     * @return
//     */
//    @PostMapping("/search")
//    @ApiOperation("查询列表")
//    public ResponseResult search(DiscountQueryForm discountQueryForm) {
//        return ResponseUtil.success(discountCouponBiz.search(discountQueryForm));
//    }
}
