package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.model.PatientBaseInfoModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介:
 *
 * @author: YK
 * @date: 2020/9/21 10:29
 * @description:
 * @since: 1.0.0
 */
@Api(value = "app端患者信息", description = "患者信息（增删查改）")
@RestController
@RequestMapping("app/central")
public class AppPatientBaseInfoController {

    /** 注入对象 */
    private final PatientBaseInfoBiz patientBaseInfoBiz;

    public AppPatientBaseInfoController(PatientBaseInfoBiz patientBaseInfoBiz) {
        this.patientBaseInfoBiz = patientBaseInfoBiz;
    }


    /**
     * 添加患者基本信息信息
     * @param patientBaseInfoModel 新增患者信息
     * @return ResponseResult
     */
    @CurrentUser
    @ApiOperation("添加患者基本信息信息")
    @PostMapping("/add")
    public ResponseResult addPatient(
            @RequestBody @Validated PatientBaseInfoModel patientBaseInfoModel) {
        return ResponseUtil.success(this.patientBaseInfoBiz.addPatient(patientBaseInfoModel));
    }


}