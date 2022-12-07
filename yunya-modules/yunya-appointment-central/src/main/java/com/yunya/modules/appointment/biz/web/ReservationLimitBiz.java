package com.yunya.modules.appointment.biz.web;

import com.google.common.collect.Maps;
import com.yunya.feign.appointment.domain.model.ReservationLimitDetailModel;
import com.yunya.feign.appointment.domain.model.ReservationLimitModel;
import com.yunya.feign.appointment.domain.query.ReservationLimitQuery;
import com.yunya.feign.appointment.vo.ReservationLimitDetailVO;
import com.yunya.feign.appointment.vo.ReservationLimitVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.models.appointment.Reservation;
import com.yunya.models.appointment.ReservationRateLimit;
import com.yunya.modules.appointment.biz.app.ReservationBiz;
import com.yunya.modules.appointment.mapper.ReservationRateLimitMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.yunya.modules.appointment.code.AppointmentError.CONFIG_DATE_ERROR;
import static java.util.stream.Collectors.*;

@Service
@Slf4j
public class ReservationLimitBiz extends BaseBiz<ReservationRateLimitMapper, ReservationRateLimit> {

    @Resource
    private ReservationBiz reservationBiz;
    @Resource
    private ReservationLimitLogBiz limitLogBiz;
    @Resource(name = "poolExecutor")
    private ExecutorService executorService;

    @Transactional(rollbackFor = Exception.class)
    public void modify(ReservationLimitModel model) {
        List<ReservationLimitDetailModel> details = model.getDetails();
        List<LocalDate> configDate = details.parallelStream().map(ReservationLimitDetailModel::getConfigDate).collect(toList());
        boolean anyMatch = configDate.parallelStream().anyMatch(t -> t.isBefore(LocalDate.now()));
        if (anyMatch) {
            throw ClientServiceException.wrap(CONFIG_DATE_ERROR);
        }
        //查询已存在流量
        Map<Integer, Integer> existLimit = existLimit(details);
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        List<CompletableFuture<Void>> insert = details.stream().filter(t -> Objects.isNull(t.getId())).map(t -> CompletableFuture.runAsync(() -> {
            ReservationRateLimit limit = new ReservationRateLimit();
            limit.setConfigDate(DateUtil.localDateToDate(t.getConfigDate()));
            limit.setConfigLimit(t.getConfigLimit());
            limit.setOrgId(model.getOrgId());
            limit.setCrtId(userId);
            limit.setUpdId(userId);
            mapper.insertSelective(limit);
            limitLogBiz.saveLimitLog(false, limit, null);
            log.info("新增登记流量完成");
        }, executorService).exceptionally(e -> {
            throw new RuntimeException(e);
        })).collect(toList());
        List<CompletableFuture<Void>> update = details.parallelStream().filter(t -> Objects.nonNull(t.getId())).map(t -> CompletableFuture.runAsync(() -> {
            ReservationRateLimit limit = new ReservationRateLimit();
            limit.setId(t.getId());
            limit.setConfigDate(DateUtil.localDateToDate(t.getConfigDate()));
            limit.setConfigLimit(t.getConfigLimit());
            limit.setUpdId(userId);
            mapper.updateByPrimaryKeySelective(limit);
            limitLogBiz.saveLimitLog(true, limit, existLimit.get(t.getId()));
            log.info("更新登记流量完成id:{}, 更新数:{}", t.getId(), t.getConfigLimit());
        }, executorService).exceptionally(e -> {
            throw new RuntimeException(e);
        })).collect(toList());
        update.addAll(insert);
        log.info("需执行的任务数：{}", update.size());
        CompletableFuture.allOf(update.toArray(new CompletableFuture[0]))
                .whenComplete((r, e) -> {
                    if (e == null) {
                        log.info("预约登记流量更新完成");
                    } else {
                        throw new ClientServiceException(e);
                    }
                }).join();
    }

    public ReservationLimitVO list(ReservationLimitQuery query) throws ParseException {
        Integer orgId = query.getOrgId();
        Date configDate = query.getConfigDate();
        //已提交预约
        List<Reservation> submittedLimit = reservationBiz.submittedLimit(query.getOrgId(), null, true, configDate);
        Map<Integer, Long> limitMap = submittedLimit.stream().collect(groupingBy(Reservation::getReservationLimitId, counting()));
        //剩余库存
        Example example = new Example(ReservationRateLimit.class);
        List<ReservationRateLimit> remaining = mapper.listRemaining(orgId, configDate);
        ReservationLimitVO result = new ReservationLimitVO();
        result.setDetails(remaining.stream()
                .map(t -> {
                    ReservationLimitDetailVO vo = BeanCopierUtils.generalCopyBean(t, ReservationLimitDetailVO.class);
                    vo.setSubmitLimit(limitMap.get(t.getId()));
                    return vo;
                }).collect(toList()));
        return result;
    }

    /**
     * 尝试获取剩余数
     *
     * @param reservationLimitId id
     */
    public boolean tryAcquire(Integer reservationLimitId) {
        ReservationRateLimit rateLimit = mapper.selectByPrimaryKey(reservationLimitId);
        if (Objects.isNull(rateLimit) || rateLimit.getConfigLimit() <= 0) {
            return false;
        }
        int count = mapper.reduceLimit(reservationLimitId);
        return count == 1;
    }

    private Map<Integer, Integer> existLimit(List<ReservationLimitDetailModel> details) {
        List<Integer> limitIds = details.stream().map(ReservationLimitDetailModel::getId).filter(Objects::nonNull).collect(toList());
        if (CollectionUtils.isNotEmpty(limitIds)) {
            Example example = new Example(ReservationRateLimit.class);
            example.createCriteria().andIn("id", limitIds);
            List<ReservationRateLimit> list = mapper.selectByExample(example);
            return list.stream().collect(toMap(ReservationRateLimit::getId, ReservationRateLimit::getConfigLimit));
        }
        return Maps.newHashMap();
    }
}
