package com.yunya.modules.patient_central;

import com.uniubi.sdk.api.PersonControllerApi;
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

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/7 16:43
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest(classes = PersonControllerApiTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class PersonControllerApiTest {

    private PersonControllerApi api;


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
        api = new UniUbiClient().PersonClient();
    }


    /**
     * 人员删除接口
     *
     * 人员删除，appId,personGuid必填
     */
    @Test
    public void batchDeleteUsingDELETETest() {
        String personGuids = "";

        Result response = api.batchDeleteUsingDELETE(appId, personGuids);
        System.out.println(response);
    }

    /**
     * 人员删除接口
     *
     * 人员删除，appId,personGuid必填
     *
     * This tests the overload of the method that uses a Map for query parameters instead of
     * listing them out individually.
     */
    @Test
    public void batchDeleteUsingDELETETestQueryMap() {
        PersonControllerApi.BatchDeleteUsingDELETEQueryParams queryParams = new PersonControllerApi.BatchDeleteUsingDELETEQueryParams()
                .personGuids("");

        Result response = api.batchDeleteUsingDELETE(appId, queryParams);
        System.out.println(response);
    }

    /**
     * 人员创建接口
     *
     * 人员创建，appId,name必填,cardNo,phone,tag选填
     */
    @Test
    public void createUsingPOSTTest() {
        PersonInput personInput = new PersonInput();
        personInput.setName("小wei");

        ResultPersonCreateOutput response = api.createUsingPOST(personInput, appId);
        System.out.println(response);
    }


    /**
     * 人员搜索接口
     *
     * 人员搜索，appId必填,personGuid,name,cardNo,phone,tag,startTime,endTime,index,length选填,name,cardNo,phone支持模糊搜索
     */
    @Test
    public void findUsingGETTest() {
        Integer index = 1;
        Integer length = 10;
        String personGuid = "";
        String name = "";
        String cardNo = "";
        String phone = "";
        String tag = "";
        Date startTime = null;
        Date endTime = null;

        ResultPageResultBeanPersonOutput response = api.findUsingGET(appId, index, length, personGuid, name, cardNo,
                phone, tag, startTime, endTime);
        System.out.println(response);
    }

    /**
     * 人员搜索接口
     *
     * 人员搜索，appId必填,personGuid,name,cardNo,phone,tag,startTime,endTime,index,length选填,name,cardNo,phone支持模糊搜索
     *
     * This tests the overload of the method that uses a Map for query parameters instead of
     * listing them out individually.
     */
    @Test
    public void findUsingGETTestQueryMap() {
        PersonControllerApi.FindUsingGETQueryParams queryParams = new PersonControllerApi.FindUsingGETQueryParams()
                .index(1)
                .length(10)
                .personGuid("")
                .name("")
                .cardNo("")
                .phone("")
                .tag("")
                .startTime(null)
                .endTime(null);

        ResultPageResultBeanPersonOutput response = api.findUsingGET(appId, queryParams);
        System.out.println(response);
    }

    /**
     * 人员查询接口
     *
     * 人员查询，appId,personGuid必填,
     */
    @Test
    public void getUsingGET1Test() {
        String personGuid = "3841F913C7C14B698735BE8DDE03C2BF";

        ResultPersonOutput response = api.getUsingGET1(appId, personGuid);
        System.out.println(response);
    }


    /**
     * 人员编辑接口
     *
     * 人员编辑，appId,personGuid必填,name,cardNo,phone,tag选填
     */
    @Test
    public void updateUsingPUTTest() {
        String personGuid = "";

        PersonUpdateInput personInput = new PersonUpdateInput();
        personInput.setName("");

        Result response = api.updateUsingPUT(personInput, appId, personGuid);
        System.out.println(response);
    }




}
