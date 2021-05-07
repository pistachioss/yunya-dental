package com.yunya365.wechat.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author xiangyang
 * @date 2020/12/5
 */
@Component
@Slf4j
public class WxTask {

    @Resource
    private RestTemplate restTemplate;

    //0 0 0 * * ? 每日零点
    @Scheduled(cron = "${corn.data}")
    public void process() {
        long start = System.currentTimeMillis();

    }
}
