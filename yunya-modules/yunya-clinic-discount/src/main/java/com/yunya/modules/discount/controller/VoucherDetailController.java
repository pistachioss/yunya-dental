package com.yunya.modules.discount.controller;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-13 14:00
 */

import com.yunya.modules.discount.biz.VoucherDetailBiz;
import com.yunya.modules.discount.form.DiscountDetailForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "代金券")
@RestController
@RequestMapping("/voucher_detail")
public class VoucherDetailController {
    @Autowired
    private VoucherDetailBiz voucherDetailBiz;

    /**
     * 新增代金券明细
     *
     * @param discountDetailForm
     * @return
     */
    @PostMapping
    @ApiOperation("新增代金券明细")
    public ResponseResult save(@RequestBody DiscountDetailForm discountDetailForm) {
        voucherDetailBiz.saveVoucherDetail(discountDetailForm);
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
        voucherDetailBiz.updateVoucherDetail(discountDetailForm);
        return ResponseUtil.success();
    }
}
