package com.yunya.middletable.controller.discount;

import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseCardServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
public class BaseCardController {

	@Resource
	BaseCardServiceImpl cardService;

	@PostMapping("/base/card/pull")
	public ResponseResult pullData(@RequestBody PullForm form) throws ExecutionException, InterruptedException {
		long start = System.currentTimeMillis();
		RestErrorBo errorBo = cardService.pullCard(form.getStartDate(), form.getEndDate());
		if (errorBo.getError() != null) {
			return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
		}
		long end = System.currentTimeMillis();
		log.info("【中间表同步】卡券总时长：[{}]", end - start);
		return ResponseUtil.success(end - start);
	}

	@PostMapping("/base/card/msg/send")
	public ResponseResult sendMessage(@RequestBody MessageModel model) {
		cardService.operateSingle(model);
		return ResponseUtil.success();
	}

	@PostMapping("/base/card/generate/msg/send")
	public ResponseResult sendBatchMessage(@RequestBody MessageModel model) throws InterruptedException {
		cardService.operateBatch(model);
		return ResponseUtil.success();
	}


}
