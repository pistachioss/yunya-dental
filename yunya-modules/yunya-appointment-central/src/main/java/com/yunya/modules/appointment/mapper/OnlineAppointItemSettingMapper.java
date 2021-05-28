package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.domain.query.OnlineAppointItemSettingQuery;
import com.yunya.feign.appointment.vo.OnlineAppointItemSettingVo;
import com.yunya.models.appointment.OnlineAppointItemSetting;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OnlineAppointItemSettingMapper extends Mapper<OnlineAppointItemSetting> {
    /**
     * 根据条件批量查询预约项目
     * @param querys 查询条件
     * @return 返回结果列表
     */
    List<OnlineAppointItemSettingVo> selectByCondition(@Param("querys") OnlineAppointItemSettingQuery querys);

    /**
     * 根据医生ID查询线上可预约项目
     * @param dentistId 医生ID
     * @return 返回查询结果
     */
    OnlineAppointItemSettingVo findItemSettingByDentistId(@Param("dentistId") Integer dentistId, @Param("orgId") Integer orgId);
}