package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.domain.query.ReservationQuery;
import com.yunya.feign.appointment.vo.ReservationVo;
import com.yunya.models.appointment.Reservation;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ReservationMapper extends Mapper<Reservation> {
    List<ReservationVo> findByCondition(@Param("query") ReservationQuery query);
}