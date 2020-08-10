package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.domain.query.AppointOperationQuery;
import com.yunya.feign.appointment.vo.AppointOperationRecordVo;
import com.yunya.models.appointment.AppointmentOperateRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AppointmentOperateRecordMapper extends Mapper<AppointmentOperateRecord> {

    /**
     * 根据条件查询预约操作记录
     * @param query 条件参数
     * @return
     */
    List<AppointOperationRecordVo> findAppointOperationRecordByExample(@Param("query") AppointOperationQuery query);

}