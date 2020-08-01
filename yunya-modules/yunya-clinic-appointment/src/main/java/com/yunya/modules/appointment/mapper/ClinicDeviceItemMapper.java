package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.vo.DeviceVo;
import com.yunya.models.appointment.ClinicDeviceItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicDeviceItemMapper extends Mapper<ClinicDeviceItem> {
    /**
     * 根据门诊id 查询门诊设备
     * @param compClinId
     * @return
     */
    List<DeviceVo> selectDeviceItemByOrgId(@Param("compClinId") Integer compClinId);
}