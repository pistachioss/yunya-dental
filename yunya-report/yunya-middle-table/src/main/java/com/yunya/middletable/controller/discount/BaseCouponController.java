package com.yunya.middletable.controller.discount;

import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseCouponServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class BaseCouponController {

	@Autowired
	BaseCouponServiceImpl couponService;

	@PostMapping("/base/coupon/pull")
	public ResponseResult pullData(@RequestBody PullForm form) {
		long start = System.currentTimeMillis();
		RestErrorBo errorBo = couponService.pullCoupon(form.getStartDate(), form.getEndDate());
		if (errorBo.getError() != null) {
			return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
		}
		long end = System.currentTimeMillis();
		log.info("总时长：[{}]", end - start);
		return ResponseUtil.success(end - start);
	}

	@PostMapping("/base/coupon/msg/send")
	public ResponseResult sendMessage(@RequestBody MessageModel model) {
		couponService.operateBaseCoupon(model);
		return ResponseUtil.success();
	}

}
