package com.yunya.modules.patient;

import com.alibaba.fastjson.JSONObject;
import com.uniubi.sdk.api.DeviceControllerApi;
import com.uniubi.sdk.auth.authToken.AppAuthParam;
import com.uniubi.sdk.auth.authToken.TokenFetcher;
import com.uniubi.sdk.client.CustomTokenClient;
import com.uniubi.sdk.client.UniUbiClient;
import com.uniubi.sdk.model.DeviceOutput;
import com.uniubi.sdk.model.ResultDeviceOutput;
import com.uniubi.sdk.model.ResultPageResultBeanAuthOutput;
import com.yunya.feign.patient_central.domain.vo.PatientBaseInfoVo;
import com.yunya.framework.common.utils.MD5Util;
import com.yunya.modules.patient.tokenApi.TokenTask;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import com.yunya.modules.patient_central.constant.WoPlatformConstants;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest(classes = YunyaPatientApplicationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
class YunyaPatientApplicationTests {
    PatientBaseInfoBiz patientBaseInfoBiz = new PatientBaseInfoBiz();
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
    public static void main12(String[] args){
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
    public static void main1(String[] args){
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
    }

    @Test
    public void miao(){
        long time = new Date().getTime();
        System.out.println(new Date().getTime());
    }

    @Test
    public void selectAllByIdList(){
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        List<PatientBaseInfoVo> patientInfoByIds = patientBaseInfoBiz.findPatientInfoByIds(ids);
        System.out.println(patientInfoByIds);
    }


    @Test
    public static void main133(String[] args){
        // 此初始化行为主要是为了获取WO平台该应用的token，如不使用会导致方式调用时被WO平台权限校验拒绝
        // 生产环境下可以将init的行为配置到spring的容器中来进行初始化
        // 初始化成功后，接下来每次调用业务方法，都会自动为发起的请求附上相应的token信息，而无需手工传入
        AppAuthParam appAuthParam = new AppAuthParam(appKey,appSecret,appId);
        TokenFetcher.init(appAuthParam);
        // 调用方法前，需初始化SDK的模块，共有4类
        DeviceControllerApi api = new UniUbiClient().DeviceClient();
        ResultDeviceOutput result = api.getDeviceUsingGET(appId,deviceKey);
        DeviceOutput data = result.getData();
        System.out.println(data.toString());
        // 应用结束时调用，关闭定时自动获取token的线程池，如webApp关闭时
        TokenFetcher.shutdown();
    }




    @Test
    public String getToken() {
        long l = System.currentTimeMillis();
        System.out.println(l);
        String S = WoPlatformConstants.APPKEY+System.currentTimeMillis()+WoPlatformConstants.APPSECRET;
        System.out.println(S);
        String token = MD5Util.getStringMD5(S);
        System.out.println(token);
        AppAuthParam appAuthParam = new AppAuthParam(WoPlatformConstants.APPKEY,WoPlatformConstants.APPSECRET,WoPlatformConstants.APPID);
        TokenFetcher.init(appAuthParam);
        return token;
    }


    @Test
    public void qingqiu() throws IOException {
       String url ="http://wo-api.uni-ubi.com/v1/D40708B670E54D2DA06B1A3974A66EA4/auth";
        long l = System.currentTimeMillis();
        System.out.println(l);
        String S = WoPlatformConstants.APPKEY+l+WoPlatformConstants.APPSECRET;
        String sign = MD5Util.getStringMD5(S);
        Map<String, String> header = new HashMap<>();
        header.put("appKey","2CA42A1905B44CD18D8EE83049903306");
        header.put("timestamp", String.valueOf(l));
        header.put("sign",sign);
        String resoult = sendGet(url, header);
        JSONObject ResoultHeader = JSONObject.parseObject(resoult);
        System.out.println(ResoultHeader);
        System.out.println(ResoultHeader.get("data"));

    }





    /**
     * 向指定URL发送GET方法的请求
     */
    public String sendGet(String url, Map<String, String> header) throws UnsupportedEncodingException, IOException {
        String result = "";
        BufferedReader in = null;
        String urlNameString = url;
        URL realUrl = new URL(urlNameString);
        // 打开和URL之间的连接
        URLConnection connection = realUrl.openConnection();
        //设置超时时间
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(15000);
        // 设置通用的请求属性
        if (header!=null) {
            Iterator<Map.Entry<String, String>> it =header.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, String> entry = it.next();
                System.out.println(entry.getKey()+":"+entry.getValue());
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

        // 建立实际的连接
        connection.connect();
        // 获取所有响应头字段
        Map<String, List<String>> map = connection.getHeaderFields();
        // 遍历所有的响应头字段
        for (String key : map.keySet()) {
            System.out.println(key + "--->" + map.get(key));
        }
        // 定义 BufferedReader输入流来读取URL的响应，设置utf8防止中文乱码
        in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }
        if (in != null) {
            in.close();
        }
        return result;
    }




    @Test
    public static void main2(String[] args) throws ParseException {
        String format = "HH:mm:ss";
        Date nowTime = new SimpleDateFormat(format).parse("09:60:00");
        Date startTime = new SimpleDateFormat(format).parse("09:20:00");
        Date endTime = new SimpleDateFormat(format).parse("09:50:59");
        System.out.println(isEffectiveDate(nowTime, startTime, endTime));
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    @Test
    public static void main4(String[] args) {
        try {
            System.out.println("----------------------------------------------------------------------");
            String postURL ="http://192.168.19.96:8090/setPassWord";
            PostMethod postMethod = null;
            postMethod = new PostMethod(postURL) ;
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;
        //参数设置，需要注意的就是里边不能传NULL，要传空字符串
            NameValuePair[] data = {
                    new NameValuePair("oldPass","123456"),
                    new NameValuePair("newPass","123456")

            };
            postMethod.setRequestBody(data);

            org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
            int response = httpClient.executeMethod(postMethod); // 执行POST方法
            String result = postMethod.getResponseBodyAsString() ;

            System.out.println(response);
            System.out.println(result);
            //return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Test
    public static void main(String[] args) {
        try {
            System.out.println("----------------------------------------------------------------------");
            String postURL ="http://192.168.19.96:8090/person/create";
            PostMethod postMethod = null;
            postMethod = new PostMethod(postURL) ;
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;
            //参数设置，需要注意的就是里边不能传NULL，要传空字符串

            JSONObject object = new JSONObject();
            object.put("name","心跳测试113");
            String s = object.toJSONString();
            NameValuePair[] data = {
                    new NameValuePair("pass","123456"),
                    new NameValuePair("person",s)

            };
            postMethod.setRequestBody(data);

            org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
            int response = httpClient.executeMethod(postMethod); // 执行POST方法
            String result = postMethod.getResponseBodyAsString() ;

            System.out.println(response);
            System.out.println(result);
            System.out.println("*******************************************************");
            JSONObject obj = JSONObject.parseObject(result);
            JSONObject jsonObject = obj.getJSONObject("data");
            System.out.println(jsonObject.get("id"));
            //return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }





}
