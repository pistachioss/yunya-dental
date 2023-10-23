package com.yunya.modules.emr.config;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.service.ScRestTemplateApi;
import com.yunya.framework.redis.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: xy
 * @date 2023/9/26 15:34
 **/
@Component
@Slf4j
public class RefreshScToken implements CommandLineRunner {

    @Resource
    private RedisUtils redisUtils;
    @Value("${sc.domain}")
    private String scPrefix;
    @Resource
    private ScRestTemplateApi scRestTemplateApi;

    private static final String SC_TOKEN = "/docking/api/token/getToken";

    private static final Map<String, String> scMap = Maps.newHashMap();

    static {
        scMap.put("zPeJysdc86mz0VzaklJNIH1tQDehO07fb9fJ+YKDHoU=", "杭州艾维鲲鹏路口腔门诊部");
        scMap.put("Y9oMw4E2xoLHsQmOlEVyDUk5lWbIRuINbw2yX2GkJ1Y=", "杭州艾维医疗投资管理有限公司春江花月口腔门诊部");
        scMap.put("FM7FSzC/LnjtuydNABEcRG18Opjm1ylGlAdkvEcr+Ao=", "杭州艾维乾元口腔门诊部");
    }


    /**
     * 刷新token的定时线程
     */
    @Resource(name = "scheduledPool")
    private ScheduledThreadPoolExecutor scheduleThreadPool;

    @Override
    public void run(String... args) {
        scheduleThreadPool.scheduleAtFixedRate(this::refreshToken,0, 7140, TimeUnit.SECONDS);
    }

    public void refreshToken(){
        HashMap<String, String> map = Maps.newHashMap();
        for (Map.Entry<String, String> entry : scMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String url = scPrefix + SC_TOKEN;
            map.put("institutionKey", key);
            JSONObject scToken = scRestTemplateApi.postObjectToken(url, map);
            log.info("调用上城区门诊{}，access_token返回结果是: {}", value,scToken);
            String shortAccessToken = scToken.getString("short-access-token");
            //redis工具根据项目自行修改
            redisUtils.set(RedisConstants.SC_TOKEN + value, shortAccessToken, 7140, TimeUnit.SECONDS);
        }
    }
}
