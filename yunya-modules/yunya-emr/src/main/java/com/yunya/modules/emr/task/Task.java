package com.yunya.modules.emr.task;

import com.yunya.modules.emr.biz.BjMedicalBiz;
import com.yunya.modules.emr.biz.QztMedicalBiz;
import com.yunya.modules.emr.biz.ScMedicalBiz;
import com.yunya.modules.emr.biz.XhqMedicalBiz;
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
    @Resource
    private BjMedicalBiz bjMedicalBiz;
    @Resource
    private XhqMedicalBiz xhqMedicalBiz;
    @Resource
    private ScMedicalBiz scMedicalBiz;

    @Scheduled(cron = "00 00 01 * * ?")
    @RequestMapping("/white/qztMedical/sync")
    public void syncDoctor() {
        qztMedicalBiz.sync();
    }

    @Scheduled(cron = "00 00 01 * * ?")
    @RequestMapping("/white/bjMedical/sync")
    public void syncBjDoctor() {
        bjMedicalBiz.sync();
    }

    @Scheduled(cron = "00 30 01 * * ?")
    @RequestMapping("/white/xhqMedical/sync")
    public void syncXhqDoctor() {
        xhqMedicalBiz.sync();
    }

    @Scheduled(cron = "00 40 01 * * ?")
    @RequestMapping("/white/scMedical/sync")
    public void syncScDoctor() {
        scMedicalBiz.sync();
    }


}
