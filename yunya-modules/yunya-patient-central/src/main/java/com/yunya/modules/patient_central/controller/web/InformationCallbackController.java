package com.yunya.modules.patient_central.controller.web;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.patient_central.domain.model.CallbackModel;
import com.yunya.feign.patient_central.domain.model.TaskModel;
import com.yunya.feign.patient_central.domain.model.TaskProcessingModel;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.modules.patient_central.constant.WoPlatformConstants;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
@RequestMapping("callback")
public class InformationCallbackController {

  /** 注入redis */
  @Autowired private RedisUtils redisUtils;

  /** 注入患者Mapper */
  @Autowired private PatientBaseInfoMapper patientBaseInfoMapper;


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
    Map<String, Object> map = new HashMap<>(16);
    String task = redisUtils.get(WoPlatformConstants.SN);
    if (task != null){
      if (WoPlatformConstants.SN.equals(callbackModel.getDeviceKey())) {
        System.out.println("对比成功,有任务要做");
        map.put("result", true);
        return map;
      }
    }
    map.put("result", false);
    return map;
  }

  @ApiOperation(value = "获取任务")
  @IgnoreUserToken
  @RequestMapping(value = "/getTask", method = {RequestMethod.POST})
  public JSONObject getTask(@RequestBody TaskModel taskModel) {
    JSONObject taskJsonObject;
    if (WoPlatformConstants.SN.equals(taskModel.getDeviceKey())) {
      System.out.println("******************************获取任务**************************************");
      System.out.println(taskModel.toString());
      System.out.println(taskModel.getDeviceKey());
      String task = redisUtils.get(WoPlatformConstants.SN);
      taskJsonObject = JSONObject.parseObject(task);
      return taskJsonObject;
    }
    taskJsonObject = new JSONObject();
    taskJsonObject.put("result", false);
    return taskJsonObject;
  }


  @ApiOperation(value = "结果回调")
  @IgnoreUserToken
  @RequestMapping(value = "/taskProcessingResultsAddress", method = {RequestMethod.POST})
  public Map<String, Object> taskProcessingResultsAddress(@RequestBody TaskProcessingModel taskProcessingModel) {
    System.out.println("*********************************是否完成，完成结果********************************");
    System.out.println(taskProcessingModel.toString());
    System.out.println(taskProcessingModel.getDeviceKey());
    Map<String, Object> map = new HashMap<>(16);
    if (WoPlatformConstants.SN.equals(taskProcessingModel.getDeviceKey())) {
      System.out.println("结果回调");
      JSONObject obj = JSONObject.parseObject(taskProcessingModel.getResult());
      Integer result = (Integer) obj.get("result");
      Boolean success = (Boolean) obj.get("success");
      //获取任务名称
      //添加员工
      String taskNoName = redisUtils.get("taskNo");

      //查看任务是否成功
      if (result == 1 && success == true){
        //成功后获取返回的数据
        JSONObject data = (JSONObject) obj.get("data");
        //获取成功返回的 taskNo
        String taskNo = taskProcessingModel.getTaskNo();

        //添加人员
        //对比任务名称 如果为true 表明是新增人员接口
        try{
          if (taskNoName.equals(taskNo)){
            System.out.println("******************************添加人员**************************************");
            String createPatientId = redisUtils.get("createPatientId");
            PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
            patientBaseInfo.setId(Integer.parseInt(createPatientId));
            //获取设备创建人员后的id，保存到数据库中
            String id = (String) data.get("id");
            patientBaseInfo.setPersonId(id);
            patientBaseInfoMapper.updateByPrimaryKeySelective(patientBaseInfo);
          }

          //拍照
          if (taskNoName.equals(taskNo)){
            System.out.println("******************************人员拍照**************************************");

          }
        }catch (NullPointerException nullPointerException){
          System.out.println("结果回调失败,此处有异常"+nullPointerException);
        }finally{
          redisUtils.delete(redisUtils.get("createPatientId"));
          redisUtils.delete(WoPlatformConstants.SN);
          System.out.println("******************************任务完成**************************************");
          map.put("result", false);
          return map;
        }
      }
    }
    map.put("result", false);
    return map;
  }

}
