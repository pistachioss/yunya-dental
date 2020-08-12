package com.yunya.feign.appointment.factory;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.appointment.domain.query.AppointItemQuery;
import com.yunya.feign.appointment.vo.AppointmentItemEnableModelVo;
import com.yunya.feign.appointment.vo.AppointmentItemVo;
import com.yunya.models.appointment.AppointType;
import com.yunya.models.appointment.Appointment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统服务调用降级处理
 * @author yunya-lihuibin
 * @create 2020-07-31 17:00
 * @update yunya-lihuibin    2020-07-31
 */
@Slf4j
@Component
public class RemoteAppointmentFeignBackFactory implements RemoteAppointmentFeign {
    @Override
    public PageInfo<AppointmentItemVo> findAppItemList(AppointItemQuery form) {
        return null;
    }

    @Override
    public List<AppointmentItemVo> searchAppItem(AppointItemQuery baseQueryForm) {
        return null;
    }

    @Override
    public List<AppointmentItemEnableModelVo> findAvailableAppItem(String compClinId) {
        return null;
    }

    @Override
    public AppointType selectAppointTypeById(Integer id) {
        return null;
    }

    @Override
    public void updateAppointment(Appointment appointment) {

    }

    @Override
    public Appointment findAppointmentById(Integer id) {
        return null;
    }
}
