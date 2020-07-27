package com.yunya.modules.patient_central.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import com.yunya.modules.patient_central.domain.model.PatientBaseInfoModel;
import com.yunya.modules.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.modules.patient_central.domain.vo.PatientBaseInfoVo;
import com.yunya.modules.patient_central.domain.vo.PatientPublicInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简单介绍:</br> 患者资料（患者信息 增删查改）
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

    @ApiOperation("添加患者信息")
    @PostMapping("/add")
    public ResponseResult addPatient(@PathVariable @Validated PatientBaseInfoModel patientBaseInfoModel){
        patientBaseInfoBiz.addPatient(patientBaseInfoModel);
        return ResponseUtil.success();
    }







}
