package com.yunya.framework.common.service;

import com.alibaba.fastjson.JSONObject;
import com.yunya.framework.common.exception.ClientServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @description:
 * @author: xy
 * @date 2022/9/26 16:44
 **/
@Service
@Slf4j
public class BjRestTemplateApi {
    @Resource
    private RestTemplate restTemplate;

    public <T> T getRestResult(String url, Class<T> clazz) {
        return this.getRequest(url).toJavaObject(clazz);
    }

    public JSONObject getRequest(String url) {
        log.info("滨江api调用url：{}", url);
        String resultStr = restTemplate.getForObject(url, String.class);
        return getRequestRes(resultStr);
    }

    public JSONObject postObject(String url, Object obj) {
        log.info("滨江api调用url：{}，参数：{}", url, obj);
        String resultStr = restTemplate.postForObject(url, obj, String.class);
        return getRequestRes(resultStr);
    }

    private JSONObject getRequestRes(String resultStr) {
        log.error("滨江api返回结果：{}", resultStr);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        boolean success = jsonObject.getBoolean("success");
        String message = jsonObject.getString("msg");
        if (!success) {
            log.error("滨江api调用失败，请求链接：{}：结果：{}", resultStr, jsonObject);
            throw ClientServiceException.wrap(999, message);
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.nonNull(data)) {
            return data;
        }
        return jsonObject;
    }
}
