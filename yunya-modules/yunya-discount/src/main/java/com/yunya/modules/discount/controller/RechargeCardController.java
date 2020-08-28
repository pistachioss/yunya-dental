package com.yunya.modules.discount.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.RechargeCard;
import com.yunya.modules.discount.biz.CouponCommonInfoBiz;
import com.yunya.modules.discount.biz.RechargeCardBiz;
import com.yunya.modules.discount.form.DiscountUpdateForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.form.RechargeCardForm;
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
 * 充值卡相关接口
 *
 * @author GaoLuding
 * @create 2020-07-17 10:09
 */
@Api(tags = "充值卡")
@RestController
@RequestMapping("/recharge_card")
@CurrentUser
public class RechargeCardController {
    @Autowired
    private RechargeCardBiz rechargeCardBiz;
    @Autowired
    private CouponCommonInfoBiz couponCommonInfoBiz;
    /**
     * 新增充值卡
     *
     * @param rechargeCardForm
     * @return
     */
    @PostMapping
    @ApiOperation("新增充值卡")
    public ResponseResult save(@RequestBody @Valid RechargeCardForm rechargeCardForm) {
        return ResponseUtil.success(rechargeCardBiz.saveRechargeCard(rechargeCardForm));
    }

    /**
     * 修改充值卡
     *
     * @param rechargeCardForm
     * @return
     */
    @PutMapping
    @ApiOperation("修改充值卡")
    public ResponseResult update(@RequestBody RechargeCardForm rechargeCardForm) {
        rechargeCardBiz.updateRechargeCard(rechargeCardForm);
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
     * 获取单个对象详细信息
     *
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("获取单个对象详细信息")
    public ResponseResult findOne(@PathVariable("id") Integer id) {
        CouponCommonInfo couponCommonInfo = couponCommonInfoBiz.selectById(id);
        RechargeCard rechargeCard = new RechargeCard();
        rechargeCard.setCouponId(couponCommonInfo.getId());
        rechargeCard = rechargeCardBiz.selectOne(rechargeCard);
        RechargeCardForm rechargeCardForm = new RechargeCardForm();
        BeanUtils.copyProperties(rechargeCard, rechargeCardForm);
        BeanUtils.copyProperties(couponCommonInfo, rechargeCardForm);
        return ResponseUtil.success(rechargeCardForm);
    }

}
