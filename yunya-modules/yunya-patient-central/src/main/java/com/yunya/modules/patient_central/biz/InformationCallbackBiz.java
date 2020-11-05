package com.yunya.modules.patient_central.biz;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientImg;
import com.yunya.models.system.EquipmentInfo;
import com.yunya.modules.patient_central.constant.WoPlatformConstants;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientImgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 简介: 设备信息回调 业务层
 *
 * @author: YK
 * @date: 2020/9/25 14:49
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
@PropertySource("classpath:application.yml")
public class InformationCallbackBiz implements Serializable {

  /** 注入redis */
  @Autowired private RedisUtils redisUtils;

  /** 注入患者Mapper */
  @Autowired private PatientBaseInfoMapper patientBaseInfoMapper;

  /** 系统服务 */
  @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;

  /** 注入患者照片Mapper */
  @Autowired private PatientImgMapper patientImgMapper;

  /** 获取患者服务端口号 */
  @Value("${var.port}")
  private String servePrort;

  /**
   * 获取硬件编码 并把硬件信息存入redis
   *
   * @return
   */
  public String getSN() {
    String SN = "SN";
    if (redisUtils.get(SN) == null) {
      EquipmentInfo equipmentInfo = remoteSystemServiceFeign.equipmentInfoOne();
      if (StringHelper.isNotNull(equipmentInfo)) {
        redisUtils.set("SN", equipmentInfo.getSerialNumber());
        redisUtils.set("PASS", equipmentInfo.getPass());
        redisUtils.set("IP", equipmentInfo.getIp());
      }
    }
    return redisUtils.get("SN");
  }

  /**
   * 心跳回调
   *
   * @param callbackModel 回调model
   * @return Map<String, Object>
   */
  public Map<String, Object> heartbeatCallback(CallbackModel callbackModel) {
    Map<String, Object> map = new HashMap<>(16);
    String task = redisUtils.get(getSN());
    if (task != null) {
      if (getSN().equals(callbackModel.getDeviceKey())) {
        System.out.println("对比成功,有任务要做");
        map.put("result", true);
        return map;
      }
    }
    map.put("result", false);
    return map;
  }

  /**
   * 心跳回调-获取任务
   *
   * @param taskModel 心跳回调Model
   * @return JSONObject
   */
  public JSONObject getTask(TaskModel taskModel) {
    JSONObject taskJsonObject;
    if (getSN().equals(taskModel.getDeviceKey())) {
      System.out.println(
          "******************************获取任务**************************************");
      System.out.println(taskModel.toString());
      System.out.println(taskModel.getDeviceKey());
      String task = redisUtils.get(getSN());
      taskJsonObject = JSONObject.parseObject(task);
      return taskJsonObject;
    }
    taskJsonObject = new JSONObject();
    taskJsonObject.put("result", false);
    return taskJsonObject;
  }

  /**
   * 心跳回调-任务结果
   *
   * @param taskProcessingModel 任务结果Model
   * @return Map<String, Object>
   */
  public Map<String, Object> taskProcessingResultsAddress(TaskProcessingModel taskProcessingModel) {
    System.out.println(
        "*********************************是否完成，完成结果********************************");
    System.out.println(taskProcessingModel.toString());
    System.out.println(taskProcessingModel.getDeviceKey());
    Map<String, Object> map = new HashMap<>(16);
    if (getSN().equals(taskProcessingModel.getDeviceKey())) {
      System.out.println("结果回调");
      JSONObject obj = JSONObject.parseObject(taskProcessingModel.getResult());
      Integer result = (Integer) obj.get("result");
      Boolean success = (Boolean) obj.get("success");
      String msg = (String) obj.get("msg");
      String message = "设置成功";
      if (message.equals(msg)) {
        System.out.println("设置成功");
        redisUtils.delete(getSN());
        map.put("result", false);
        return map;
      }
      // 查看任务是否成功
      if (result == 1 && success == true) {
        // 成功后获取返回的数据
        JSONObject data = (JSONObject) obj.get("data");
        // 获取成功返回的 taskNo
        String taskNo = taskProcessingModel.getTaskNo();

        // 添加人员
        // 对比任务名称 如果为true 表明是新增人员接口
        try {
          if (WoPlatformConstants.CREATOR_TASK_NO.equals(taskNo)) {
            System.out.println(
                "******************************添加人员**************************************");
            String createPatientId = redisUtils.get("patientId");
            PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
            patientBaseInfo.setId(Integer.parseInt(createPatientId));
            // 获取设备创建人员后的id，保存到数据库中
            String id = (String) data.get("id");
            patientBaseInfo.setPersonId(id);
            patientBaseInfoMapper.updateByPrimaryKeySelective(patientBaseInfo);
          }

          // 拍照注册
          if (WoPlatformConstants.TAKE_IMG_TASK_NO.equals(taskNo)) {
            System.out.println(
                "******************************人员拍照**************************************");
            System.out.println(taskProcessingModel.toString());
            System.out.println(taskProcessingModel.getDeviceKey());
          }

          // 删除照片
          if (WoPlatformConstants.FACE_DELETE_NO.equals(taskNo)) {
            System.out.println(
                "******************************删除照片**************************************");
            System.out.println(taskProcessingModel.toString());
            System.out.println(taskProcessingModel.getDeviceKey());
          }

        } catch (NullPointerException nullPointerException) {
          System.out.println("结果回调失败,此处有异常" + nullPointerException);
        } finally {
          redisUtils.delete("patientId");
          redisUtils.delete(getSN());
          System.out.println(
              "******************************任务完成**************************************");
          map.put("result", false);
        }
        return map;
      }
      redisUtils.delete(getSN());
      map.put("result", false);
      return map;
    }
    redisUtils.delete(getSN());
    map.put("result", false);
    return map;
  }

  /**
   * 授权人脸识别结果
   *
   * @param patientWoPlatformInfoModel 测试
   */
  public Map<String, Object> renlianshibie(PatientWoPlatformInfoModel patientWoPlatformInfoModel) {

    // 未认证
    String wrongIdentity = "STRANGERBABY";
    // 陌生人
    String stranger = "IDCARD";

    Map<String, Object> map = new HashMap<>();
    if (StringHelper.isNotNull(patientWoPlatformInfoModel)
        && StringHelper.isNotNull(patientWoPlatformInfoModel.getPersonId())) {
      if (!wrongIdentity.equals(patientWoPlatformInfoModel.getPersonId())
          && !stranger.equals(patientWoPlatformInfoModel.getPersonId())) {
        PatientBaseInfoVo patientBaseInfoVo =
            patientBaseInfoMapper.selectOneByPersonId(patientWoPlatformInfoModel.getPersonId());
        if (patientBaseInfoVo != null) {
          System.out.println("********************认证成功*********************************");
          System.out.println(patientWoPlatformInfoModel.toString());
          map.put("result", 1);
          map.put("success", true);
          return map;
        }
      }
    }
    System.out.println("未认证,请联系管理员");
    map.put("result", 1);
    map.put("success", true);
    return map;
  }

  /**
   * 拍照回调
   *
   * @param picturesCallbackInfoModel 拍照回调接收结果Model
   */
  public Boolean takePictures(PicturesCallbackInfoModel picturesCallbackInfoModel) {
    // 如果未获取到设备号 任务就不创建
    System.out.println(
        "******************************拍照回调成功**************************************");
    if (picturesCallbackInfoModel != null && picturesCallbackInfoModel.getBase64() != null) {
      if (getSN().equals(picturesCallbackInfoModel.getDeviceKey())) {
        // 创建患者照片对象
        PatientImg patientImg = new PatientImg();
        // 创建患者信息对象
        PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
        patientBaseInfo.setFaceUrl(picturesCallbackInfoModel.getBase64());
        // 查询患者照片信息
        PatientImg patientImgModel =
            patientImgMapper.selectByPatient(
                Integer.parseInt(redisUtils.get("takePhotosPatientId")));

        // 如果照片不为空就判断那个照片位置是空的 让后插入照片
        if (patientImgModel != null) {
          // 添加一张照片
          if (patientImgModel.getImgOne() == null
              || StringHelper.isEmpty(patientImgModel.getImgOne())) {
            // 获取照片base64码
            patientImgModel.setImgOne(picturesCallbackInfoModel.getBase64());
            // 获取设备照片id
            patientImgModel.setFaceIdOne(picturesCallbackInfoModel.getFaceId());
            addPatientImg(patientImgModel);
            // 患者信息对象 头像更新
            updatePatientInfoImg(patientBaseInfo);
            return true;
          }
          // 添加二张照片
          if (patientImgModel.getImgTwo() == null
              || StringHelper.isEmpty(patientImgModel.getImgTwo())) {
            // 获取照片base64码
            patientImgModel.setImgTwo(picturesCallbackInfoModel.getBase64());
            // 获取设备照片id
            patientImgModel.setFaceIdTwo(picturesCallbackInfoModel.getFaceId());
            addPatientImg(patientImgModel);
            return true;
          }
          // 添加三张照片
          if (patientImgModel.getImgThree() == null
              || StringHelper.isEmpty(patientImgModel.getImgThree())) {
            // 获取照片base64码
            patientImgModel.setImgThree(picturesCallbackInfoModel.getBase64());
            // 获取设备照片id
            patientImgModel.setFaceIdThree(picturesCallbackInfoModel.getFaceId());
            addPatientImg(patientImgModel);
            return true;
          }

          // 如果没有照片对象就创建 并把第一张照片作为头像
        } else {
          patientImg.setPatientId(Integer.parseInt(redisUtils.get("takePhotosPatientId")));
          patientImg.setImgOne(picturesCallbackInfoModel.getBase64());
          patientImg.setFaceIdOne(picturesCallbackInfoModel.getFaceId());
          patientImg.setCrtId(Integer.parseInt(redisUtils.get("userId")));
          patientImg.setCrtName(redisUtils.get("userName"));
          patientImgMapper.insert(patientImg);
          // 患者信息头像同时更新
          updatePatientInfoImg(patientBaseInfo);
          return true;
        }
      }
      return false;
    }
    redisUtils.delete("userId");
    redisUtils.delete("userName");
    return false;
  }

  /**
   * 插入照片
   *
   * @param patientImgModel 患者照片对象
   */
  public void addPatientImg(PatientImg patientImgModel) {
    patientImgModel.setUpdId(Integer.parseInt(redisUtils.get("userId")));
    patientImgModel.setUpdName(redisUtils.get("userName"));
    patientImgModel.setUpdTime(new Date());
    patientImgMapper.updateByPrimaryKeySelective(patientImgModel);
  }

  /**
   * 更新患者信息头像
   *
   * @param patientBaseInfo 患者信息
   */
  public void updatePatientInfoImg(PatientBaseInfo patientBaseInfo) {
    patientBaseInfo.setId(Integer.parseInt(redisUtils.get("takePhotosPatientId")));
    patientBaseInfo.setUptId(Integer.parseInt(redisUtils.get("userId")));
    patientBaseInfo.setUpdName(redisUtils.get("userName"));
    patientBaseInfo.setUpdTime(new Date());
    patientBaseInfoMapper.updateByPrimaryKeySelective(patientBaseInfo);
    redisUtils.delete("takePhotosPatientId");
    redisUtils.delete("userId");
    redisUtils.delete("userName");
  }

  /**
   * 获取患者服务端口号
   *
   * @return String
   */
  public String portNumberGet() {
    return servePrort;
  }
}
