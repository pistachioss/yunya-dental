package com.yunya.modules.patient_central.biz;

import com.uniubi.sdk.api.DeviceControllerApi;
import com.uniubi.sdk.api.FaceControllerApi;
import com.uniubi.sdk.api.PersonControllerApi;
import com.uniubi.sdk.client.CustomTokenClient;
import com.uniubi.sdk.model.*;
import com.yunya.feign.patient_central.domain.form.PictureForm;
import com.yunya.feign.patient_central.domain.model.PictureModel;
import com.yunya.feign.patient_central.domain.vo.web.PhotoInformationVo;
import com.yunya.feign.patient_central.domain.vo.web.PictureVo;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.modules.patient_central.constant.WoPlatformConstants;
import com.yunya.modules.patient_central.constant.WoPlatformHeartbeat;
import com.yunya.modules.patient_central.tokenApi.TokenTask;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单介绍:</br> Wo平台API
 *
 * @author: WY
 * @date 2020/8/10 13:16
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WoPersonBiz {

  /** 注入redis */
  @Autowired private RedisUtils redisUtils;

  /** 入住Wo平台tokenTask */
  @Autowired private TokenTask tokenTask;

  /** 注入患者信息对象 */
  @Autowired PatientBaseInfoBiz patientBaseInfoBiz;

  /** 人员接口控制层 */
  private PersonControllerApi PersonClientApi;

  /** 设备接口控制层 */
  private DeviceControllerApi DeviceApi;

  /** 照片接口控制层 */
  private FaceControllerApi FaceApi;

  /**
   * Wo平台添加人员信息
   * @param name 人员姓名
   * @return String
   */
  public String addWoPersonInput(String name) {
    PersonInput personInput = new PersonInput();
    personInput.setName(name);
    PersonClientApi = new CustomTokenClient(tokenTask).PersonClient();
    ResultPersonCreateOutput response =
        PersonClientApi.createUsingPOST(personInput, WoPlatformConstants.APPID);
    PersonCreateOutput data = response.getData();
    return data.getGuid();
  }

  /**
   * 拍照上传
   *
   * @param patientBaseInfo 患者信息
   * @return void
   */
  public void takeAPhoto(PatientBaseInfo patientBaseInfo) {
    /*DeviceRegisterModeInput input = new DeviceRegisterModeInput();
    input.setPersonGuid(patientBaseInfo.getWoGuid());
    input.setType(WoPlatformConstants.TYPE);
    DeviceApi = new CustomTokenClient(tokenTask).DeviceClient();
    DeviceApi.createRegisterModeUsingPOST(input, WoPlatformConstants.APPID, WoPlatformConstants.DEVICEKEY); //连接硬件设备进行人员拍照注册*/
    NameValuePair[] data = {
      new NameValuePair("pass", redisUtils.get("PASS")),
      new NameValuePair("personId", patientBaseInfo.getPersonId())
    };
    // 调用心跳接口进行拍照注册
    WoPlatformHeartbeat.httpPostHeartbeatAccess(
        redisUtils.get("URL") + "/face/takeImg", data);
  }

  /**
   * 删除照片并查询
   *
   * @param pictureForm
   * @return List<PictureVo>
   */
  public List<PhotoInformationVo> deleteThePhoto(PictureForm pictureForm) {
    /* FaceApi = new CustomTokenClient(tokenTask).FaceClient();
    FaceApi.deleteUsingDELETE(WoPlatformConstants.APPID, pictureForm.getFaceGuid(), pictureForm.getPersonGuid()); //删除照片
    ResultPersonOutput PersonResponse = PersonClientApi.getUsingGET1(WoPlatformConstants.APPID, pictureForm.getPersonGuid()); //获取Wo平台照片路径
    List<FaceOutput> faces = PersonResponse.getData().getFaces();
    List<PictureVo> pictureVoList = new ArrayList<>();
    if (faces.size() > 0) {
        for (FaceOutput face : faces) {
            PictureVo pictureVo = new PictureVo();
            pictureVo.setPersonGuid(face.getPersonGuid());
            pictureVo.setFaceGuid(face.getGuid());
            pictureVo.setFaceUrl(face.getFaceUrl());
            pictureVo.setCreateTime(face.getCreateTime());
            pictureVoList.add(pictureVo);
        }
        return pictureVoList;
    }*/

    NameValuePair[] data = {
      new NameValuePair("pass", redisUtils.get("PASS")),
      new NameValuePair("faceId", pictureForm.getFaceId())
    };
    // 调用心跳接口创建人员信息
    WoPlatformHeartbeat.httpPostHeartbeatAccess(
        redisUtils.get("URL") + "/face/delete", data);
    ArrayList<PhotoInformationVo> faceUrl =
        patientBaseInfoBiz.getFaceUrl(pictureForm.getPatientId());
    return faceUrl;
  }

  /**
   * 设备人员认证授权
   *
   * @param pictureModel 设备人员认证授权model
   */
  public void equipmenAuthorization(PictureModel pictureModel) {
    DeviceApi.bindPersonPersonsetUsingPUT(
        WoPlatformConstants.APPID,
        WoPlatformConstants.DEVICEKEY,
        pictureModel.getPersonGuid(),
        String.valueOf(WoPlatformConstants.TYPE));
  }

  /**
   * 查询人员照片
   *
   * @param personGuid 人员id
   * @return List<PictureVo>
   */
  public List<PictureVo> findWoPersonnelFaceUrl(String personGuid) {
    PersonClientApi = new CustomTokenClient(tokenTask).PersonClient();
    // 获取Wo平台照片路径
    ResultPersonOutput personResponse =
        PersonClientApi.getUsingGET1(WoPlatformConstants.APPID, personGuid);
    List<FaceOutput> faces = personResponse.getData().getFaces();
    List<PictureVo> pictureVoList = new ArrayList<>();
    if (faces.size() > 0) {
      for (FaceOutput face : faces) {
        PictureVo pictureVo = new PictureVo();
        pictureVo.setPersonGuid(face.getPersonGuid());
        pictureVo.setFaceGuid(face.getGuid());
        pictureVo.setFaceUrl(face.getFaceUrl());
        pictureVo.setCreateTime(face.getCreateTime());
        pictureVoList.add(pictureVo);
      }
      return pictureVoList;
    }
    return pictureVoList;
  }
}
