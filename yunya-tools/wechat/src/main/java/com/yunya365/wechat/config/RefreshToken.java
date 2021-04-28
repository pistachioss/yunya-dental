package com.yunya365.wechat.config;

import org.apache.commons.lang3.concurrent.*;
import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;

import javax.annotation.*;
import java.util.concurrent.*;

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
    private ScheduledThreadPoolExecutor scheduledPool = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("refresh-wx-access-token-%d").daemon(true).build());


    @Override
    public void afterPropertiesSet() {
        scheduledPool.scheduleAtFixedRate(() -> accessTokenRunner.refreshToken(),0, 7140, TimeUnit.SECONDS);
    }
}
