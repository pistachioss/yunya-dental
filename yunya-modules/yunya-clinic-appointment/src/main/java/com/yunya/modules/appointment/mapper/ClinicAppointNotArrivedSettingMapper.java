package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.vo.AppointNotArrivedSettingVo;
import com.yunya.models.appointment.ClinicAppointNotArrivedSetting;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ClinicAppointNotArrivedSettingMapper extends Mapper<ClinicAppointNotArrivedSetting> {

    /**
     * 通过用户id查询预约未到设置
     * @param userId
     * @return
     */
    AppointNotArrivedSettingVo findAppointNotArrivedSettingByUserId(@Param("userId") Integer userId);

}