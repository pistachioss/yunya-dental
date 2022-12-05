package com.yunya.modules.appointment.biz.web;

import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.appointment.ReservationRateLimit;
import com.yunya.modules.appointment.biz.app.ReservationCodeBiz;
import com.yunya.modules.appointment.mapper.ReservationRateLimitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationLimitBiz extends BaseBiz<ReservationRateLimitMapper, ReservationRateLimit> {

    @Autowired
    private RemoteSystemServiceFeign systemServiceFeign;

    @Autowired
    private ReservationCodeBiz reservationCodeBiz;


}
