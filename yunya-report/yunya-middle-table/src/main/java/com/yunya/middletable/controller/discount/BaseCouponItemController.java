package com.yunya.middletable.controller.discount;

import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseCouponItemServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class BaseCouponItemController {

	@Resource
	BaseCouponItemServiceImpl itemService;

	@PostMapping("/base/coupon/item/pull")
	public ResponseResult pullData(@RequestBody PullForm form) {
		long start = System.currentTimeMillis();
		RestErrorBo errorBo = itemService.pullCouponItem(form.getStartDate(), form.getEndDate());
		if (errorBo.getError() != null) {
			return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
		}
		long end = System.currentTimeMillis();
		log.info("【中间表同步】产品项目总时长：[{}]", end - start);
		return ResponseUtil.success(end - start);
	}

	@PostMapping("/base/coupon/item/msg/send")
	public ResponseResult sendMessage(@RequestBody MessageModel model) {
		RestErrorBo errorBo = itemService.operateBaseCouponItem(model);
		if (errorBo.getError() != null) {
			return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
		}
		return ResponseUtil.success();
	}

}
