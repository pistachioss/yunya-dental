package com.yunya.framework.common.service;

import com.alibaba.fastjson.JSONObject;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.QztXmlToMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Node;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

import static com.yunya.framework.common.enums.ExceptionCode.BAD_REQUEST;

/**
 * @description:
 * @author: xy
 * @date 2023/9/26 16:44
 **/
@Service
@Slf4j
public class ScRestTemplateApi {
    @Resource
    private RestTemplate restTemplate;

    public String getRequest(String url) {
        log.info("上城区api调用url：{}", url);
        String resultStr = restTemplate.getForObject(url, String.class);
        return getRequestRes(resultStr);
    }

    public String postObject(String url, Object obj) {
        log.info("上城区api调用url：{}，参数：{}", url, obj);
        String resultStr = restTemplate.postForObject(url, obj, String.class);
        return getRequestRes(resultStr);
    }

    private String getRequestRes(String resultStr) {
        log.info("上城区api返回结果：{}", resultStr);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        String data = jsonObject.getString("data");
        // 解析XML字符串为Document对象
        Node node = QztXmlToMap.getNode(data, "retcode");
        String nodeValue = node.getTextContent();
        if (Objects.isNull(nodeValue) || !Objects.equals("1", nodeValue)) {
            log.error("上城区api调用失败,nodeValue:{}", nodeValue);
            throw ClientServiceException.wrap(-1, data);
        }
        return data;
    }

    public JSONObject postObjectToken(String url, Map<String, String> param) {
        log.info("上城区api调用token url：{}，参数：{}", url, param);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            formData.add(entry.getKey(), entry.getValue());
        }
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(formData, headers), String.class);
        return getRequestTokenRes(response);
    }

    private JSONObject getRequestTokenRes(ResponseEntity<String> response) {
        // 处理响应
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw ClientServiceException.wrap(BAD_REQUEST);
        }
        String resultStr = response.getBody();
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        Integer code = jsonObject.getInteger("code");
        String message = jsonObject.getString("message");
        if (Objects.equals(code, 0)) {
            log.error("上城区全诊通token api调用失败，请求链接：{}：结果：{}", resultStr, jsonObject);
            throw ClientServiceException.wrap(code, message);
        }
        return jsonObject;
    }
}
