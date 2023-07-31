package com.yunya.modules.discount.controller;

import com.yunya.feign.discount.domain.form.DeductionActiveForm;
import com.yunya.feign.discount.domain.form.DeductionAllocateForm;
import com.yunya.feign.discount.domain.form.DeductionChangeForm;
import com.yunya.feign.discount.domain.model.DeductionRefundModel;
import com.yunya.feign.discount.domain.query.CouponRefundQuery;
import com.yunya.feign.discount.domain.query.DeductionOrderQuery;
import com.yunya.feign.discount.domain.query.DeductionPatientQuery;
import com.yunya.feign.discount.domain.vo.PatientDeductionBaseVO;
import com.yunya.feign.discount.domain.vo.PatientDeductionOrderVO;
import com.yunya.feign.discount.domain.vo.PatientRefundOrderVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.biz.CardBiz;
import com.yunya.modules.discount.biz.DeductionPatientBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/patient/deduction/goods/list")
    public ResponseResult<List<PatientDeductionBaseVO>> deductionList(@Valid @RequestBody DeductionPatientQuery query) {
        return ResponseUtil.success(patientBiz.deductionList(query));
    }

    @ApiOperation(value = "订单列表")
    @PostMapping("/patient/deduction/order/list")
    public ResponseResult<List<PatientDeductionOrderVO>> orderList(@Valid @RequestBody DeductionOrderQuery query) {
        return ResponseUtil.success(patientBiz.orderList(query));
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

    @ApiOperation(value = "退款信息")
    @PostMapping("/deduction/refund/info")
    public ResponseResult<PatientRefundOrderVO> refundDetail(@Valid @RequestBody CouponRefundQuery query) {
        return ResponseUtil.success(patientBiz.refundDetail(query));
    }

    @ApiOperation(value = "退费")
    @PostMapping("/patient/deduction/refund")
    @CurrentUser
    public ResponseResult<Boolean> refund(@RequestBody DeductionRefundModel model) {
        patientBiz.refund(model);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "分配卡号")
    @PostMapping("/patient/deduction/allocate")
    public ResponseResult<String> allocate(@Valid @RequestBody DeductionAllocateForm form) {
        return ResponseUtil.success(patientBiz.allocate(form));
    }

//    @ApiOperation(value = "订单记录")
//    @PostMapping("/patient/coupon/goods/category")
//    @CurrentUser
//    public ResponseResult<Set<DeductionCategoryVO>> category() {
//        return ResponseUtil.success(couponGoodsBiz.category());
//    }
}
