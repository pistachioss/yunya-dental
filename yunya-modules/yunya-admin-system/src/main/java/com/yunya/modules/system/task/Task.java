package com.yunya.modules.system.task;

import com.yunya.modules.system.biz.BjDoctorBiz;
import com.yunya.modules.system.biz.QztDoctorBiz;
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
    private QztDoctorBiz qztDoctorBiz;
    @Resource
    private BjDoctorBiz BjDoctorBiz;

    @Scheduled(cron = "${corn.qzt-doctor}")
    @RequestMapping("/white/qztDocter/sync")
    public void syncDoctor() {
        qztDoctorBiz.sync();
    }

    @Scheduled(cron = "${corn.qzt-doctor}")
    @RequestMapping("/white/bjDocter/sync")
    public void syncBjDoctor() {
        BjDoctorBiz.sync();
    }

}
