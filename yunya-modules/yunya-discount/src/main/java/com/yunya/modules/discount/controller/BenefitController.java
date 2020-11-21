package com.yunya.modules.discount.controller;

import com.yunya.feign.discount.domain.query.PatientBenefitQuery;
import com.yunya.feign.discount.domain.vo.PatientOptionalBenefitVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.biz.BenefitBiz;
import com.yunya.modules.discount.biz.CardBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
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
	@Resource
	private RemoteSystemServiceFeign systemServiceFeign;

	@ApiOperation(value = "收费-打开选择优惠页面")
	@PostMapping("/order/benefit/init")
	@CurrentUser
	public ResponseResult<PatientOptionalBenefitVo> initBenefit(@Valid @RequestBody PatientBenefitQuery query) {
		PatientOptionalBenefitVo benefit = cardBiz.initBenefit(query);
		return ResponseUtil.success(benefit);
	}

	@ApiOperation(value = "查询账单使用优惠券信息")
	@PostMapping("/{orderId}/benefit/coupon")
	@CurrentUser
	public ResponseResult<String> getCardUseCoupon(@PathVariable(value = "orderId") Integer orderId) {
		return ResponseUtil.success(benefitBiz.getOrderCoupon(orderId));
	}

}
