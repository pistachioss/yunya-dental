package com.yunya.modules.discount.controller;

import com.yunya.modules.discount.biz.PackageCouponBiz;
import com.yunya.models.discount.PackageCoupon;
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
@Api(tags = "套餐券")
@RestController
@RequestMapping("/package_coupon")
public class PackageCouponController {
    @Autowired
    private PackageCouponBiz packageCouponBiz;

    /**
     * 新增套餐券
     *
     * @param packageCoupon
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐券")
    public ResponseResult save(@RequestBody @Valid PackageCoupon packageCoupon) {
        return ResponseUtil.success(packageCouponBiz.savePackageCoupon(packageCoupon));
    }

    /**
     * 修改套餐券
     *
     * @param discountUpdateForm
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐券")
    public ResponseResult update(@RequestBody DiscountUpdateForm discountUpdateForm) {
        packageCouponBiz.updatePackageCoupon(discountUpdateForm);
        return ResponseUtil.success();
    }

    /**
     * 删除套餐券
     *
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除套餐券")
    public ResponseResult delete(@PathVariable("id") Integer id) {
        packageCouponBiz.deletePackageCoupon(id);
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
        return ResponseUtil.success(packageCouponBiz.selectById(id));
    }

    /**
     * 获取列表
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("获取列表")
    public ResponseResult list() {
        return ResponseUtil.success(packageCouponBiz.selectListAll());
    }

    /**
     * 查询列表
     *
     * @return
     */
    @PostMapping("/search")
    @ApiOperation("查询列表")
    public ResponseResult search(DiscountQueryForm discountQueryForm) {
        return ResponseUtil.success(packageCouponBiz.search(discountQueryForm));
    }
}
