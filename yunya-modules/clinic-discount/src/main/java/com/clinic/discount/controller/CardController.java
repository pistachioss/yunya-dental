package com.clinic.discount.controller;

import com.clinic.discount.biz.CardBiz;
import com.clinic.discount.biz.CardClinicBiz;
import com.clinic.discount.entity.Card;
import com.clinic.discount.entity.CardClinic;
import com.clinic.discount.form.CardClinicForm;
import com.clinic.discount.form.CardDistributionForm;
import com.clinic.discount.form.CardSaleForm;
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
 * @create 2020-07-17 11:30
 */
@Api(tags = "卡券")
@RestController
@RequestMapping("/card")
public class CardController {
    @Autowired
    private CardClinicBiz cardClinicBiz;
    @Autowired
    private CardBiz cardBiz;

    /**
     * 配给计划
     *
     * @param
     * @return
     */
    @PostMapping("/distribution/plan/list")
    @ApiOperation("配给计划")
    public ResponseResult plan(@RequestBody @Valid CardDistributionForm cardDistributionForm) {
        cardClinicBiz.plan(cardDistributionForm);
        return ResponseUtil.success();
    }

    /**
     * 修改配给计划
     *
     * @param
     * @return
     */
    @GetMapping("/distribution/plan")
    @ApiOperation("修改配给计划")
    public ResponseResult updatePlan(@RequestBody CardDistributionForm cardDistributionForm) {
        cardClinicBiz.updatePlan(cardDistributionForm);
        return ResponseUtil.success();
    }

    /**
     * 获取产品分配列表
     *
     * @param
     * @return
     */
    @PostMapping("/distribution/search")
    @ApiOperation("获取产品分配列表")
    public ResponseResult search(@RequestBody CardClinicForm CardClinicForm) {
        return ResponseUtil.success(cardClinicBiz.search(CardClinicForm));
    }

    /**
     * 产品生成分配
     *
     * @param
     * @return
     */
    @GetMapping("/{type}/{relevanceId}/{revision}/distribution/rationing")
    @ApiOperation("获取优惠活动具体批次配给计划")
    public ResponseResult rationing(
            @PathVariable(name = "type") Integer type, @PathVariable(name = "relevanceId") Integer relevanceId,
            @PathVariable(name = "revision") Integer revision) {
        cardClinicBiz.rationing(type, relevanceId, revision);
        return ResponseUtil.success();
    }

    /**
     * 获取优惠活动配给计划列表
     *
     * @param
     * @return
     */
    @GetMapping("/{type}/{relevanceId}/distribution/plan")
    @ApiOperation("获取优惠活动计划配给列表")
    public ResponseResult getPlanList(@PathVariable(name = "type") Integer type, @PathVariable(name = "relevanceId") Integer relevanceId) {
        cardClinicBiz.getPlanList(relevanceId, type);
        return ResponseUtil.success();
    }

    /**
     * 获取优惠活动具体批次配给计划
     *
     * @param
     * @return
     */
    @GetMapping("/{type}/{relevanceId}/{revision}/distribution/plan")
    @ApiOperation("获取优惠活动具体批次配给计划")
    public ResponseResult getPlanListByRevision(
            @PathVariable(name = "type") Integer type, @PathVariable(name = "relevanceId") Integer relevanceId,
            @PathVariable(name = "revision") Integer revision) {
        CardClinic cardClinic = new CardClinic();
        cardClinic.setRelevanceId(relevanceId);
        cardClinic.setRevision(revision);
        cardClinic.setType(type);
        return ResponseUtil.success(cardClinicBiz.selectList(cardClinic));
    }

    /**
     * 获取优惠活动所有卡列表
     *
     * @param
     * @return
     */
    @GetMapping("/{type}/{relevanceId}/list")
    @ApiOperation("获取优惠活动具体批次配给计划")
    public ResponseResult getPlanListByRevision(@PathVariable(name = "type") Integer type, @PathVariable(name = "relevanceId") Integer relevanceId) {
        Card card = new Card();
        card.setCardType(type);
        card.setRelevanceId(relevanceId);
        return ResponseUtil.success(cardBiz.selectList(card));
    }

    /**
     * 售出
     *
     * @param
     * @return
     */
    @PostMapping("/sale")
    @ApiOperation("售出")
    public ResponseResult getPlanListByRevision(@RequestBody @Valid CardSaleForm cardSaleForm) {
        cardBiz.sale(cardSaleForm);
        return ResponseUtil.success();
    }
}
