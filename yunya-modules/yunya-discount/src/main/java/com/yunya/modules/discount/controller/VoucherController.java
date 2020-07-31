package com.yunya.modules.discount.controller;

import com.yunya.modules.discount.biz.VoucherBiz;
import com.yunya.models.discount.Voucher;
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
 * @create 2020-07-09 17:45
 */
@Api(tags = "代金券")
@RestController
@RequestMapping("/voucher")
public class VoucherController {
    @Autowired
    private VoucherBiz voucherBiz;

    /**
     * 新增代金券
     *
     * @param voucher
     * @return
     */
    @PostMapping
    @ApiOperation("新增代金券")
    public ResponseResult save(@RequestBody @Valid Voucher voucher) {
        return ResponseUtil.success(voucherBiz.saveVoucher(voucher));
    }

    /**
     * 修改代金券
     *
     * @param discountUpdateForm
     * @return
     */
    @PutMapping
    @ApiOperation("修改代金券")
    public ResponseResult update(@RequestBody DiscountUpdateForm discountUpdateForm) {
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
    public ResponseResult update(@PathVariable("id") Integer id) {
        voucherBiz.deleteVoucher(id);
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
        return ResponseUtil.success(voucherBiz.selectById(id));
    }

    /**
     * 获取列表
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("获取列表")
    public ResponseResult list() {
        return ResponseUtil.success(voucherBiz.selectListAll());
    }

    /**
     * 查询列表
     *
     * @return
     */
    @PostMapping("/search")
    @ApiOperation("查询列表")
    public ResponseResult search(DiscountQueryForm discountQueryForm) {
        return ResponseUtil.success(voucherBiz.search(discountQueryForm));
    }
}
