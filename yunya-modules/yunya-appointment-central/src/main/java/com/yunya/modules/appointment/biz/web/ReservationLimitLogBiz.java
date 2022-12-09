package com.yunya.modules.appointment.biz.web;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.appointment.ReservationLimitLog;
import com.yunya.models.appointment.ReservationRateLimit;
import com.yunya.modules.appointment.mapper.ReservationLimitLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReservationLimitLogBiz extends BaseBiz<ReservationLimitLogMapper, ReservationLimitLog> {

    public void saveLimitLog(boolean modify, List<ReservationRateLimit> limits, Map<Integer, Integer> existLimit) {
        List<ReservationLimitLog> build = build(modify, limits, existLimit);
        mapper.insertList(build);
    }

    public List<ReservationLimitLog> build(boolean modify, List<ReservationRateLimit> limits,  Map<Integer, Integer> existLimit){
        return limits.parallelStream().map(t -> {
            ReservationLimitLog limitLog;
            if (modify) {
                limitLog = new ReservationLimitLog();
                limitLog.setResConfigId(t.getId());
                limitLog.setOperate((byte)1);
                limitLog.setOperatePre(existLimit.get(t.getId()));
                limitLog.setOperateNext(t.getConfigLimit());
                limitLog.setCrtId(t.getUpdId());
            } else {
                limitLog = new ReservationLimitLog();
                limitLog.setOperate((byte)0);
                limitLog.setResConfigId(t.getId());
                limitLog.setOperatePre(t.getConfigLimit());
                limitLog.setCrtId(t.getCrtId());
            }
            return limitLog;
        }).collect(Collectors.toList());
    }
}
