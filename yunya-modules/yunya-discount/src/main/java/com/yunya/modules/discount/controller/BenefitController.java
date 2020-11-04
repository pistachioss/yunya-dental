package com.yunya.modules.discount.controller;

import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.model.AuthDiscountBenefitModel;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.discount.domain.query.PatientBenefitQuery;
import com.yunya.feign.discount.domain.vo.PatientOptionalBenefitVo;
import com.yunya.feign.discount.domain.vo.PatientOrderBenefitVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.biz.BenefitBiz;
import com.yunya.modules.discount.biz.CardBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author xiangyang
 * @date 2020/10/9
 */
@Api(tags = {"收费使用优惠"})
@RestController
public class BenefitController {

    @Resource
    private CardBiz cardBiz;

    @Resource
    private BenefitBiz benefitBiz;

    @ApiOperation(value = "收费-打开选择优惠页面")
    @PostMapping("/order/benefit/init")
    @CurrentUser
    public ResponseResult<PatientOptionalBenefitVo> initBenefit(@Valid @RequestBody PatientBenefitQuery query) {
        PatientOptionalBenefitVo benefit = cardBiz.initBenefit(query);
        return ResponseUtil.success(benefit);
    }

    @ApiOperation(value = "收费-选择优惠")
    @PostMapping("/order/choice/benefit")
    @CurrentUser
    public ResponseResult<PatientOrderBenefitVo> choiceBenefit(@Valid @RequestBody PatientChooseBenefitForm form) {
        return cardBiz.choiceBenefit(form);
    }

    @ApiOperation(value = "收费-确认收费-保存优惠券优惠")
    @PostMapping("/order/benefit/save")
    @CurrentUser
    public ResponseResult<PatientOrderBenefitVo> saveCardBenefit(@Valid @RequestBody PatientOrderBenefitModel model) {
        return benefitBiz.saveCardBenefit(model);
    }

    @ApiOperation(value = "收费-确认收费-授权折扣优惠")
    @PostMapping("/order/benefit/authorization/save")
    @CurrentUser
    public ResponseResult<PatientOrderBenefitVo> saveAuthBenefit(@Valid @RequestBody AuthDiscountBenefitModel model) {
        return benefitBiz.saveAuthBenefit(model);
    }
}
