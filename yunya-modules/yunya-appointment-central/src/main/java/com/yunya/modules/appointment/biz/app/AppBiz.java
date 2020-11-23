package com.yunya.modules.appointment.biz.app;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.appointment.Appointment;
import com.yunya.modules.appointment.mapper.AppointmentMapper;
import org.springframework.stereotype.Service;

/**
 * @program: yunya-dental
 * @description: app端相关业务
 * @author: LHB
 * @create: 2020-11-23 13:11
 **/
@Service
public class AppBiz extends BaseBiz<AppointmentMapper, Appointment> {
}
