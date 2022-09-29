package com.yunya.modules.system.config;

import com.alibaba.fastjson.JSONObject;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.service.QztRestTemplateApi;
import com.yunya.framework.redis.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: xy
 * @date 2022/9/26 15:34
 **/
@Component
@Slf4j
public class RefreshQztToken implements CommandLineRunner {

    @Resource
    private RedisUtils redisUtils;
    @Value("${qzt.prefix}")
    private String qztPrefix;
    @Resource
    private QztRestTemplateApi qztRestTemplateApi;

    private static final String QZT_TOKEN = "/docking/api/token/init";

    /**
     * 刷新token的定时线程
     */
    @Resource(name = "scheduledPool")
    private ScheduledThreadPoolExecutor scheduleThreadPool;

    @Override
    public void run(String... args) {
        scheduleThreadPool.scheduleAtFixedRate(this::refreshToken,0, 11, TimeUnit.HOURS);
    }

    public void refreshToken(){
        String url = qztPrefix + QZT_TOKEN;
        JSONObject qztToken = qztRestTemplateApi.postObject(url, null);
        log.info("调用全诊通access_token返回结果是: {}", qztToken);
        String shortAccessToken = qztToken.getString("short-access-token");
        //redis工具根据项目自行修改
        redisUtils.set(RedisConstants.QZT_TOKEN, shortAccessToken, 11, TimeUnit.HOURS);
    }
}
