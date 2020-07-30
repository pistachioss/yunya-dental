package com.clinic.discount.controller;

import com.clinic.discount.biz.RechargeCardBiz;
import com.clinic.discount.entity.RechargeCard;
import com.clinic.discount.form.DiscountQueryForm;
import com.clinic.discount.form.DiscountUpdateForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 描述:
 * 充值卡相关接口
 *
 * @author GaoLuding
 * @create 2020-07-17 10:09
 */
@Api(tags = "充值卡")
@RestController
@RequestMapping("/recharge_card")
public class RechargeCardController {
    @Autowired
    private RechargeCardBiz rechargeCardBiz;

    /**
     * 新增充值卡
     *
     * @param rechargeCard
     * @return
     */
    @PostMapping
    @ApiOperation("新增充值卡")
    public ResponseResult save(@RequestBody @Valid RechargeCard rechargeCard) {
        return ResponseUtil.success(rechargeCardBiz.saveRechargeCard(rechargeCard));
    }

    /**
     * 修改充值卡
     *
     * @param discountUpdateForm
     * @return
     */
    @PutMapping
    @ApiOperation("修改充值卡")
    public ResponseResult update(@RequestBody DiscountUpdateForm discountUpdateForm) {
        rechargeCardBiz.updateRechargeCard(discountUpdateForm);
        return ResponseUtil.success();
    }

    /**
     * 删除充值卡
     *
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除充值卡")
    public ResponseResult update(@PathVariable("id") Integer id) {
        rechargeCardBiz.deleteRechargeCard(id);
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
        return ResponseUtil.success(rechargeCardBiz.selectById(id));
    }

    /**
     * 获取列表
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("获取列表")
    public ResponseResult list() {
        return ResponseUtil.success(rechargeCardBiz.selectListAll());
    }

    /**
     * 查询列表
     *
     * @return
     */
    @PostMapping("/search")
    @ApiOperation("查询列表")
    public ResponseResult search(DiscountQueryForm discountQueryForm) {
        return ResponseUtil.success(rechargeCardBiz.search(discountQueryForm));
    }
}
