package com.yunya.middletable.controller.emr;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.emr.TreatPlanRecordBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介:报表中间表随访/提醒控制器
 *
 * @author: WY
 * @date: 2020/10/20 19:11
 * @description:
 * @since: 1.0.0
 */

@RestController
    @RequestMapping("treatPlanDetail")
public class TreatPlanDetailController {
    /** 注入业务成 */
    @Autowired
    TreatPlanRecordBiz treatPlanRecordBiz;

    /**
     * 变更治疗计划明细核销记录操作
     * @param model 提醒随访model
     * @return
     */
    @PostMapping("/operate")
    public ResponseResult<T> operate(@RequestBody @Validated MessageModel model) {
        treatPlanRecordBiz.operate(model);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件拉取提醒随访数据并更新中间表
     *
     * @param form 拉取时间
     * @return ResponseResult
     */
    @ApiOperation("根据时间段批量拉取提醒随访信息")
    @PostMapping(value = "/batch", name = "PatientBaseInfoBiz")
    public ResponseResult<T> pullPatientData(@RequestBody PullForm form) throws InterruptedException {
//        treatPlanRecordBiz.pullPatientData(form);
        return ResponseUtil.success(null);
    }
}