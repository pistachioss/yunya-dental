package com.yunya.modules.discount.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.SpecialPackageCoupon;
import com.yunya.modules.discount.biz.CouponCommonInfoBiz;
import com.yunya.modules.discount.biz.SpecialPackageCouponBiz;
import com.yunya.modules.discount.form.SpecialPackageCouponForm;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 杨柳絮
 * @className SpecialPackageCouponController
 * @description
 * @date 2020/8/27 14:58
 */
@Api(tags = "套餐券")
@RestController
@RequestMapping("/special_coupon")
@CurrentUser
public class SpecialPackageCouponController {
    @Autowired
    private SpecialPackageCouponBiz specialPackageCouponBiz;
    @Autowired
    private CouponCommonInfoBiz couponCommonInfoBiz;
    @Autowired
    private CouponAllocateMapper couponAllocateMapper;

    /**
     * 新增套餐券
     *
     * @param specialPackageCouponForm
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐券")
    public ResponseResult save(@RequestBody @Valid SpecialPackageCouponForm specialPackageCouponForm) {
        return ResponseUtil.success(specialPackageCouponBiz.saveSpecialPackageCoupon(specialPackageCouponForm));
    }

    /**
     * 修改套餐券
     *
     * @param specialPackageCouponForm
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐券")
    public ResponseResult update(@RequestBody SpecialPackageCouponForm specialPackageCouponForm) {
        specialPackageCouponBiz.updateSpecialPackageCoupon(specialPackageCouponForm);
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
        specialPackageCouponBiz.deletePackageCoupon(id);
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
        SpecialPackageCoupon specialPackageCoupon = new SpecialPackageCoupon();
        specialPackageCoupon.setCouponId(couponCommonInfo.getId());
        specialPackageCoupon = specialPackageCouponBiz.selectOne(specialPackageCoupon);
        SpecialPackageCouponForm specialPackageCouponForm = new SpecialPackageCouponForm();
        BeanUtils.copyProperties(specialPackageCoupon, specialPackageCouponForm);
        BeanUtils.copyProperties(couponCommonInfo, specialPackageCouponForm);
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(id);
        if (couponAllocateMapper.findAllocate(couponAllocate)==0) {
            // 未完成分配
            specialPackageCouponForm.setIsDistribution(false);
        }
        return ResponseUtil.success(specialPackageCouponForm);
    }

    /**
     * 获取折扣券的配给门诊ID列表
     *
     * @return
     */
    @GetMapping("/findOrgId/{id}")
    @ApiOperation("获取折扣券的配给门诊ID列表")
    public ResponseResult findOrgIdList(@PathVariable("id") Integer id) {
        SpecialPackageCoupon specialPackageCoupon = new SpecialPackageCoupon();
        specialPackageCoupon.setCouponId(id);
        specialPackageCoupon = specialPackageCouponBiz.selectOne(specialPackageCoupon);
        List<String> list = Arrays.asList(specialPackageCoupon.getUseableClinic().split(","));
        List<Integer> reList = new ArrayList<>();
        for (String s : list) {
            reList.add(Integer.valueOf(s));
        }
        return ResponseUtil.success(reList);
    }
}
