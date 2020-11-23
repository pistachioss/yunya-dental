package com.yunya.modules.appointment.async;
import com.yunya.modules.appointment.biz.web.AppointmentBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 预约相关定时任务
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 17:26
 * @update yunya-lihuibin    2020-08-10    新建
 */
@Component
@EnableScheduling
public class AppointScheduledTask {

    /** 注入日志对象 */
    private final Logger logger = LoggerFactory.getLogger(AppointScheduledTask.class);

    @Autowired
    private AppointmentBiz appointmentBiz;

    /**
     * 定时任务，超过预约当前24点，将预约状态设置为失约 定时任务每天01：00：00执行 00 00 01 * * ?
     */
    @Async("customizeExecutor")
    @Scheduled(cron = "00 00 01 * * ?")
    public void missedAppointment(){
        Integer missedAppointmentNum = appointmentBiz.missedAppointmentsStatusSchedule();
        logger.info("处理了"+missedAppointmentNum+"个失约患者！");
    }

}
