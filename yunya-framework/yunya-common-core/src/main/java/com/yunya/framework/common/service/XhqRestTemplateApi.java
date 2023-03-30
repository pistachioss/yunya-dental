package com.yunya.framework.common.service;

import com.alibaba.fastjson.JSONObject;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.QztXmlToMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Node;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @description:
 * @author: xy
 * @date 2022/9/26 16:44
 **/
@Service
@Slf4j
public class XhqRestTemplateApi {
    @Resource
    private RestTemplate restTemplate;

    public String getRequest(String url) {
        log.info("西湖区api调用url：{}", url);
        String resultStr = restTemplate.getForObject(url, String.class);
        return getRequestRes(resultStr);
    }

    public String postObject(String url, Object obj) {
        log.info("西湖区api调用url：{}，参数：{}", url, obj);
        String resultStr = restTemplate.postForObject(url, obj, String.class);
        return getRequestRes(resultStr);
    }

    private String getRequestRes(String resultStr) {
        log.info("西湖区api返回结果：{}", resultStr);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        String data = jsonObject.getString("data");
        // 解析XML字符串为Document对象
        Node node = QztXmlToMap.getNode(data, "retcode");
        String nodeValue = node.getTextContent();
        if (Objects.isNull(nodeValue) || !Objects.equals("1", nodeValue)) {
            log.error("西湖区api调用失败,nodeValue:{}", nodeValue);
            throw ClientServiceException.wrap(-1, data);
        }
        return data;
    }
}
