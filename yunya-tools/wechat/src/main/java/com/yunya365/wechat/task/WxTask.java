package com.yunya365.wechat.task;

import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya365.wechat.service.impl.WXService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author xiangyang
 * @date 2020/12/5
 */
@Component
@Slf4j
public class WxTask {

    @Resource
    private WXService wxService;
    @Resource
    private RemoteReportServiceFeign reportServiceFeign;

    @Scheduled(cron = "${corn.wxPushDate}")
    public void process() {
        long start = System.currentTimeMillis();
//        reportServiceFeign.listPushCard()
    }
}
