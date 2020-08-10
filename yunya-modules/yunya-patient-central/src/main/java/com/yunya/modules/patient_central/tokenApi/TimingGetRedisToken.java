package com.yunya.modules.patient_central.tokenApi;

import com.alibaba.fastjson.JSONObject;
import com.yunya.framework.common.utils.MD5Util;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.modules.patient_central.constant.WoPlatformConstants;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/10 16:25
 * @description:
 * @since: 1.0.0
 */
@Configuration
public class TimingGetRedisToken {

    @Autowired
    private RedisUtils redisUtils;

    public String getToken(){
        return redisUtils.get("token");
    }


    @Scheduled(cron = "0 0 0/20 * * ?")
    public String getRedisToken() throws IOException {
        String url ="http://wo-api.uni-ubi.com/v1/"+WoPlatformConstants.APPID+"/auth";
        long timestamp = System.currentTimeMillis();
        String link = WoPlatformConstants.APPKEY+System.currentTimeMillis()+WoPlatformConstants.APPSECRET;
        String sign = MD5Util.getStringMD5(link);
        Map<String, String> header = new HashMap<>();
        header.put("appKey",WoPlatformConstants.APPKEY);
        header.put("timestamp", String.valueOf(timestamp));
        header.put("sign",sign);
        String resoult = sendGet(url, header);
        JSONObject ResoultHeader = JSONObject.parseObject(resoult);
        String token = (String) ResoultHeader.get("data");
        redisUtils.set("token",token);
        return token;
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
}
