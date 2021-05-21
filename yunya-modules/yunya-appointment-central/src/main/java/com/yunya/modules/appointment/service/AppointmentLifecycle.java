package com.yunya.modules.appointment.service;

import com.yunya.models.appointment.Appointment;
import org.springframework.stereotype.Component;

/**
 * @program: yunya-dental
 * @description: 预约通知接口
 * @author: LHB
 * @create: 2021-05-21 15:19
 **/
@Component
public interface AppointmentLifecycle {
    /**
     * 预约新建成功时调用该接口，可以实现这个接口，在方法中写对应逻辑
     * @param appointment 预约信息
     */
    void build(Appointment appointment);
}
