package com.yunya.middletable.controller.report;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.treatment_other.BaseVisitRemindBiz;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@RestController
public class BaseVisitRemindRecordController {
    @Autowired
    private BaseVisitRemindBiz baseVisitRemindBiz;
    /**
     * 导入随访提醒计划时间
     *
     */
    @ApiOperation("导入随访提醒计划时间")
    @PostMapping("/dsj")
    public ResponseResult<Integer> dsj(@RequestBody @Validated PullForm pullForm) {
        int a = baseVisitRemindBiz.dsj(pullForm);
        return ResponseUtil.success(a);
    }
}
