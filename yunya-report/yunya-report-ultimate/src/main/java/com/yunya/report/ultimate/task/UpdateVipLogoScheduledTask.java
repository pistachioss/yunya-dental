package com.yunya.report.ultimate.task;

import com.yunya.report.ultimate.biz.PatientVipLogoBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UpdateVipLogoScheduledTask {

    /** 注入日志对象 */
    private final Logger logger =
            LoggerFactory.getLogger(UpdateVipLogoScheduledTask.class);

    @Autowired
    private PatientVipLogoBiz patientVipLogoBiz;

    /** 每周四玩上， 根据规则跑数据并为老患者会员和365卡会员更新患者分类标识*/
    @Scheduled(cron = "0 0 0 ? * 5")
    public void execute() {
        System.out.println("begin vip logo tash：" + new Date());
        patientVipLogoBiz.updateVipLogoInfo(9528);
        System.out.println("end vip logo tash：" + new Date());
    }
}
