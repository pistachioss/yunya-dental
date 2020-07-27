package com.clinic.discount.controller;

import com.clinic.discount.biz.PackageCouponDetailBiz;
import com.clinic.discount.form.PackageDetailForm;
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
 * @create 2020-07-16 10:07
 */
@Api(tags = "套餐券")
@RestController
@RequestMapping("/package_coupon_detail")
public class PackageCouponDetailController {
    @Autowired
    private PackageCouponDetailBiz packageCouponDetailBiz;

    /**
     * 新增套餐券明细
     *
     * @param packageDetailForm
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐券明细")
    public ResponseResult save(@RequestBody @Valid PackageDetailForm packageDetailForm) {
        packageCouponDetailBiz.savePackageCouponDetail(packageDetailForm);
        return ResponseUtil.success();
    }

    /**
     * 修改套餐券明细
     *
     * @param discountDetailForm
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐券明细")
    public ResponseResult update(@RequestBody @Valid PackageDetailForm discountDetailForm) {
        packageCouponDetailBiz.updatePackageCouponDetail(discountDetailForm);
        return ResponseUtil.success();
    }
}
