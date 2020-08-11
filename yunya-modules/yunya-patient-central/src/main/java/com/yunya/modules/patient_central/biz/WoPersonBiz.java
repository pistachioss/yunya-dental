package com.yunya.modules.patient_central.biz;

import com.uniubi.sdk.api.DeviceControllerApi;
import com.uniubi.sdk.api.PersonControllerApi;
import com.uniubi.sdk.client.CustomTokenClient;
import com.uniubi.sdk.model.*;
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

    @Autowired private TokenTask tokenTask;

    //人员接口控制层
    private PersonControllerApi PersonClientApi;
    //设备接口控制层
    private DeviceControllerApi DeviceApi;

    /**
     * Wo平台添加人员信息
     * @param name
     */
    public String addWoPersonInput(String name){
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
    public List<PictureVo> takeAPhoto(PatientBaseInfo patientBaseInfo) {
        DeviceRegisterModeInput input = new DeviceRegisterModeInput();
        input.setPersonGuid(patientBaseInfo.getwoGuid());
        byte TYPE =1;
        input.setType(TYPE);
        DeviceApi = new CustomTokenClient(tokenTask).DeviceClient();
        ResultDeviceRegisterModeOutput response = DeviceApi.createRegisterModeUsingPOST(input, WoPlatformConstants.APPID, WoPlatformConstants.DEVICEKEY);
        System.out.println(response.getData());
        System.out.println(response.getData());
        System.out.println(response.getData());
        PersonClientApi = new CustomTokenClient(tokenTask).PersonClient();
        ResultPersonOutput PersonResponse = PersonClientApi.getUsingGET1(WoPlatformConstants.APPID, patientBaseInfo.getwoGuid());
        List<FaceOutput> faces = PersonResponse.getData().getFaces();
        List<PictureVo> pictureVoList = new ArrayList<>();
        if(faces.size()>0){
            for (FaceOutput face : faces) {
                PictureVo pictureVo = new PictureVo();
                pictureVo.setFaceUrl(face.getFaceUrl());
                pictureVo.setCreateTime(face.getCreateTime());
                pictureVoList.add(pictureVo);
            }
            return pictureVoList;
        }
        return pictureVoList;
    }
}
