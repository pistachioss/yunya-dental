package com.yunya.modules.appointment.mapper;


import com.yunya.models.appointment.ReservationRateLimit;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface ReservationRateLimitMapper extends Mapper<ReservationRateLimit> {

    int reduceLimit(Integer reservationLimitId);

    List<ReservationRateLimit> listRemaining(@Param("orgId") Integer orgId,@Param("configDate") Date configDate);
}