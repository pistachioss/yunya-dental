package com.yunya365.mini.config;

import com.yunya365.mini.service.WxApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.yunya.framework.common.constant.WxMiniAuthConstant.*;


/**
 * @description:
 * @author: xy
 * @date 2021/3/24 15:34
 **/
@Component
public class RefreshAccessToken implements InitializingBean {

    @Resource
    private WxApi wxApi;
    /**
     * 刷新token的定时线程
     */
    @Resource(name = "scheduleExecutorService")
    private ScheduledThreadPoolExecutor scheduleThreadPool;

    @Override
    public void afterPropertiesSet(){
        scheduleThreadPool.scheduleAtFixedRate(() -> wxApi.refreshToken(),0, ACCESS_TOKEN_EXPIRE_GAP, TimeUnit.SECONDS);
    }
}
