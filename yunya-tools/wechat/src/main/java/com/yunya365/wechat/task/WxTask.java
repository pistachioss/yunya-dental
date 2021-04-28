package com.yunya365.wechat.task;

import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya365.wechat.service.impl.WXService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/12/5
 */
@Component
@EnableScheduling
@Slf4j
public class WxTask {

    @Resource
    private WXService wxService;
    @Resource
    private RemoteReportServiceFeign reportServiceFeign;

    @Scheduled(cron = "${corn.cardPush}")
    public void cardUnusedTask() {
        long start = System.currentTimeMillis();
        List<WxTemplateMsgModel> pushCard = reportServiceFeign.listPushCard(0);
        wxService.batchPushTemplate(pushCard);
        long end = System.currentTimeMillis();
        log.info("卡券激活未使用批量推送消息时长：[{}] 秒", (end - start)/1000);
    }

    @Scheduled(cron = "${corn.cardPush}")
    public void cardExpiringTask() {
        long start = System.currentTimeMillis();
        List<WxTemplateMsgModel> pushCard = reportServiceFeign.listPushCard(1);
        wxService.batchPushTemplate(pushCard);
        long end = System.currentTimeMillis();
        log.info("卡券即将到期推送消息时长：[{}] 秒", (end - start)/1000);
    }

    @Scheduled(cron = "${corn.cardPush}")
    public void cardExpiredTask() {
        long start = System.currentTimeMillis();
        List<WxTemplateMsgModel> pushCard = reportServiceFeign.listPushCard(2);
        wxService.batchPushTemplate(pushCard);
        long end = System.currentTimeMillis();
        log.info("卡券到期推送消息时长：[{}] 秒", (end - start)/1000);
    }

    @Scheduled(cron = "${corn.appointConfim}")
    public void appointConfirmTask() {
        long start = System.currentTimeMillis();
        List<WxTemplateMsgModel> pushModels = reportServiceFeign.listPushConfirmAppoint();
        wxService.batchPushTemplate(pushModels);
        long end = System.currentTimeMillis();
        log.info("预约确认推送消息时长：[{}] 秒", (end - start)/1000);
    }
}
