package com.yunya.modules.system.config;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.service.BjRestTemplateApi;
import com.yunya.framework.common.service.QztRestTemplateApi;
import com.yunya.framework.redis.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: xy
 * @date 2022/9/26 15:34
 **/
@Component
@Slf4j
public class RefreshBjToken implements CommandLineRunner {

    @Resource
    private RedisUtils redisUtils;
    @Value("${bj.prefix}")
    private String bjPrefix;
    @Value("${bj.appKey}")
    private String bjAppkey;
    @Value("${bj.appSecret}")
    private String bjAppSecret;
    @Resource
    private BjRestTemplateApi bjRestTemplateApi;

    private static final String BJ_TOKEN = "/api/outer/token";

    /**
     * 刷新token的定时线程
     */
    @Resource(name = "scheduledPool")
    private ScheduledThreadPoolExecutor scheduleThreadPool;

    @Override
    public void run(String... args) {
        scheduleThreadPool.scheduleAtFixedRate(this::refreshToken, 0, 11, TimeUnit.HOURS);
    }

    public void refreshToken() {
        String url = bjPrefix + BJ_TOKEN;
        Map<String, String> param = Maps.newHashMap();
        param.put("appKey", bjAppkey);
        param.put("appSecret", bjAppSecret);
        JSONObject bjToken = bjRestTemplateApi.postObject(url, param);
        log.info("调用滨江access_token返回结果是: {}", bjToken);
        String token = bjToken.getString("token");
        //redis工具根据项目自行修改
        redisUtils.set(RedisConstants.BJ_TOKEN, token, 11, TimeUnit.HOURS);
    }
}
