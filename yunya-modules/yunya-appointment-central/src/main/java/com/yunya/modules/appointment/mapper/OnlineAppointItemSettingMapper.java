package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.domain.query.OnlineAppointItemSettingQuery;
import com.yunya.feign.appointment.vo.OnlineAppointItemSettingModelVo;
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
    List<OnlineAppointItemSettingModelVo> selectByCondition(@Param("querys") OnlineAppointItemSettingQuery querys);
}