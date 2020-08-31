package com.yunya.modules.patient_central.constant;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.Serializable;

/**
 * 简单介绍:</br> Wo平台-心跳本版
 *
 * @author: WY
 * @date 2020/8/29 15:43
 * @description:
 * @since: 1.0.0
 */
public class WoPlatformHeartbeat implements Serializable {
    public JSONObject httpPostHeartbeatAccess(String url,NameValuePair[] data){
        try {
            System.out.println("----------------------------------------------------------------------");
            //String postURL ="http://192.168.19.96:8090/person/create";
            /* JSONObject object = new JSONObject();
              object.put("name","心跳测试113");
              String s = object.toJSONString();
              NameValuePair[] data = {
              new NameValuePair("pass","123456"),
              new NameValuePair("person",s)

      };*/
            // WoPlatformHeartbeat.httpPostHeartbeatAccess(WoPlatformConstants.URL+"/person/create",) todo

            String postURL =url;
            PostMethod postMethod = null;
            postMethod = new PostMethod(postURL) ;
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;
            //参数设置，需要注意的就是里边不能传NULL，要传空字符串
            /*JSONObject object = new JSONObject();
            object.put("name","心跳测试113");
            String s = object.toJSONString();
            NameValuePair[] data = {
                    new NameValuePair("pass","123456"),
                    new NameValuePair("person",s)

            };*/
            postMethod.setRequestBody(data);

            org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
            int response = httpClient.executeMethod(postMethod); // 执行POST方法
            String result = postMethod.getResponseBodyAsString() ;

            System.out.println("*******************************************************");
            JSONObject obj = JSONObject.parseObject(result);
            JSONObject jsonObject = obj.getJSONObject("data");
            System.out.println(jsonObject.get("id"));
            return jsonObject;
            //return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
