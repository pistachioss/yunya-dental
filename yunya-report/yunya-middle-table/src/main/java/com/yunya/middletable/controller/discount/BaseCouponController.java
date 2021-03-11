package com.yunya.middletable.controller.discount;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseCouponServiceImpl;
import com.yunya.middletable.service.discount.BaseCouponServiceNewImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Slf4j
@RestController
public class BaseCouponController {

	@Resource
	BaseCouponServiceImpl couponService;
	@Resource
	BaseCouponServiceNewImpl couponService1;

	@PostMapping("/base/coupon/pull")
	public ResponseResult pullData(@RequestBody PullForm form) throws InterruptedException {
		long start = System.currentTimeMillis();
		couponService1.pullCoupon(form.getStartDate(), form.getEndDate());
		long end = System.currentTimeMillis();
		log.info("【中间表同步】产品设计总时长：[{}]", end - start);
		return ResponseUtil.success(end - start);
	}

	@PostMapping("/base/coupon/msg/send")
	public ResponseResult sendMessage(@RequestBody MessageModel model) {
		couponService.operateBaseCoupon(model);
		return ResponseUtil.success();
	}

}
