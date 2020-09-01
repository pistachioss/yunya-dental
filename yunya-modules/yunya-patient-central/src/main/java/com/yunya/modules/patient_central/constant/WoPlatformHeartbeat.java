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

    /**
     * 心跳版本访问post接口方法
     * @param url
     * @param data
     * @return
     */
    public static JSONObject httpPostHeartbeatAccess(String url, NameValuePair[] data){
        try {
            String postURL =url;
            PostMethod postMethod = null;
            postMethod = new PostMethod(postURL) ;
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;
            //参数设置，需要注意的就是里边不能传NULL，要传空字符串
            postMethod.setRequestBody(data);
            org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
            int response = httpClient.executeMethod(postMethod); // 执行POST方法
            String result = postMethod.getResponseBodyAsString() ;

            System.out.println("*******************************************************");
            JSONObject obj = JSONObject.parseObject(result);
            System.out.println(obj.toJSONString());
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
