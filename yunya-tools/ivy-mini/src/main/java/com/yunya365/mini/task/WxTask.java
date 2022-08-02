package com.yunya365.mini.task;

import com.yunya365.mini.service.IOrderSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author xiangyang
 * @date 2022/08/02
 */
@Component
@EnableScheduling
@Slf4j
public class WxTask {

    @Resource
    private IOrderSettingService settingService;

    @Scheduled(cron = "${corn.order.confirm}")
    public void autoConfirm() {
        settingService.autoConfirm();
        log.info("订单自动确认收货");
    }

}
