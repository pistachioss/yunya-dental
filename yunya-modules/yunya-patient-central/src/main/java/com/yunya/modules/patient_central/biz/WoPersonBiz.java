package com.yunya.modules.patient_central.biz;

import com.uniubi.sdk.api.PersonControllerApi;
import com.uniubi.sdk.client.CustomTokenClient;
import com.uniubi.sdk.model.PersonCreateOutput;
import com.uniubi.sdk.model.PersonInput;
import com.uniubi.sdk.model.ResultPersonCreateOutput;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.modules.patient_central.constant.WoPlatformConstants;
import com.yunya.modules.patient_central.tokenApi.TokenTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简单介绍:</br>
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
    private RedisUtils redisUtils;


   /* @Before
    public void setup() {
        // 调用方法前，需初始化SDK的模块，共有4类
        // 此处入参为客户自定义的实现类，如TokenTask
        DeviceControllerApi api = new CustomTokenClient(new TokenTask()).DeviceClient();
        ResultDeviceOutput result = api.getDeviceUsingGET("appId","deviceKey");
        // 应用结束时调用，关闭定时自动获取token的线程池，如webApp关闭时
        TokenFetcher.shutdown();
    }*/

    /**
     * Wo平台添加人员信息
     * @param name
     */
    public String addWoPersonInput(String name){
        PersonInput personInput = new PersonInput();
        personInput.setName(name);
        PersonControllerApi api = new CustomTokenClient(new TokenTask()).PersonClient();
        ResultPersonCreateOutput response = api.createUsingPOST(personInput, WoPlatformConstants.APPID);
        PersonCreateOutput data = response.getData();
        return data.getGuid();
    }

}
