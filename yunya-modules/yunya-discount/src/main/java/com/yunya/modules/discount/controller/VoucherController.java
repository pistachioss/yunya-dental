package com.yunya.modules.discount.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.VoucheCoupon;
import com.yunya.modules.discount.biz.CouponCommonInfoBiz;
import com.yunya.modules.discount.biz.VoucherBiz;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.form.CouponCommonInfoQueryForm;
import com.yunya.modules.discount.form.VoucheCouponForm;
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
 * @create 2020-07-09 17:45
 */
@Api(tags = "代金券")
@RestController
@RequestMapping("/voucher")
public class VoucherController {
    @Autowired
    private VoucherBiz voucherBiz;
    @Autowired
    private CouponCommonInfoBiz couponCommonInfoBiz;
    @Autowired
    private CouponAllocateMapper couponAllocateMapper;
    /**
     * 新增代金券基础信息
     *
     * @param voucheCouponForm
     * @return
     */
    @PostMapping
    @ApiOperation("新增代金券基础信息(返回插入成功后的ID)")
    @CurrentUser
    public ResponseResult save(@RequestBody @Valid VoucheCouponForm voucheCouponForm) {
        return ResponseUtil.success(voucherBiz.saveVoucher(voucheCouponForm));
    }

    /**
     * 修改代金券基础信息
     *
     * @param discountUpdateForm
     * @return
     */
    @PutMapping
    @ApiOperation("修改代金券基础信息")
    @CurrentUser
    public ResponseResult update(@RequestBody VoucheCouponForm discountUpdateForm) {
        voucherBiz.updateVoucher(discountUpdateForm);
        return ResponseUtil.success();
    }

    /**
     * 删除代金券
     *
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除代金券")
    public ResponseResult delete(@PathVariable("id") Integer id) {
        voucherBiz.deleteVoucher(id);
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
        VoucheCoupon voucheCoupon = new VoucheCoupon();
        voucheCoupon.setCouponId(couponCommonInfo.getId());
        voucheCoupon = voucherBiz.selectOne(voucheCoupon);
        VoucheCouponForm voucheCouponForm = new VoucheCouponForm();
        BeanUtils.copyProperties(voucheCoupon, voucheCouponForm);
        BeanUtils.copyProperties(couponCommonInfo, voucheCouponForm);
        //格式问题 单独进行转换赋值
        voucheCouponForm.setMixedUseType(voucheCoupon.getMixedUseType().intValue());
        voucheCouponForm.setIsDistribution(true);

        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(id);
        if (couponAllocateMapper.select(couponAllocate).isEmpty()) {
            // 未完成分配
            voucheCouponForm.setIsDistribution(false);
        }


        return ResponseUtil.success(voucheCouponForm);
    }

    /**
     * 获取代金券的配给门诊ID列表
     *
     * @return
     */
    @GetMapping("/findOrgId/{id}")
    @ApiOperation("获取代金券的配给门诊ID列表")
    public ResponseResult findOrgIdList(@PathVariable("id") Integer id) {
        VoucheCoupon voucheCoupon = new VoucheCoupon();
        voucheCoupon.setCouponId(id);
        voucheCoupon = voucherBiz.selectOne(voucheCoupon);
        List<String> list = Arrays.asList(voucheCoupon.getUseableClinci().split(","));
        List<Integer>reList = new ArrayList<>();
        for(String s:list){
            reList.add(Integer.valueOf(s));
        }
        return ResponseUtil.success(reList);
    }

    /**
     * 获取列表
     *
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("根据分类获取列表(卡券通用方法 用类型来区分)")
    public ResponseResult findList(@RequestBody @Valid CouponCommonInfoQueryForm couponCommonInfoQueryForm) {
        return ResponseUtil.success(
                voucherBiz.findList(couponCommonInfoQueryForm));
    }

}
