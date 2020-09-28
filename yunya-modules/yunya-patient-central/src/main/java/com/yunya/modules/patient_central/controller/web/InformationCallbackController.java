package com.yunya.modules.patient_central.controller.web;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.InformationCallbackBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 简介:
 *
 * @author: YK
 * @date: 2020/9/22 09:52
 * @description: 回调中心控制层
 * @since: 1.0.0
 */
@Api(value = "回调中心", description = "回调中心控制层")
@RestController
@RequestMapping("/callback")
public class InformationCallbackController {

  /** 回调中心业务层 */
  @Autowired private InformationCallbackBiz informationCallbackBiz;


  /**
   * 心跳回调
   *
   * @param callbackModel
   * @return Boolean
   */
  @ApiOperation(value = "心跳回调")
  @IgnoreUserToken
  @RequestMapping(value = "/heartbeatCallback", method = {RequestMethod.POST})
  public Map<String, Object> getCallback(@RequestBody CallbackModel callbackModel) {
    System.out.println("******************************心跳回调**************************************");
    System.out.println(callbackModel.getDeviceKey());
    return informationCallbackBiz.heartbeatCallback(callbackModel);

  }

  /**
   * 心跳回调
   *
   * @param callbackModel
   * @return Boolean
   */
  @ApiOperation(value = "心跳回调")
  @IgnoreUserToken
  @RequestMapping(value = "/heartbeatCallbackPaizhao", method = {RequestMethod.POST})
  public Map<String, Object> heartbeatCallbackPaizhao(@RequestBody CallbackModel callbackModel) {
    System.out.println("******************************回调拍照**************************************");
    return informationCallbackBiz.heartbeatCallback(callbackModel);

  }

  @ApiOperation(value = "获取任务")
  @IgnoreUserToken
  @RequestMapping(value = "/getTask", method = {RequestMethod.POST})
  public JSONObject getTask(@RequestBody TaskModel taskModel) {
    return informationCallbackBiz.getTask(taskModel);

  }


  @ApiOperation(value = "结果回调")
  @IgnoreUserToken
  @RequestMapping(value = "/taskProcessingResultsAddress", method = {RequestMethod.POST})
  public Map<String, Object> taskProcessingResultsAddress(@RequestBody TaskProcessingModel taskProcessingModel) {
    return informationCallbackBiz.taskProcessingResultsAddress(taskProcessingModel);
  }

  /**
   * 人脸识别认证返回
   * @param patientWoPlatformInfoModel 回调model
   * @return Map<String, Object>
   */
  @IgnoreUserToken
  @ApiOperation(value = "人脸识别认证返回")
    @RequestMapping(value = "/faceRecognition", method = {RequestMethod.POST})
  public Map<String, Object> faceRecognition(@RequestBody PatientWoPlatformInfoModel patientWoPlatformInfoModel) {
    return this.informationCallbackBiz.renlianshibie(patientWoPlatformInfoModel);
  }

  /**
   * 拍照回调
   * @param picturesCallbackInfoModel 拍照回调Model
   * @return ResponseResult
   */
  @IgnoreUserToken
  @ApiOperation(value = "拍照回调")
  @RequestMapping(value = "/takePictures", method = {RequestMethod.POST})
  public ResponseResult takePictures(@RequestBody PicturesCallbackInfoModel picturesCallbackInfoModel) {
    if( informationCallbackBiz.takePictures(picturesCallbackInfoModel) ){
      return ResponseUtil.success();
    }
    return ResponseUtil.fail(OperationCodeConstants.DATA_ERROR, "拍照回调错误！", "");
  }


  @IgnoreUserToken
  @ApiOperation(value = "拍照回调")
  @RequestMapping(value = "/paizhao")
  public ResponseResult takePictures() {
    System.out.println("---------------------拍照回调------------------");
    System.out.println("---------------------拍照回调------------------");
    System.out.println("---------------------拍照回调------------------");
    System.out.println("---------------------拍照回调------------------");
      return ResponseUtil.success();
  }


}
