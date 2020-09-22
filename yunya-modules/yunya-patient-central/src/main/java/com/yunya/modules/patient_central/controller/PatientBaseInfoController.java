//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.form.PatientPhotoForm;
import com.yunya.feign.patient_central.domain.form.PictureForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.PatientExtendInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientPublicInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientVisitInfoVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 简介: 患者信息控制器
 *
 * @author: WY
 * @date: 2020/9/18 11:14
 * @description:
 * @since: 1.0.0
 */
@Api(value = "患者信息", description = "患者信息（增删查改）")
@RestController
@RequestMapping("central")
public class PatientBaseInfoController {

  /** 注入对象 */
  private final PatientBaseInfoBiz patientBaseInfoBiz;

  public PatientBaseInfoController(PatientBaseInfoBiz patientBaseInfoBiz) {
    this.patientBaseInfoBiz = patientBaseInfoBiz;
  }

  /**
   * 根据Id查询患者信息公用信息
   * @param id 患者id
   * @return ResponseResult<PatientPublicInfoVo>
   */
  @ApiOperation("根据Id查询患者信息公用信息")
  @GetMapping("/publicInformation/{id}")
  public ResponseResult<PatientPublicInfoVo> findPatientPublicInfoById(
      @PathVariable("id") Integer id) {
    return ResponseUtil.success(this.patientBaseInfoBiz.findPatientPublicInfoById(id));
  }

  /**
   * 根据姓名和手机号判断是否已存在
   * @param patientBaseInfoQueryForm 患者信息查询QueryFrom
   * @return ResponseResult
   */
  @ApiOperation("根据姓名和手机号判断是否已存在")
  @PostMapping("/userExistsFind")
  public ResponseResult findUserExists(
      @RequestBody PatientBaseInfoQueryForm patientBaseInfoQueryForm) {
    return this.patientBaseInfoBiz.findUserExists(patientBaseInfoQueryForm);
  }

  /**
   * 添加完善患者基本信息
   * @param patientExtendInfoModel 基本信息+扩展信息+其他信息 参数模板
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("添加完善患者基本信息")
  @PostMapping("/patientInfoAdd")
  public ResponseResult addPatientInfo(
      @RequestBody @Validated PatientExtendInfoModel patientExtendInfoModel) {
    this.patientBaseInfoBiz.addPatientInfo(patientExtendInfoModel);
    return ResponseUtil.success();
  }

  /**
   * 根据患者id查询患者资料
   * @param id 患者id
   * @return ResponseResult<PatientExtendInfoVo>
   */
  @ApiOperation("根据患者id查询患者资料")
  @GetMapping("/patientDataInfoFind/{id}")
  public ResponseResult<PatientExtendInfoVo> findPatientData(@PathVariable("id") Integer id) {
    return this.patientBaseInfoBiz.findPatientData(id);
  }

  /**
   * 据姓名/病例编号/手机号/姓名拼音模糊查询患者
   * @param patientBaseInfoQueryForm 患者模糊查询模板
   * @return ResponseResult
   */
  @ApiOperation("根据姓名/病例编号/手机号/姓名拼音模糊查询患者")
  @PostMapping("/likePatient")
  public ResponseResult findPatientByNameAndMobile(
      @RequestBody PatientLikeFinleQueryForm patientBaseInfoQueryForm) {
    return ResponseUtil.success(
        this.patientBaseInfoBiz.findPatientByNameAndMobile(patientBaseInfoQueryForm));
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

  /**
   * 拍照
   * @param patientId 患者id
   * @return ResponseResult
   */
  @ApiOperation("拍照")
  @GetMapping("/takeAPhoto/{patientId}")
  public ResponseResult takeAPhoto(@PathVariable("patientId") Integer patientId) {
    this.patientBaseInfoBiz.takeAPhoto(patientId);
    return ResponseUtil.success();
  }

  /**
   * 获取照片
   * @param patientId 患者id
   * @return ResponseResult
   */
  @ApiOperation("获取照片")
  @GetMapping("/getFaceUrl/{patientId}")
  public ResponseResult getFaceUrl(@PathVariable("patientId") Integer patientId) {
    return ResponseUtil.success(this.patientBaseInfoBiz.getFaceUrl(patientId));
  }

  /**
   * 删除照片
   * @param pictureForm Wo平台照片删除Form
   * @return ResponseResult
   */
  @ApiOperation("删除照片")
  @DeleteMapping("/deleteThePhoto")
  public ResponseResult deleteThePhoto(@RequestBody @Validated PictureForm pictureForm) {
    return ResponseUtil.success(this.patientBaseInfoBiz.deleteThePhoto(pictureForm));
  }

  /**
   * 设备人员认证授权
   * @param pictureModel 设备授权model
   * @return ResponseResult
   */
  @ApiOperation("设备人员认证授权")
  @PostMapping("/equipmenAuthorization")
  public ResponseResult equipmenAuthorization(@RequestBody @Validated PictureModel pictureModel) {
    this.patientBaseInfoBiz.equipmenAuthorization(pictureModel);
    return ResponseUtil.success();
  }

  /**
   * 测试人脸识别认证返回
   * @param patientWoPlatformInfoModel 回调model
   * @return Map<String, Object>
   */
  @IgnoreUserToken
  @ApiOperation(value = "测试人脸识别认证返回")
  @RequestMapping(value = "/faceRecognition", method = {RequestMethod.POST})
  public Map<String, Object> faceRecognition(@RequestBody PatientWoPlatformInfoModel patientWoPlatformInfoModel) {
    this.patientBaseInfoBiz.renlianshibie(patientWoPlatformInfoModel);
    Map<String, Object> map = new HashMap<>();
    map.put("result", 1);
    map.put("success", true);
    return map;
  }

  /**
   * 测试拍照回调
   * @param picturesCallbackInfoModel 测试拍照回调
   * @return ResponseResult
   */
  @IgnoreUserToken
  @ApiOperation(value = "测试拍照回调")
  @RequestMapping(value = "/takePictures", method = {RequestMethod.POST})
  public ResponseResult takePictures(@RequestBody PicturesCallbackInfoModel picturesCallbackInfoModel) {
    this.patientBaseInfoBiz.takePictures(picturesCallbackInfoModel);
    return ResponseUtil.success();
  }

  /**
   * 根据患者id查询来访信息
   * @param id 患者id
   * @return ResponseResult<PatientVisitInfoVo>
   */
  @ApiOperation("根据患者id查询来访信息")
  @GetMapping("/visitInfo/{id}")
  public ResponseResult<PatientVisitInfoVo> findPatientVisitInfo(@PathVariable("id") Integer id) {
    return ResponseUtil.success(this.patientBaseInfoBiz.findPatientVisitInfo(id));
  }

  /**
   * 编辑头像
   * @param patientPhotoForm 编辑患者头像
   * @return ResponseResult
   */
  @ApiOperation("编辑头像")
  @PostMapping("/uptPhoto")
  public ResponseResult uptPhoto(@RequestBody PatientPhotoForm patientPhotoForm) {
    this.patientBaseInfoBiz.uptPhoto(patientPhotoForm);
    return ResponseUtil.success();
  }
}
