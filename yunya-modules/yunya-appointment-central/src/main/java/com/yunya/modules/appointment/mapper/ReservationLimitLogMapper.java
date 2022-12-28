package com.yunya.modules.appointment.mapper;

import com.yunya.models.appointment.ReservationLimitLog;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ReservationLimitLogMapper extends Mapper<ReservationLimitLog> {

    void insertList(@Param("list") List<ReservationLimitLog> list);
}