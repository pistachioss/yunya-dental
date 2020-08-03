package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.domain.query.DeviceItemQuery;
import com.yunya.feign.appointment.vo.DeviceItemVo;
import com.yunya.models.appointment.ClinicDeviceItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicDeviceItemMapper extends Mapper<ClinicDeviceItem> {
    /**
     * 根据条件查询设备信息
     * @param query  查询条件
     * @return
     */
    List<DeviceItemVo> selectDeviceItemByExample(@Param("query") DeviceItemQuery query);

    /**
     * 根据设备id查询设备
     * @param id  设备id
     * @return
     */
    DeviceItemVo selectDeviceItemById(@Param("id") Integer id);
}