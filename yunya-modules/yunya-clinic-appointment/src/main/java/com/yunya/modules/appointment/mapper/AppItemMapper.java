package com.yunya.modules.appointment.mapper;

import com.yunya.models.appointment.AppItem;
import com.yunya.modules.appointment.vo.AppointmentItemTypeVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AppItemMapper extends Mapper<AppItem> {
    /**
     * 根据门诊id、预约名称查询预约信息
     *
     * @param compClinId 门诊id
     * @param keyword    关键字
     * @return
     */
    List<AppointmentItemTypeVo> selectAppitemByName(@Param("compClinId") String compClinId, @Param("keyword") String keyword);
}