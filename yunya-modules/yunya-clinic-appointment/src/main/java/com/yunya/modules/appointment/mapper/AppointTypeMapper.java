package com.yunya.modules.appointment.mapper;

import com.yunya.models.appointment.AppointType;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface AppointTypeMapper extends Mapper<AppointType> {

    /**
     * 根据预约项目类型名字查询数据
     * @param name  类型名字
     * @return
     */
    AppointType selectAppointTypeByName(@Param("name") String name);

}