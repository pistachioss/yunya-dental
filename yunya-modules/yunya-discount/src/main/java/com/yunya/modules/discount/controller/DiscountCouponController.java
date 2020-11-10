package com.yunya.modules.discount.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.DiscountCoupon;
import com.yunya.modules.discount.biz.CouponCommonInfoBiz;
import com.yunya.modules.discount.biz.DiscountCouponBiz;
import com.yunya.modules.discount.form.DiscountCouponForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
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
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-14 17:23
 */
@Api(tags = "折扣券")
@RestController
@RequestMapping("/discount_coupon")
@CurrentUser
public class DiscountCouponController {
    @Autowired
    private DiscountCouponBiz discountCouponBiz;
    @Autowired
    private CouponCommonInfoBiz couponCommonInfoBiz;
    @Autowired
    private CouponAllocateMapper couponAllocateMapper;
    /**
     * 新增折扣券
     *
     * @param discountCouponForm
     * @return
     */
    @PostMapping
    @ApiOperation("新增折扣券")
    public ResponseResult save(@RequestBody @Valid DiscountCouponForm discountCouponForm) {
        return ResponseUtil.success(discountCouponBiz.saveDiscountCoupon(discountCouponForm));
    }

    /**
     * 修改折扣券
     *
     * @param discountCouponForm
     * @return
     */
    @PutMapping
    @ApiOperation("修改折扣券")
    public ResponseResult update(@RequestBody DiscountCouponForm discountCouponForm) {
        discountCouponBiz.updateDiscountCoupon(discountCouponForm);
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
     * 获取单个对象详细信息
     *
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("获取单个对象详细信息")
    public ResponseResult findOne(@PathVariable("id") Integer id) {
        CouponCommonInfo couponCommonInfo = couponCommonInfoBiz.selectById(id);
        DiscountCoupon discountCoupon = new DiscountCoupon();
        discountCoupon.setCouponId(couponCommonInfo.getId());
        DiscountCoupon coupon = discountCouponBiz.selectOne(discountCoupon);
        if (null != coupon) {
            discountCoupon = coupon;
        }
        DiscountCouponForm discountCouponForm = new DiscountCouponForm();
        BeanUtils.copyProperties(discountCoupon, discountCouponForm);
        BeanUtils.copyProperties(couponCommonInfo, discountCouponForm);

        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(id);
        if (couponAllocateMapper.findAllocate(couponAllocate)==0) {
            // 未完成分配
            discountCouponForm.setIsDistribution(false);
        }
        return ResponseUtil.success(discountCouponForm);
    }

    /**
     * 获取折扣券的配给门诊ID列表
     *
     * @return
     */
    @GetMapping("/findOrgId/{id}")
    @ApiOperation("获取折扣券的配给门诊ID列表")
    public ResponseResult findOrgIdList(@PathVariable("id") Integer id) {
        DiscountCoupon discountCoupon = new DiscountCoupon();
        discountCoupon.setCouponId(id);
        discountCoupon = discountCouponBiz.selectOne(discountCoupon);
        List<String> list = Arrays.asList(discountCoupon.getUseableClinic().split(","));
        List<Integer>reList = new ArrayList<>();
        for(String s:list){
            reList.add(Integer.valueOf(s));
        }
        return ResponseUtil.success(reList);
    }

}
