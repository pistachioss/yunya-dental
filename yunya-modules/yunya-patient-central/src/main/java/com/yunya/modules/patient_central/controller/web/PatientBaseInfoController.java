//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunya.modules.patient_central.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.model.PatientBaseInfoModel;
import com.yunya.feign.patient_central.domain.model.PatientExtendInfoModel;
import com.yunya.feign.patient_central.domain.model.PatientLabelRecordModel;
import com.yunya.feign.patient_central.domain.model.PicturesCallbackInfoModel;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLabelRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@RequestMapping("/central")
public class PatientBaseInfoController {

  /** 注入对象 */
  private final PatientBaseInfoBiz patientBaseInfoBiz;

  public PatientBaseInfoController(PatientBaseInfoBiz patientBaseInfoBiz) {
    this.patientBaseInfoBiz = patientBaseInfoBiz;
  }

  /**
   * 添加患者基本信息信息
   *
   * @param patientBaseInfoModel 新增患者信息
   * @return ResponseResult
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("添加患者基本信息信息")
  @PostMapping("/add")
  public ResponseResult addPatient(
      @RequestBody @Validated PatientBaseInfoModel patientBaseInfoModel) {
    return ResponseUtil.success(this.patientBaseInfoBiz.addPatient(patientBaseInfoModel));
  }

  /**
   * 根据Id查询患者信息公用信息
   *
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
   * 完善患者基本信息
   *
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
   * 根据姓名和手机号判断是否已存在
   *
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
   * 根据患者id查询患者资料
   *
   * @param id 患者id
   * @return ResponseResult<PatientExtendInfoVo>
   */
  @ApiOperation("根据患者id查询患者资料")
  @GetMapping("/patientDataInfoFind/{id}")
  public ResponseResult<PatientExtendInfoVo> findPatientData(@PathVariable("id") Integer id) {
    return this.patientBaseInfoBiz.findPatientData(id);
  }

  @ApiOperation("根据患者id查询患者资料")
  @RequestMapping (value = "/total/patientInfo/{id}", method = RequestMethod.GET)
  public PatientTotalInfoVo findPatientTotalInfo(@PathVariable(value = "id") Integer id){
    return patientBaseInfoBiz.findPatientTotalInfo(id);
  }

  /**
   * 据姓名/病例编号/手机号/姓名拼音模糊查询患者
   *
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
   * 拍照
   *
   * @param patientId 患者id
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("拍照")
  @GetMapping("/takeAPhoto/{patientId}")
  public ResponseResult takeAPhoto(@PathVariable("patientId") Integer patientId) {
    this.patientBaseInfoBiz.takeAPhoto(patientId);
    return ResponseUtil.success();
  }

  /**
   * 获取照片
   *
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
   *
   * @param faceId 硬件照片id
   * @return ResponseResult
   */
  @ApiOperation("删除照片")
  @DeleteMapping("/deleteThePhoto/{faceId}")
  public ResponseResult deleteThePhoto(@PathVariable(value = "faceId") String faceId) {
    return ResponseUtil.success(this.patientBaseInfoBiz.deleteThePhoto(faceId));
  }

  /**
   * 根据患者id查询来访信息
   *
   * @param id 患者id
   * @return ResponseResult<PatientVisitInfoVo>
   */
  @ApiOperation("根据患者id查询来访信息")
  @GetMapping("/visitInfo/{id}")
  public ResponseResult<PatientVisitInfoVo> findPatientVisitInfo(@PathVariable("id") Integer id) {
    return ResponseUtil.success(this.patientBaseInfoBiz.findPatientVisitInfo(id));
  }

  @CurrentUser
  @ApiOperation(value = "操作标签记录")
  @PostMapping(value = "/operatingLabel")
  public ResponseResult operatingLabel(
      @RequestBody List<PatientLabelRecordModel> patientLabelRecordModelList) {
    patientBaseInfoBiz.operatingLabel(patientLabelRecordModelList);
    return ResponseUtil.success();
  }

  @CurrentUser
  @ApiOperation(value = "查询标签操作记录")
  @PostMapping(value = "/labelList")
  public ResponseResult<PageInfo<PatientLabelRecordVo>> labelList(
      @RequestBody PatientLabelRecordQueryForm form) {
    PageInfo<PatientLabelRecordVo> patientLabelRecordList = patientBaseInfoBiz.labelList(form);
    return ResponseUtil.success(patientLabelRecordList);
  }

  /**
   * 拍照回调
   *
   * @param picturesCallbackInfoModel 拍照回调Model
   * @return ResponseResult
   */
  @IgnoreUserToken
  @ApiOperation(value = "拍照回调")
  @RequestMapping(
      value = "/takePictures",
      method = {RequestMethod.POST})
  public ResponseResult takePictures(
      @RequestBody PicturesCallbackInfoModel picturesCallbackInfoModel) {
    if (patientBaseInfoBiz.takePictures(picturesCallbackInfoModel)) {
      return ResponseUtil.success();
    }
    return ResponseUtil.fail(OperationCodeConstants.DATA_ERROR, "拍照回调错误！", "");
  }

  @IgnoreUserToken
  @ApiOperation(value = "拍照回调")
  @RequestMapping(
      value = "/permit/paizhaohuidiao",
      method = {RequestMethod.POST})
  public ResponseResult paizhaohuidiao(
      @RequestBody PicturesCallbackInfoModel picturesCallbackInfoModel) {
    System.out.println(picturesCallbackInfoModel.toString());
    return ResponseUtil.success();
  }
}
