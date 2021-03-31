package com.yunya365.wechat.config;

import com.yunya365.wechat.service.impl.WXService;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 15:34
 **/
@Component
public class RefreshToken implements InitializingBean {
    @Autowired
    private WXService wxService;

    /**
     * 刷新token的定时线程
     */
    private ScheduledThreadPoolExecutor scheduledPool = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("refresh-wx-access-token-%d").daemon(true).build());


    @Override
    public void afterPropertiesSet() {
        scheduledPool.scheduleAtFixedRate(() -> wxService.refreshToken(),0, 7000, TimeUnit.SECONDS);
    }
}
