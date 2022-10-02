package com.yunya.modules.emr.task;

import com.yunya.modules.emr.biz.QztMedicalBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author xiangyang
 * @date 2022/08/02
 */
@Component
@EnableScheduling
@Slf4j
@RestController
public class Task {

    @Resource
    private QztMedicalBiz qztMedicalBiz;

    @Scheduled(cron = "00 00 01 * * ?")
    @RequestMapping("/white/qztMedical/sync")
    public void syncDoctor() {
        qztMedicalBiz.sync();
    }

}
