package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.domain.query.DeviceTypeQuery;
import com.yunya.feign.appointment.vo.DeviceTypeVo;
import com.yunya.models.appointment.ClinicDeviceType;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicDeviceTypeMapper extends Mapper<ClinicDeviceType> {

    /**
     * 根据条件查询设备类型列表
     * @param query  查询条件
     * @return
     */
    List<DeviceTypeVo> selectDeviceTypeByExample(@Param("query") DeviceTypeQuery query);
}