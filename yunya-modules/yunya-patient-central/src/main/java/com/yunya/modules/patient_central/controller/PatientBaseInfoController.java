package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.form.PictureForm;
import com.yunya.feign.patient_central.domain.model.PatientBaseInfoModel;
import com.yunya.feign.patient_central.domain.model.PatientExtendInfoModel;
import com.yunya.feign.patient_central.domain.model.PatientWoPlatformInfoModel;
import com.yunya.feign.patient_central.domain.model.PictureModel;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.PatientBaseInfoVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
@RequestMapping("central")
public class PatientBaseInfoController {

    /** 注入服务 */
    private final PatientBaseInfoBiz patientBaseInfoBiz;


    public PatientBaseInfoController(PatientBaseInfoBiz patientBaseInfoBiz) {
        this.patientBaseInfoBiz = patientBaseInfoBiz;
    }

    @ApiOperation("根据Id查询患者信息公用信息")
    @GetMapping("/findPatientPublicInfoById/{id}")
    public ResponseResult findPatientPublicInfoById(@PathVariable("id") Integer id){
        return ResponseUtil.success(patientBaseInfoBiz.findPatientPublicInfoById(id));
    }

    @ApiOperation("根据姓名和手机号判断是否已存在")
    @PostMapping("/findUserExists")
    public ResponseResult findUserExists(@RequestBody PatientBaseInfoQueryForm patientBaseInfoQueryForm){
        return patientBaseInfoBiz.findUserExists(patientBaseInfoQueryForm);
    }

    @CurrentUser
    @ApiOperation("添加完善患者基本信息")
    @PostMapping("/addPatientInfo")
    public ResponseResult addPatientInfo(@RequestBody @Validated PatientExtendInfoModel patientExtendInfoModel){
        patientBaseInfoBiz.addPatientInfo(patientExtendInfoModel);
        return ResponseUtil.success();
    }

    @ApiOperation("根据患者id查询患者资料")
    @GetMapping ("/findPatientData/{id}")
    public ResponseResult findPatientData(@PathVariable("id") Integer id){
        return ResponseUtil.success(patientBaseInfoBiz.findPatientData(id));
    }

    @ApiOperation("根据姓名/病例编号/手机号/姓名拼音模糊查询患者")
    @PostMapping ("/findPatientByNameAndMobile")
    public ResponseResult findPatientByNameAndMobile(@RequestBody PatientLikeFinleQueryForm patientBaseInfoQueryForm){
        return ResponseUtil.success(patientBaseInfoBiz.findPatientByNameAndMobile(patientBaseInfoQueryForm));
    }

    @ApiOperation("根据输入年龄计算出生年份")
    @GetMapping("/birth/{age}")
    public Date birthYear(@PathVariable("age") Integer age) {
        return patientBaseInfoBiz.birthYear(age);
    }

    @CurrentUser
    @ApiOperation("添加患者基本信息信息")
    @PostMapping("/add")
    public ResponseResult addPatient(@RequestBody @Validated PatientBaseInfoModel patientBaseInfoModel){
        return ResponseUtil.success(patientBaseInfoBiz.addPatient(patientBaseInfoModel));
    }

    @ApiOperation(value = "拍照")
    @GetMapping("/takeAPhoto/{patientId}")
    public ResponseResult takeAPhoto(@PathVariable("patientId") Integer patientId){
        patientBaseInfoBiz.takeAPhoto(patientId);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "获取照片")
    @GetMapping("/getFaceUrl/{patientId}")
    public ResponseResult getFaceUrl(@PathVariable("patientId") Integer patientId){
        return ResponseUtil.success(patientBaseInfoBiz.getFaceUrl(patientId));
    }

    @ApiOperation(value = "删除照片")
    @DeleteMapping("/deleteThePhoto")
    public ResponseResult deleteThePhoto(@RequestBody @Validated PictureForm pictureForm){
        return ResponseUtil.success(patientBaseInfoBiz.deleteThePhoto(pictureForm));
    }

    @ApiOperation("设备人员认证授权")
    @PostMapping("/equipmenAuthorization")
    public ResponseResult equipmenAuthorization(@RequestBody @Validated PictureModel pictureModel){
        patientBaseInfoBiz.equipmenAuthorization(pictureModel);
        return ResponseUtil.success();
    }

    @IgnoreUserToken
    @ApiOperation(value = "测试人脸识别认证返回", notes = "测试人脸识别认证返回")
    @RequestMapping(value = "/renlianshibie", method = RequestMethod.POST)
    public ResponseResult renlianshibie(PatientWoPlatformInfoModel patientWoPlatformInfoModel ){
        patientBaseInfoBiz.renlianshibie(patientWoPlatformInfoModel);
        return ResponseUtil.success();
    }

    @ApiOperation("根据患者id查询来访信息")
    @GetMapping("/findPatientVisitInfo/{id}")
    public ResponseResult findPatientVisitInfo(@PathVariable("id") Integer id){
        return ResponseUtil.success(patientBaseInfoBiz.findPatientVisitInfo(id));
    }




}
