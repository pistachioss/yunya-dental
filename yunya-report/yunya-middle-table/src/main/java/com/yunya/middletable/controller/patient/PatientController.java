package com.yunya.middletable.controller.patient;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.patient.BasePatientBiz;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介:报表中间表患者信息控制器
 *
 * @author: WY
 * @date: 2020/10/15 13:20
 * @description:
 * @since: 1.0.0
 */

@RestController
@RequestMapping("patient")
public class PatientController {
    /** 注入服务 */
    @Autowired private BasePatientBiz basePatientBiz;

    @PostMapping("/operate")
    public ResponseResult<T> operate(@RequestBody @Validated MessageModel model) {
        basePatientBiz.operate(model);
        return ResponseUtil.success(null);
    }

    @PostMapping("/patient/pull")
    public ResponseResult pullData(@RequestBody PullForm form) {
        basePatientBiz.pullPatient(form.getStartDate(), form.getEndDate());
        return ResponseUtil.success();
    }

}