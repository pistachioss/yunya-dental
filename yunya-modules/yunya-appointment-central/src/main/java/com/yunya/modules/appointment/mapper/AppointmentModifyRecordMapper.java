package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.domain.query.AppointModifyRecordQuery;
import com.yunya.feign.appointment.vo.AppointModifyRecordVo;
import com.yunya.models.appointment.AppointmentModifyRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AppointmentModifyRecordMapper extends Mapper<AppointmentModifyRecord> {

    /**
     * 通过id查询
     * @param id
     * @return
     */
    AppointModifyRecordVo findAppointModifyRecordById(@Param("id") Integer id);

    /**
     * 根据条件查询修改内容
     * @param query  条件参数
     * @return
     */
    List<AppointModifyRecordVo> findAppointModifyRecordByExample(@Param("query") AppointModifyRecordQuery query);
}