package com.yunya.modules.appointment.scheduled_task;

import com.yunya.models.appointment.Appointment;
import com.yunya.modules.appointment.biz.AppointmentBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
     * 定时任务，超过预约当前24点，将预约状态设置为失约
     */
    @Scheduled(cron = "5 * * * * ?")
    public void missedAppointment(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        int missedAppointmentNum = 0;
        List<Appointment> missedAppointments = appointmentBiz.findMissedAppointmentByDate(calendar.getTime());
        if (missedAppointments != null && !missedAppointments.isEmpty()){
            missedAppointmentNum = missedAppointments.size();
            missedAppointments.forEach(appointment -> {
                appointment.setAppointStatus((byte) 3);
                // TODO
//                appointmentBiz.updateSelectiveById(appointment);
            });
        }
        logger.info("预约状态定时任务>>"+"处理了"+missedAppointmentNum+"个失约患者！");
    }

}
