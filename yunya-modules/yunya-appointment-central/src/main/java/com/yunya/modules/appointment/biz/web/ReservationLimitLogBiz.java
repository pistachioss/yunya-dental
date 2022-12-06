package com.yunya.modules.appointment.biz.web;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.appointment.ReservationLimitLog;
import com.yunya.models.appointment.ReservationRateLimit;
import com.yunya.modules.appointment.mapper.ReservationLimitLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReservationLimitLogBiz extends BaseBiz<ReservationLimitLogMapper, ReservationLimitLog> {

    public void saveLimitLog(boolean modify, ReservationRateLimit log, Integer next) {
        ReservationLimitLog limitLog;
        if (modify) {
            limitLog = new ReservationLimitLog();
            limitLog.setResConfigId(log.getId());
            limitLog.setOperate((byte)1);
            limitLog.setOperatePre(next);
            limitLog.setOperateNext(log.getConfigLimit());
        } else {
            limitLog = new ReservationLimitLog();
            limitLog.setOperate((byte)0);
            limitLog.setResConfigId(log.getId());
            limitLog.setOperatePre(log.getConfigLimit());
        }
        mapper.insertSelective(limitLog);
    }

}
