package com.yunya.modules.discount.controller;

import com.yunya.modules.discount.biz.DiscountCouponDetailBiz;
import com.yunya.modules.discount.form.DiscountDetailForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-15 14:32
 */
@Api(tags = "折扣券")
@RestController
@RequestMapping("/discount_coupon_detail")
public class DiscountCouponDetailController {
    @Autowired
    private DiscountCouponDetailBiz discountCouponDetailBiz;

    /**
     * 新增代金券明细
     *
     * @param discountDetailForm
     * @return
     */
    @PostMapping
    @ApiOperation("新增代金券")
    public ResponseResult save(@RequestBody DiscountDetailForm discountDetailForm) {
        discountCouponDetailBiz.saveDiscountCouponDetail(discountDetailForm);
        return ResponseUtil.success();
    }

    /**
     * 修改代金券明细
     *
     * @param discountDetailForm
     * @return
     */
    @PutMapping
    @ApiOperation("修改代金券明细")
    public ResponseResult update(@RequestBody DiscountDetailForm discountDetailForm) {
        discountCouponDetailBiz.updateDiscountCouponDetail(discountDetailForm);
        return ResponseUtil.success();
    }
}
