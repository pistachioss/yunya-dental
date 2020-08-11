package com.yunya.modules.patient_central.biz;

import com.uniubi.sdk.api.DeviceControllerApi;
import com.uniubi.sdk.api.FaceControllerApi;
import com.uniubi.sdk.api.PersonControllerApi;
import com.uniubi.sdk.client.CustomTokenClient;
import com.uniubi.sdk.model.*;
import com.yunya.feign.patient_central.domain.form.PictureForm;
import com.yunya.feign.patient_central.domain.model.PictureModel;
import com.yunya.feign.patient_central.domain.vo.PictureVo;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.modules.patient_central.constant.WoPlatformConstants;
import com.yunya.modules.patient_central.tokenApi.TokenTask;
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

    @Autowired
    private TokenTask tokenTask;

    //人员接口控制层
    private PersonControllerApi PersonClientApi;
    //设备接口控制层
    private DeviceControllerApi DeviceApi;
    //照片接口控制层
    private FaceControllerApi FaceApi;

    /**
     * Wo平台添加人员信息
     * @param name
     */
    public String addWoPersonInput(String name) {
        PersonInput personInput = new PersonInput();
        personInput.setName(name);
        PersonClientApi = new CustomTokenClient(tokenTask).PersonClient();
        ResultPersonCreateOutput response = PersonClientApi.createUsingPOST(personInput, WoPlatformConstants.APPID);
        PersonCreateOutput data = response.getData();
        return data.getGuid();
    }

    /**
     * 拍照上传WO平台并获取照片URL
     * @param patientBaseInfo
     * @return String
     */
    public void takeAPhoto(PatientBaseInfo patientBaseInfo) {
        DeviceRegisterModeInput input = new DeviceRegisterModeInput();
        input.setPersonGuid(patientBaseInfo.getwoGuid());
        input.setType(WoPlatformConstants.TYPE);
        DeviceApi = new CustomTokenClient(tokenTask).DeviceClient();
        DeviceApi.createRegisterModeUsingPOST(input, WoPlatformConstants.APPID, WoPlatformConstants.DEVICEKEY); //连接硬件设备进行人员拍照注册
    }

    /**
     * 删除照片并查询
     * @param pictureForm
     * @return List<PictureVo>
     */
    public List<PictureVo> DeleteThePhoto(PictureForm pictureForm) {
        FaceApi = new CustomTokenClient(tokenTask).FaceClient();
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
        }
        return pictureVoList;

    }

    /**
     * 设备人员认证授权
     * @param pictureModel
     */
    public void equipmenAuthorization(PictureModel pictureModel) {
        DeviceApi.bindPersonPersonsetUsingPUT(WoPlatformConstants.APPID, WoPlatformConstants.DEVICEKEY, pictureModel.getPersonGuid(), String.valueOf(WoPlatformConstants.TYPE));
    }


    /**
     * 查询人员照片
     * @param personGuid
     * @return
     */
    public List<PictureVo> findWoPersonnelFaceUrl(String personGuid) {
        PersonClientApi = new CustomTokenClient(tokenTask).PersonClient();
        ResultPersonOutput PersonResponse = PersonClientApi.getUsingGET1(WoPlatformConstants.APPID, personGuid); //获取Wo平台照片路径
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
        }
        return pictureVoList;
    }
}
