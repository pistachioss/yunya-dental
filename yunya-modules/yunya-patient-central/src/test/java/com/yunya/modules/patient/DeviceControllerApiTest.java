package com.yunya.modules.patient;

import com.uniubi.sdk.api.DeviceControllerApi;
import com.uniubi.sdk.auth.authToken.AppAuthParam;
import com.uniubi.sdk.auth.authToken.TokenFetcher;
import com.uniubi.sdk.client.UniUbiClient;
import com.uniubi.sdk.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/7 16:49
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest(classes = DeviceControllerApiTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DeviceControllerApiTest {

    private DeviceControllerApi api;

    //应用Id
    private static String appId = "D40708B670E54D2DA06B1A3974A66EA4";
    //设备序列号
    private static String deviceKey = "84E0F4246B261501";
    //公钥 秘钥
    private static String appSecret = "B496892726AC4D0BBCBC0A6575EC9365";
    private static String appKey = "2CA42A1905B44CD18D8EE83049903306";

    @Before
    public void setup() {
        AppAuthParam appAuthParam = new AppAuthParam(appKey, appSecret, appId);
        TokenFetcher.init(appAuthParam);
        api = new UniUbiClient().DeviceClient();
    }

    /**
     * 设备查询接口
     * <p>
     * 设备查询，deviceKey，appId必填
     */
    @Test
    public void getDeviceUsingGETTest() {
        ResultDeviceOutput response = api.getDeviceUsingGET(appId, deviceKey);
        System.out.println(response);
    }

    /**
     * 授权查询接口
     * <p>
     */
    @Test
    public void findPersonsAuthorizationUsingGET() {
        Map<String, Object> queryParams = new HashMap<>();
        ResultPageResultBeanAuthOutput reponse = api.findPersonsAuthorizationUsingGET(appId, queryParams);
        System.out.println(reponse);
    }


    /**
     * 批量授权
     * <p>
     */
    @Test
    public void batchBindUsingPUT() {
        DeviceBatchBindInput deviceBatchBindInput = new DeviceBatchBindInput();
        deviceBatchBindInput.setDeviceKey("");
        deviceBatchBindInput.setPersonGuids("");
        deviceBatchBindInput.setType((byte) 1);
        deviceBatchBindInput.setPassTime("");


        Result result = api.batchBindUsingPUT(deviceBatchBindInput,appId);
        System.out.println(result);
    }


    /**
     * 设备授权人员
     * <p>
     * 设备授权人员，deviceKey，appId, personGuid, type(1:前端比对 2:云端比对)必填
     */
    @Test
    public void bindPersonPersonsetUsingPUTTest() {
        String personGuid = "9E666DE7AE0E4F9196E522AC05827BAB";
        String type = "1";
        Result response = api.bindPersonPersonsetUsingPUT(appId, deviceKey, personGuid, type);

        System.out.println(response);
    }


    /**
     * 设备添加接口
     * <p>
     * 设备添加，deviceKey，appId，recType必填，name，tag选填
     */
    @Test
    public void createDeviceUsingPOSTTest() {
        String deviceKey = "";
        Byte recType = null;

        DeviceCreateInput deviceInput = new DeviceCreateInput();
        deviceInput.setName("");
        deviceInput.setTag("");

        ResultDeviceOutput response = api.createDeviceUsingPOST(deviceInput, appId, deviceKey, recType);

        System.out.println(response);
        // TODO: test validations
    }


    /**
     * 开启设备注册模式任务接口
     * <p>
     * appId,deviceKey,personGuid,type必填
     */
    @Test
    public void createRegisterModeUsingPOSTTest() {

        DeviceRegisterModeInput input = new DeviceRegisterModeInput();
        input.setPersonGuid("9E666DE7AE0E4F9196E522AC05827BAB");
        byte TYPE =1;
        input.setType(TYPE);

        ResultDeviceRegisterModeOutput response = api.createRegisterModeUsingPOST(input, appId, deviceKey);
        System.out.println(response.getData());
    }


    /**
     * 设备删除接口
     * <p>
     * 设备删除，deviceKey，appId必填
     */
    @Test
    public void deleteDeviceUsingDELETETest() {
        String deviceKey = "";

        Result response = api.deleteDeviceUsingDELETE(appId, deviceKey);
        System.out.println(response);
    }


    /**
     * 结束设备注册模式任务
     * <p>
     * appId,deviceKey,taskGuid必填
     */
    @Test
    public void deleteRegisterModeUsingDELETETest() {
        String deviceKey = "";
        String taskGuid = "";

        Result response = api.deleteRegisterModeUsingDELETE(appId, deviceKey, taskGuid);
        System.out.println(response);
    }


    /**
     * 设备禁用接口
     * <p>
     * 设备禁用，deviceKey，appId必填
     */
    @Test
    public void disableDeviceUsingPUTTest() {
        String deviceKey = "";

        Result response = api.disableDeviceUsingPUT(appId, deviceKey);
        System.out.println(response);
    }


    /**
     * 设备启用接口
     * <p>
     * 设备启用，deviceKey，appId必填
     */
    @Test
    public void enableDeviceUsingPUTTest() {
        String deviceKey = "";

        Result response = api.enableDeviceUsingPUT(appId, deviceKey);
        System.out.println(response);
    }



    /**
     * 查询固件包列表接口
     * <p>
     * 固件包列表，appId必填,type,hardwareVersion,platform,index,length选填
     */
    @Test
    public void findDevicePackageUsingGETTest() {
        Integer index = 1;
        Integer length = 10;
        Byte type = null;
        Byte hardwareVersion = null;
        Byte platform = null;

        ResultPageResultBeanDevicePackageOutput response = api.findDevicePackageUsingGET(index, length,
                type, hardwareVersion, platform);
        System.out.println(response);
    }

    /**
     * 查询固件包列表接口
     * <p>
     * 固件包列表，appId必填,type,hardwareVersion,platform,index,length选填
     * <p>
     * This tests the overload of the method that uses a Map for query parameters instead of
     * listing them out individually.
     */
    @Test
    public void findDevicePackageUsingGETTestQueryMap() {
        DeviceControllerApi.FindDevicePackageUsingGETQueryParams queryParams = new DeviceControllerApi.FindDevicePackageUsingGETQueryParams()
                .index(1)
                .length(10)
                .type(null)
                .hardwareVersion(null)
                .platform(null);
        ResultPageResultBeanDevicePackageOutput response = api.findDevicePackageUsingGET(queryParams);
        System.out.println(response);
    }


    /**
     * 设备配置查询接口
     * <p>
     * 设备配置查询，deviceKey，appId必填
     */
    @Test
    public void getDeviceSettingUsingGETTest() {
        String deviceKey = "";

        ResultDeviceSettingOutput response = api.getDeviceSettingUsingGET(appId, deviceKey);
        System.out.println(response);
    }




    /**
     * 设备状态查询接口
     * <p>
     * 设备查询，deviceKey，appId必填
     */
    @Test
    public void getDeviceOnlineStateUsingGETTest() {
        String deviceKey = "";

        ResultPageResultBeanDeviceOnlineStateOutput response = api.searchDeviceOnlineStateUsingGET(appId, deviceKey);
        System.out.println(response);
    }


    /**
     * 查询设备注册模式任务
     * <p>
     * appId,deviceKey,taskGuid必填
     */
    @Test
    public void getRegisterModeUsingGETTest() {
        String deviceKey = "";
        String taskGuid = "";

        ResultDeviceRegisterModeOutput response = api.getRegisterModeUsingGET(appId, deviceKey, taskGuid);
        System.out.println(response);
    }


    /**
     * 设备重置接口
     * <p>
     * 设备重置，deviceKey，appId必填
     */
    @Test
    public void resetDeviceUsingPUTTest() {
        String deviceKey = "";

        Result response = api.resetDeviceUsingPUT(appId, deviceKey);
        System.out.println(response);
    }


    /**
     * 设备清空人员信息
     * <p>
     * 设备清空人员信息，deviceKey，appId, type(1:前端比对 2:云端比对)必填
     */
    @Test
    public void resetPersonsetUsingDELETETest() {
        String deviceKey = "";
        String type = "";

        Result response = api.resetPersonsetUsingDELETE(appId, deviceKey, type);
        System.out.println(response);
    }


    /**
     * 设备重启接口
     * <p>
     * 设备重启，deviceKey，appId必填
     */
    @Test
    public void restartDeviceUsingPUTTest() {
        String deviceKey = "";

        Result response = api.restartDeviceUsingPUT(appId, deviceKey);
        System.out.println(response);
    }


    /**
     * 设备列表接口
     * <p>
     * 设备列表，appId必填,name,tag,type,deviceKey,state,startTime,endTime,index,length选填, name与deviceKey支持模糊查询
     */
    @Test
    public void searchDeviceUsingGETTest() {
        Integer index = 1;
        Integer length = 10;
        String deviceKey = "";
        String name = "";
        String tag = "";
        Byte type = null;
        String versionNo = "";
        Byte recType = null;
        Byte state = null;
        Date startTime = new Date("");
        Date endTime = new Date("");

        ResultPageResultBeanDeviceOutput response = api.searchDeviceUsingGET(appId, index, length,
                deviceKey, name, tag, type, versionNo, recType, state, startTime, endTime);
        System.out.println(response);
    }

    /**
     * 设备列表接口
     * <p>
     * 设备列表，appId必填,name,tag,type,deviceKey,state,startTime,endTime,index,length选填, name与deviceKey支持模糊查询
     * <p>
     * This tests the overload of the method that uses a Map for query parameters instead of
     * listing them out individually.
     */
    @Test
    public void searchDeviceUsingGETTestQueryMap() {
        DeviceControllerApi.SearchDeviceUsingGETQueryParams queryParams = new DeviceControllerApi.SearchDeviceUsingGETQueryParams()
                .index(1)
                .length(10)
                .deviceKey("")
                .name("")
                .tag("")
                .type(null)
                .versionNo("")
                .recType(null)
                .state(null)
                .startTime(null)
                .endTime(null);

        ResultPageResultBeanDeviceOutput response = api.searchDeviceUsingGET(appId, queryParams);
        System.out.println(response);
    }

    /**
     * 设备销权人员
     * <p>
     * 设备销权人员，deviceKey，appId, personGuid, type(1:前端比对 2:云端比对)必填
     */
    @Test
    public void unbindPersonPersonsetUsingDELETETest() {
        String deviceKey = "";
        String personGuid = "";
        String type = "";

        Result response = api.unbindPersonPersonsetUsingDELETE(appId, deviceKey, personGuid, type);
        System.out.println(response);
    }


    /**
     * 设备配置修改接口
     * <p>
     * 设备配置修改，deviceKey，appId必填
     */
    @Test
    public void updateDeviceSettingUsingPUTTest() {
        String deviceKey = "";

        DeviceSettingInput deviceSettingInput = new DeviceSettingInput();

        Result response = api.updateDeviceSettingUsingPUT(deviceSettingInput, appId, deviceKey);
        System.out.println(response);
    }


    /**
     * 设备更新接口
     * <p>
     * 设备更新，deviceKey，appId必填， name，recType,tag选填
     */
    @Test
    public void updateDeviceUsingPUTTest() {
        String deviceKey = "";

        DeviceUpdateInput deviceUpdate = new DeviceUpdateInput();

        Result response = api.updateDeviceUsingPUT(deviceUpdate, appId, deviceKey);
        System.out.println(response);
    }


    /**
     * 设备升级接口
     * <p>
     * appId,deviceKey,packageId必填
     */
    @Test
    public void upgradeDeviceUsingPUTTest() {
        UpgradeDeviceInput upgradeDeviceInput = new UpgradeDeviceInput();

        upgradeDeviceInput.setDeviceKeys("");
        upgradeDeviceInput.setPackageId("");

        Result response = api.upgradeDeviceUsingPUT(upgradeDeviceInput, appId);
        System.out.println(response);
    }


}


