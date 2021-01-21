package com.yunya.modules.patient_central;

import com.uniubi.sdk.api.FaceControllerApi;
import com.uniubi.sdk.auth.authToken.AppAuthParam;
import com.uniubi.sdk.auth.authToken.TokenFetcher;
import com.uniubi.sdk.client.UniUbiClient;
import com.uniubi.sdk.model.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/11 13:23
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest(classes = FaceControllerApiTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class FaceControllerApiTest {

    private FaceControllerApi api;

    private static String appId = "";

    @Before
    public void setup() {
        String appSecret = "";
        String appKey = "";

        AppAuthParam appAuthParam = new AppAuthParam(appKey, appSecret, appId);
        TokenFetcher.init(appAuthParam);
        api = new UniUbiClient().FaceClient();
    }


    /**
     * 人员照片删除接口
     *
     * 人员照片删除，appId,faceGuid,personGuid必填
     */
    @Test
    public void deleteUsingDELETETest() {
        String faceGuid = "";
        String personGuid = "";

        Result response = api.deleteUsingDELETE(appId, faceGuid, personGuid);
        System.out.println(response);
    }

    /**
     * 人员照片删除接口
     *
     * 人员照片删除，appId,faceGuid,personGuid必填
     *
     * This tests the overload of the method that uses a Map for query parameters instead of listing them out individually.
     * listing them out individually.
     */
    @Test
    public void deleteUsingDELETETestQueryMap() {
        String faceGuid = "";
        FaceControllerApi.DeleteUsingDELETEQueryParams queryParams = new FaceControllerApi.DeleteUsingDELETEQueryParams()
                .personGuid("");

        Result response = api.deleteUsingDELETE(appId, faceGuid, queryParams);
        System.out.println(response);
    }

    /**
     * 人员照片状态查询接口(线下库)
     *
     * 人员照片状态查询(线下库)，appId,faceGuid,personGuid必填,deviceKey选填
     */
    @Test
    public void getFaceStateUsingGETTest() {
        String faceGuid = "";
        String personGuid = "";
        String deviceKey = "";

        ResultListFaceStateOutput response = api.getFaceStateUsingGET(appId, faceGuid, personGuid, deviceKey);
        System.out.println(response);
    }

    /**
     * 人员照片状态查询接口(线下库)
     *
     * 人员照片状态查询(线下库)，appId,faceGuid,personGuid必填,deviceKey选填
     *
     * This tests the overload of the method that uses a Map for query parameters instead of
     * listing them out individually.
     */
    @Test
    public void getFaceStateUsingGETTestQueryMap() {
        String faceGuid = "";
        String personGuid = "";
        FaceControllerApi.GetFaceStateUsingGETQueryParams queryParams = new FaceControllerApi.GetFaceStateUsingGETQueryParams()
                .deviceKey("");

        ResultListFaceStateOutput response = api.getFaceStateUsingGET(appId, faceGuid, personGuid, queryParams);
        System.out.println(response);
    }

    /**
     * 人员照片查询接口
     *
     * 人员照片查询，appId,faceGuid必填
     */
    @Test
    public void getUsingGETTest() {
        String faceGuid = "";

        ResultFaceOutput response = api.getUsingGET(appId, faceGuid);
        System.out.println(response);
    }


    /**
     * 人员照片注册接口
     *
     * 人员照片注册，appId,personGuid必填,base64,url二选一填
     */
    @Test
    public void registryFaceUsingPOSTTest() {
        FaceInput faceInput = new FaceInput();
        faceInput.setPersonGuid("");
        faceInput.setUrl("");
        faceInput.setBase64("");

        ResultFaceOutput response = api.registryFaceUsingPOST(faceInput, appId);
        System.out.println(response);
    }
}
