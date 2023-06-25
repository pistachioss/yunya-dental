package com.yunya.modules.discount.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.DeductionCoupon;
import com.yunya.modules.discount.biz.CouponCommonInfoBiz;
import com.yunya.modules.discount.biz.DeductionCouponBiz;
import com.yunya.modules.discount.form.DeductionCouponForm;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Api(tags = "划扣券")
@RestController
@RequestMapping("/deduction_coupon")
@CurrentUser
public class DeductionCouponController {
    @Resource
    private DeductionCouponBiz deductionCouponBiz;
    @Resource
    private CouponCommonInfoBiz couponCommonInfoBiz;
    @Resource
    private CouponAllocateMapper couponAllocateMapper;

    /**
     * 新增划扣券
     *
     * @param deductionCouponForm
     * @return
     */
    @PostMapping
    @ApiOperation("新增划扣券")
    public ResponseResult save(@RequestBody @Valid DeductionCouponForm deductionCouponForm) {
        return ResponseUtil.success(deductionCouponBiz.saveDeductionCoupon(deductionCouponForm));
    }

    /**
     * 修改划扣券
     *
     * @param deductionCouponForm
     * @return
     */
    @PutMapping
    @ApiOperation("修改划扣券")
    public ResponseResult update(@RequestBody DeductionCouponForm deductionCouponForm) {
        deductionCouponBiz.updateDeductionCoupon(deductionCouponForm);
        return ResponseUtil.success();
    }

    /**
     * 删除划扣券
     *
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除划扣券")
    public ResponseResult delete(@PathVariable("id") Integer id) {
        deductionCouponBiz.deletePackageCoupon(id);
        return ResponseUtil.success();
    }

    /**
     * 获取单个对象详细信息
     *
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("获取单个对象详细信息")
    public ResponseResult findOne(@PathVariable("id") Integer id) {
        CouponCommonInfo couponCommonInfo = couponCommonInfoBiz.selectById(id);
        DeductionCoupon deductionCoupon = new DeductionCoupon();
        deductionCoupon.setCouponId(couponCommonInfo.getId());
        deductionCoupon = deductionCouponBiz.selectOne(deductionCoupon);
        DeductionCouponForm deductionCouponForm = new DeductionCouponForm();
        BeanUtils.copyProperties(deductionCoupon, deductionCouponForm);
        BeanUtils.copyProperties(couponCommonInfo, deductionCouponForm);
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(id);
        if (couponAllocateMapper.findAllocate(couponAllocate)==0) {
            // 未完成分配
            deductionCouponForm.setIsDistribution(false);
        }
        return ResponseUtil.success(deductionCouponForm);
    }

    /**
     * 获取划扣券的配给门诊ID列表
     *
     * @return
     */
    @GetMapping("/findOrgId/{id}")
    @ApiOperation("获取划扣券的配给门诊ID列表")
    public ResponseResult findOrgIdList(@PathVariable("id") Integer id) {
        DeductionCoupon deductionCoupon = new DeductionCoupon();
        deductionCoupon.setCouponId(id);
        deductionCoupon = deductionCouponBiz.selectOne(deductionCoupon);
        List<String> list = Arrays.asList(deductionCoupon.getUseableClinic().split(","));
        List<Integer> reList = new ArrayList<>();
        for (String s : list) {
            reList.add(Integer.valueOf(s));
        }
        return ResponseUtil.success(reList);
    }
}
