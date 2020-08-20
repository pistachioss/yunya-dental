package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.domain.query.AppointSettingQuery;
import com.yunya.feign.appointment.domain.query.DeviceTypeQuery;
import com.yunya.feign.appointment.vo.AppointSettingVo;
import com.yunya.feign.appointment.vo.DeviceTypeVo;
import com.yunya.models.appointment.ClinicAppointmentSetting;
import com.yunya.models.appointment.ClinicDeviceType;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicAppointmentSettingMapper extends Mapper<ClinicAppointmentSetting> {

    /**
     * 根据条件查询预约设置
     * @param query  查询条件
     * @return
     */
    AppointSettingVo selectAppointSettingByExample(@Param("query") AppointSettingQuery query);
}