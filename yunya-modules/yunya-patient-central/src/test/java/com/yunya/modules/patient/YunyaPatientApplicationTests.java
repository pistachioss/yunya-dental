package com.yunya.modules.patient;

import com.uniubi.sdk.api.DeviceControllerApi;
import com.uniubi.sdk.auth.authToken.AppAuthParam;
import com.uniubi.sdk.auth.authToken.TokenFetcher;
import com.uniubi.sdk.auth.authToken.TokenFetcherNoRunnable;
import com.uniubi.sdk.client.CustomTokenClient;
import com.uniubi.sdk.client.UniUbiClient;
import com.uniubi.sdk.model.ResultDeviceOutput;
import com.uniubi.sdk.model.ResultPageResultBeanAuthOutput;
import com.yunya.modules.patient.tokenApi.TokenTask;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = YunyaPatientApplicationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
class YunyaPatientApplicationTests {

    private DeviceControllerApi api;
    //应用Id
    private static String appId = "D40708B670E54D2DA06B1A3974A66EA4";
    //设备序列号
    private static String deviceKey = "84E0F4246B261501";
    //公钥 秘钥
    private static String appSecret = "B496892726AC4D0BBCBC0A6575EC9365";
    private static String appKey = "2CA42A1905B44CD18D8EE83049903306";

    @Test
    void contextLoads() {
        System.out.println("aa");
    }

    @Test
    public static void main1(String[] args){
        // 此初始化行为主要是为了获取WO平台该应用的token，如不使用会导致方式调用时被WO平台权限校验拒绝
        // 生产环境下可以将init的行为配置到spring的容器中来进行初始化
        // 初始化成功后，接下来每次调用业务方法，都会自动为发起的请求附上相应的token信息，而无需手工传入
        AppAuthParam appAuthParam = new AppAuthParam(appKey,appSecret,appId);
        TokenFetcher.init(appAuthParam);
        // 调用方法前，需初始化SDK的模块，共有4类
        DeviceControllerApi api = new UniUbiClient().DeviceClient();
        ResultDeviceOutput result = api.getDeviceUsingGET(appId,deviceKey);
        System.out.println(result);
        // 应用结束时调用，关闭定时自动获取token的线程池，如webApp关闭时
        TokenFetcher.shutdown();
    }


    @Test
    public static void main(String[] args){
        // 调用方法前，需初始化SDK的模块，共有4类
        // 此处入参为客户自定义的实现类，如TokenTask
        DeviceControllerApi api = new CustomTokenClient(new TokenTask()).DeviceClient();
        ResultDeviceOutput result = api.getDeviceUsingGET(appId,deviceKey);
        System.out.println(result);
        // 应用结束时调用，关闭定时自动获取token的线程池，如webApp关闭时
        TokenFetcher.shutdown();
    }

    /*//@Test
    public static void main(String[] args) {
        // 方法一
        System.out.println(System.currentTimeMillis());

       *//* // 方法二
        Date date = new Date();
        System.out.println(date.getTime());

        // 方法三
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        System.out.println(time);*//*
    }
*/


    //@Before
   /* @Test
    public void setup() {
        String appSecret = "B496892726AC4D0BBCBC0A6575EC9365";
        String appKey = "2CA42A1905B44CD18D8EE83049903306";

        AppAuthParam appAuthParam = new AppAuthParam(appKey, appSecret, appId);
        TokenFetcher.init(appAuthParam);
        TokenFetcherNoRunnable.getToken(appAuthParam);
        api = new UniUbiClient().DeviceClient();
        api = new CustomTokenClient(new TokenTask()).DeviceClient();
    }*/

    /**
     * 设备查询接口
     * <p>
     * 设备查询，deviceKey，appId必填
     */
    @Test
    public void getDeviceUsingGETTest() {
        /*String deviceKey = "84E0F4246B261501";
        ResultDeviceOutput response = api.getDeviceUsingGET("D40708B670E54D2DA06B1A3974A66EA4", deviceKey);*/
        AppAuthParam appAuthParam = new AppAuthParam(appKey,appSecret,appId);
        System.out.println(appAuthParam);
    }

    /**
     * 授权查询接口
     * <p>
     */
    @Test
    public void findPersonsAuthorizationUsingGET() {
        AppAuthParam appAuthParam = new AppAuthParam(appKey,appSecret,appId);
        TokenFetcher.init(appAuthParam);
        Map<String, Object> queryParams = new HashMap<>();
        ResultPageResultBeanAuthOutput reponse = api.findPersonsAuthorizationUsingGET(appId, queryParams);
        System.out.println(reponse);
    }

    @Test
    public void token(){
        System.out.println(new TokenTask().getToken());
        System.out.println("c37cce16c0dd5bb5571e8a73630e79b8");
    }

    @Test
    public void miao(){
        System.out.println(System.currentTimeMillis());
    }

}
