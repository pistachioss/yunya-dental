package com.yunya.modules.discount.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.PackageCoupon;
import com.yunya.modules.discount.biz.CouponCommonInfoBiz;
import com.yunya.modules.discount.biz.PackageCouponBiz;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.form.PackageCouponForm;
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
 * 描述:
 *
 * @author
 * @create 2020-07-14 17:23
 */
@Api(tags = "兑换券")
@RestController
@RequestMapping("/package_coupon")
@CurrentUser
public class PackageCouponController {
    @Autowired
    private PackageCouponBiz packageCouponBiz;
    @Autowired
    private CouponCommonInfoBiz couponCommonInfoBiz;

    /**
     * 新增兑换券
     *
     * @param packageCouponForm
     * @return
     */
    @PostMapping
    @ApiOperation("新增兑换券")
    public ResponseResult save(@RequestBody @Valid PackageCouponForm packageCouponForm) {
        return ResponseUtil.success(packageCouponBiz.savePackageCoupon(packageCouponForm));
    }

    /**
     * 修改兑换券
     *
     * @param packageCouponForm
     * @return
     */
    @PutMapping
    @ApiOperation("修改兑换券")
    public ResponseResult update(@RequestBody PackageCouponForm packageCouponForm) {
        packageCouponBiz.updatePackageCoupon(packageCouponForm);
        return ResponseUtil.success();
    }

    /**
     * 删除兑换券
     *
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除兑换券")
    public ResponseResult delete(@PathVariable("id") Integer id) {
        packageCouponBiz.deletePackageCoupon(id);
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
        PackageCoupon packageCoupon = new PackageCoupon();
        packageCoupon.setCouponId(couponCommonInfo.getId());
        packageCoupon = packageCouponBiz.selectOne(packageCoupon);
        PackageCouponForm packageCouponForm = new PackageCouponForm();
        BeanUtils.copyProperties(packageCoupon, packageCouponForm);
        BeanUtils.copyProperties(couponCommonInfo, packageCouponForm);
        return ResponseUtil.success(packageCouponForm);
    }

    /**
     * 获取折扣券的配给门诊ID列表
     *
     * @return
     */
    @GetMapping("/findOrgId/{id}")
    @ApiOperation("获取折扣券的配给门诊ID列表")
    public ResponseResult findOrgIdList(@PathVariable("id") Integer id) {
        PackageCoupon packageCoupon = new PackageCoupon();
        packageCoupon.setCouponId(id);
        packageCoupon = packageCouponBiz.selectOne(packageCoupon);
        List<String> list = Arrays.asList(packageCoupon.getUseableClinic().split(","));
        List<Integer> reList = new ArrayList<>();
        for (String s : list) {
            reList.add(Integer.valueOf(s));
        }
        return ResponseUtil.success(reList);
    }
}
