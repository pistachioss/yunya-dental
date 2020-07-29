package com.yunya.modules.appointment.mapper;

import com.yunya.models.appointment.ClinicDeviceItem;

public interface ClinicDeviceItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ClinicDeviceItem record);

    int insertSelective(ClinicDeviceItem record);

    ClinicDeviceItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ClinicDeviceItem record);

    int updateByPrimaryKey(ClinicDeviceItem record);
}