package com.yunya.middletable.controller.discount;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseBenefitServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class BaseBenefitController {

	@Resource
	BaseBenefitServiceImpl benefitService;

	@PostMapping("/base/benefit/pull")
	public ResponseResult pullData(@RequestBody PullForm form) throws InterruptedException {
		long start = System.currentTimeMillis();
//		RestErrorBo errorBo = benefitService.pullBenefit(form.getStartDate(), form.getEndDate());
//		if (errorBo.getError() != null) {
//			return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
//		}
		long end = System.currentTimeMillis();
		log.info("【中间表同步】卡券优惠总时长：[{}]", end - start);
		return ResponseUtil.success(end - start);
	}

	@PostMapping("/base/benefit/msg/send")
	public ResponseResult sendMessage(@RequestBody MessageModel model) throws InterruptedException {
		benefitService.operateBaseBenefit(model);
		return ResponseUtil.success();
	}

}
