package com.yunya.modules.discount.controller;

import com.yunya.feign.discount.domain.form.DeductionActiveForm;
import com.yunya.feign.discount.domain.form.DeductionChangeForm;
import com.yunya.feign.discount.domain.query.DeductionOrderQuery;
import com.yunya.feign.discount.domain.query.DeductionPatientQuery;
import com.yunya.feign.discount.domain.vo.PatientDeductionBaseVO;
import com.yunya.feign.discount.domain.vo.PatientDeductionOrderVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.biz.CardBiz;
import com.yunya.modules.discount.biz.DeductionPatientBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @auther: xy
 * @date: 2023/6/28
 */
@Api(tags = {"患者档案-划扣卡接口"})
@RestController
public class DeductionPatientController {
    @Resource
    private DeductionPatientBiz patientBiz;
    @Resource
    private CardBiz cardBiz;

    @ApiOperation(value = "产品列表")
    @GetMapping("/{patientId}/deduction/goods/list")
    public ResponseResult<List<PatientDeductionBaseVO>> deductionList(@PathVariable(value = "patientId") Integer patientId, @Valid @RequestBody DeductionPatientQuery query) {
        return ResponseUtil.success(patientBiz.deductionList(patientId, query));
    }

    @ApiOperation(value = "订单列表")
    @GetMapping("/{patientId}/deduction/order/list")
    public ResponseResult<List<PatientDeductionOrderVO>> orderList(@PathVariable(value = "patientId") Integer patientId, @Valid @RequestBody DeductionOrderQuery query) {
        return ResponseUtil.success(patientBiz.orderList(patientId, query));
    }

    @ApiOperation(value = "激活")
    @PostMapping("/patient/deduction/active")
    @CurrentUser
    public ResponseResult<Boolean> active(@RequestBody DeductionActiveForm form) {
        patientBiz.active(form);
        return ResponseUtil.success(true);
    }

    @ApiOperation(value = "取消激活")
    @PostMapping("/patient/deduction/cancelActive")
    @CurrentUser
    public ResponseResult<Boolean> cancelActive(@RequestBody DeductionActiveForm form) {
        patientBiz.cancelActive(form);
        return ResponseUtil.success(true);
    }

    @ApiOperation(value = "转赠")
    @PostMapping("/patient/deduction/change")
    @CurrentUser
    public ResponseResult<Boolean> change(@RequestBody DeductionChangeForm form) {
        patientBiz.change(form);
        return ResponseUtil.success(true);
    }

    @ApiOperation(value = "退费")
    @PostMapping("/patient/deduction/refund")
    @CurrentUser
    public ResponseResult<Boolean> refund(@RequestBody DeductionChangeForm form) {
        patientBiz.change(form);
        return ResponseUtil.success(true);
    }

//    @ApiOperation(value = "订单记录")
//    @PostMapping("/patient/coupon/goods/category")
//    @CurrentUser
//    public ResponseResult<Set<DeductionCategoryVO>> category() {
//        return ResponseUtil.success(couponGoodsBiz.category());
//    }
}
