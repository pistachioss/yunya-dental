package com.yunya.modules.appointment.mapper;


import com.yunya.models.appointment.ReservationRateLimit;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRateLimitMapper extends Mapper<ReservationRateLimit> {

    void insertList(@Param("list") List<ReservationRateLimit> list);

    void updateList(@Param("list") List<ReservationRateLimit> list);

    int reduceLimit(Integer reservationLimitId);

    List<ReservationRateLimit> listRemaining(@Param("orgName") String orgName,@Param("configDate") LocalDate configDate);
}