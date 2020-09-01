//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.form.PatientPhotoForm;
import com.yunya.feign.patient_central.domain.form.PictureForm;
import com.yunya.feign.patient_central.domain.model.PatientBaseInfoModel;
import com.yunya.feign.patient_central.domain.model.PatientExtendInfoModel;
import com.yunya.feign.patient_central.domain.model.PatientWoPlatformInfoModel;
import com.yunya.feign.patient_central.domain.model.PictureModel;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(
        value = "患者信息",
        description = "患者信息（增删查改）"
)
@RestController
@RequestMapping({"central"})
public class PatientBaseInfoController {
    private final PatientBaseInfoBiz patientBaseInfoBiz;

    public PatientBaseInfoController(PatientBaseInfoBiz patientBaseInfoBiz) {
        this.patientBaseInfoBiz = patientBaseInfoBiz;
    }

    @ApiOperation("根据Id查询患者信息公用信息")
    @GetMapping({"/findPatientPublicInfoById/{id}"})
    public ResponseResult findPatientPublicInfoById(@PathVariable("id") Integer id) {
        return ResponseUtil.success(this.patientBaseInfoBiz.findPatientPublicInfoById(id));
    }

    @ApiOperation("根据姓名和手机号判断是否已存在")
    @PostMapping({"/findUserExists"})
    public ResponseResult findUserExists(@RequestBody PatientBaseInfoQueryForm patientBaseInfoQueryForm) {
        return this.patientBaseInfoBiz.findUserExists(patientBaseInfoQueryForm);
    }

    @CurrentUser
    @ApiOperation("添加完善患者基本信息")
    @PostMapping({"/addPatientInfo"})
    public ResponseResult addPatientInfo(@RequestBody @Validated PatientExtendInfoModel patientExtendInfoModel) {
        this.patientBaseInfoBiz.addPatientInfo(patientExtendInfoModel);
        return ResponseUtil.success();
    }

    @ApiOperation("根据患者id查询患者资料")
    @GetMapping({"/findPatientDataInfo/{id}"})
    public ResponseResult findPatientData(@PathVariable("id") Integer id) {
        return ResponseUtil.success(this.patientBaseInfoBiz.findPatientData(id));
    }

    @ApiOperation("根据姓名/病例编号/手机号/姓名拼音模糊查询患者")
    @PostMapping({"/findPatientByNameAndMobile"})
    public ResponseResult findPatientByNameAndMobile(@RequestBody PatientLikeFinleQueryForm patientBaseInfoQueryForm) {
        return ResponseUtil.success(this.patientBaseInfoBiz.findPatientByNameAndMobile(patientBaseInfoQueryForm));
    }

    @CurrentUser
    @ApiOperation("添加患者基本信息信息")
    @PostMapping({"/add"})
    public ResponseResult addPatient(@RequestBody @Validated PatientBaseInfoModel patientBaseInfoModel) {
        return ResponseUtil.success(this.patientBaseInfoBiz.addPatient(patientBaseInfoModel));
    }

    @ApiOperation("拍照")
    @GetMapping({"/takeAPhoto/{patientId}"})
    public ResponseResult takeAPhoto(@PathVariable("patientId") Integer patientId) {
        this.patientBaseInfoBiz.takeAPhoto(patientId);
        return ResponseUtil.success();
    }

    @ApiOperation("获取照片")
    @GetMapping({"/getFaceUrl/{patientId}"})
    public ResponseResult getFaceUrl(@PathVariable("patientId") Integer patientId) {
        return ResponseUtil.success(this.patientBaseInfoBiz.getFaceUrl(patientId));
    }

    @ApiOperation("删除照片")
    @DeleteMapping({"/deleteThePhoto"})
    public ResponseResult deleteThePhoto(@RequestBody @Validated PictureForm pictureForm) {
        return ResponseUtil.success(this.patientBaseInfoBiz.deleteThePhoto(pictureForm));
    }

    @ApiOperation("设备人员认证授权")
    @PostMapping({"/equipmenAuthorization"})
    public ResponseResult equipmenAuthorization(@RequestBody @Validated PictureModel pictureModel) {
        this.patientBaseInfoBiz.equipmenAuthorization(pictureModel);
        return ResponseUtil.success();
    }

    @IgnoreUserToken
    @ApiOperation(value = "测试人脸识别认证返回")
    @RequestMapping(value = {"/renlianshibie"}, method = {RequestMethod.POST})
    public ResponseResult renlianshibie(PatientWoPlatformInfoModel patientWoPlatformInfoModel) {
        this.patientBaseInfoBiz.renlianshibie(patientWoPlatformInfoModel);
        return ResponseUtil.success();

    }

    @ApiOperation("根据患者id查询来访信息")
    @GetMapping({"/visitInfo/{id}"})
    public ResponseResult findPatientVisitInfo(@PathVariable("id") Integer id) {
        return ResponseUtil.success(this.patientBaseInfoBiz.findPatientVisitInfo(id));
    }

    @ApiOperation("编辑头像")
    @PostMapping({"/uptPhoto"})
    public ResponseResult uptPhoto(@RequestBody PatientPhotoForm patientPhotoForm) {
        this.patientBaseInfoBiz.uptPhoto(patientPhotoForm);
        return ResponseUtil.success();
    }
}
