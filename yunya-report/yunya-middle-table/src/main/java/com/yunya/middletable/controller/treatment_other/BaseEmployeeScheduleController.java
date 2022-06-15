package com.yunya.middletable.controller.treatment_other;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.treatment_other.BaseEmployeeScheduleBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介:报表中间表员工排班控制器
 *
 * @author: WY
 * @date: 2020/10/20 19:11
 * @description:
 * @since: 1.0.0
 */

@RestController
    @RequestMapping("employeeSchedule")
public class BaseEmployeeScheduleController {
    /** 注入业务成 */
    @Autowired
    private BaseEmployeeScheduleBiz baseEmployeeScheduleBiz;

    /**
     * 提醒随访操作
     * @param model 提醒随访model
     * @return
     */
    @PostMapping("/operate")
    public ResponseResult<T> operate(@RequestBody @Validated MessageModel model) {
        baseEmployeeScheduleBiz.operateEmployeeSchedule(model);
        return ResponseUtil.success(null);
    }
}