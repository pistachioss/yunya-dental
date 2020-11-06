package com.yunya.modules.patient_central.controller.web;

import com.yunya.feign.patient_central.domain.model.CustomerRegistrationModel;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.CustomerRegistrationBiz;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 客户登记
 *
 * @author: WY
 * @date: 2020/11/5 16:51
 * @description: 客户登记控制层
 * @since: 1.0.0
 */
@RestController
@RequestMapping("registration")
public class CustomerRegistrationController {

    /** 注入服务 */
    @Autowired private CustomerRegistrationBiz customerRegistrationBiz;

    /** 患者服务 */
    @Autowired private PatientBaseInfoBiz patientBaseInfoBiz;

    /**
     * 添加患者基本信息信息
     *
     * @param customerRegistrationModel 新增患者信息
     * @return ResponseResult
     */
    @RepeatSubmit
    @CurrentUser
    @ApiOperation("添加患者基本信息信息")
    @PostMapping("/permit/add")
    public ResponseResult addPatient(
            @RequestBody @Validated CustomerRegistrationModel customerRegistrationModel) {
        return ResponseUtil.success(this.customerRegistrationBiz.addPatient(customerRegistrationModel));
    }

    /**
     * 根据姓名和手机号判断是否已存在
     *
     * @param patientBaseInfoQueryForm 患者信息查询QueryFrom
     * @return ResponseResult
     */
    @ApiOperation("根据姓名和手机号判断是否已存在")
    @PostMapping("/permit/userExistsFind")
    public ResponseResult findUserExists(
            @RequestBody PatientBaseInfoQueryForm patientBaseInfoQueryForm) {
        return this.patientBaseInfoBiz.findUserExists(patientBaseInfoQueryForm);
    }
}