package com.yunya365.wechat.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 15:34
 **/
@Component
public class RefreshToken implements InitializingBean {

    @Resource
    private AccessTokenRunner accessTokenRunner;
    /**
     * 刷新token的定时线程
     */
    @Resource(name = "scheduledPool")
    private ScheduledThreadPoolExecutor scheduleThreadPool;

    @Override
    public void afterPropertiesSet() throws InterruptedException {
        scheduleThreadPool.scheduleAtFixedRate(() -> accessTokenRunner.refreshToken(),0, 7140, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(3);
        scheduleThreadPool.scheduleAtFixedRate(() -> accessTokenRunner.refreshTicket(),0, 7180, TimeUnit.SECONDS);
    }
}
