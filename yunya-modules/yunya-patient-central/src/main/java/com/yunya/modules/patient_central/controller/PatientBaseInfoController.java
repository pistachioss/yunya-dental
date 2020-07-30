package com.yunya.modules.patient_central.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import com.yunya.feign.patient_central.domain.model.PatientBaseInfoModel;
import com.yunya.feign.patient_central.domain.model.PatientExtendInfoModel;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简单介绍:</br> 患者资料模块控制器
 *
 * @author: WY
 * @date 2020/7/24 19:51
 * @description: 患者资料 患者信息
 * @since: 1.0.0
 */
@Api(value = "患者信息", description = "患者信息（增删查改）")
@RestController
@RequestMapping("patient")
public class PatientBaseInfoController {

    /** 注入服务 */
    private final PatientBaseInfoBiz patientBaseInfoBiz;


    public PatientBaseInfoController(PatientBaseInfoBiz patientBaseInfoBiz) {
        this.patientBaseInfoBiz = patientBaseInfoBiz;
    }

    @ApiOperation("根据Id查询患者信息公用信息")
    @GetMapping("/findPatientPublicInfoById/{id}")
    public ResponseResult findPatientPublicInfoById(@PathVariable Integer id){
        return ResponseUtil.success(patientBaseInfoBiz.findPatientPublicInfoById(id));
    }

    @ApiOperation("根据姓名和手机号判断是否已存在")
    @PostMapping("/findUserExists")
    public ResponseResult findUserExists(@RequestBody @Validated PatientBaseInfoQueryForm patientBaseInfoQueryForm){
        return patientBaseInfoBiz.findUserExists(patientBaseInfoQueryForm);
    }

    @CurrentUser
    @ApiOperation("添加患者基本信息信息")
    @PostMapping("/add")
    public ResponseResult addPatient(@RequestBody @Validated  PatientBaseInfoModel patientBaseInfoModel){
        patientBaseInfoBiz.addPatient(patientBaseInfoModel);
        return ResponseUtil.success();
    }

    @ApiOperation("添加完善患者基本信息")
    @PostMapping("/addPatientInfo")
    public ResponseResult addPatientInfo(@RequestBody @Validated PatientExtendInfoModel patientExtendInfoModel){
        patientBaseInfoBiz.addPatientInfo(patientExtendInfoModel);
        return ResponseUtil.success();
    }

    @ApiOperation("根据患者id查询患者资料")
    @GetMapping ("/findPatientDate/{id}")
    public ResponseResult findPatientDate(@PathVariable Integer id){
        return ResponseUtil.success(patientBaseInfoBiz.findPatientDate(id));
    }








}
