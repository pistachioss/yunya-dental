package com.yunya.modules.appointment.biz.web;

import com.google.common.collect.Lists;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.yunya.modules.appointment.code.AppointmentError.CONFIG_DATE_ERROR;
import static com.yunya.modules.appointment.code.AppointmentError.CONFIG_DATE_REPEAT;
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
        String orgName = model.getOrgName();
        List<ReservationLimitDetailModel> details = model.getDetails();
        List<LocalDate> configDate = details.stream()
                .map(ReservationLimitDetailModel::getConfigDate).filter(Objects::nonNull).collect(toList());
        boolean anyMatch = configDate.stream().anyMatch(t -> t.isBefore(LocalDate.now()));
        if (anyMatch) {
            throw ClientServiceException.wrap(CONFIG_DATE_ERROR);
        }
        //查询已存在流量
        List<ReservationRateLimit> existData = existData(orgName, configDate);
        //校验日期是否存在
        Optional<LocalDate> checkMonthRepeat = checkMonthRepeat(details, existData);
        if (checkMonthRepeat.isPresent()) {
            throw ClientServiceException.wrap(CONFIG_DATE_REPEAT, orgName, checkMonthRepeat.get());
        }
        Map<Integer, Integer> existLimit = existData.stream().collect(toMap(ReservationRateLimit::getId, ReservationRateLimit::getConfigLimit));
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        List<CompletableFuture<Void>> insert = details.stream().filter(t -> Objects.isNull(t.getId())).map(t -> CompletableFuture.runAsync(() -> {
            ReservationRateLimit limit = new ReservationRateLimit();
            limit.setConfigDate(DateUtil.localDateToDate(t.getConfigDate()));
            limit.setConfigLimit(t.getConfigLimit());
//            limit.setOrgId(model.getOrgId());
            limit.setOrgName(orgName);
            limit.setCrtId(userId);
            limit.setUpdId(userId);
            mapper.insertSelective(limit);
            limitLogBiz.saveLimitLog(false, limit, null);
            log.info("新增登记流量完成");
        }, executorService).exceptionally(e -> {
            throw new RuntimeException(e);
        })).collect(toList());
        List<CompletableFuture<Void>> update = details.stream().filter(t -> Objects.nonNull(t.getId())).map(t -> CompletableFuture.runAsync(() -> {
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
        String orgName = query.getOrgName();
        LocalDate configDate = query.getConfigDate();
        //已提交预约
        List<Reservation> submittedLimit = reservationBiz.submittedLimit(orgName, null, true, configDate);
        Map<Integer, Long> limitMap = submittedLimit.stream().collect(groupingBy(Reservation::getReservationLimitId, counting()));
        //剩余库存
        List<ReservationRateLimit> remaining = mapper.listRemaining(orgName, configDate);
        ReservationLimitVO result = new ReservationLimitVO();
        result.setDetails(remaining.stream()
                .map(t -> {
                    ReservationLimitDetailVO vo = BeanCopierUtils.generalCopyBean(t, ReservationLimitDetailVO.class);
                    Long submit = limitMap.get(t.getId());
                    vo.setSubmitLimit(Objects.isNull(submit) ? 0 : submit);
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

    private Optional<LocalDate> checkMonthRepeat(List<ReservationLimitDetailModel> details, List<ReservationRateLimit> existData) {
        List<LocalDate> configDate = details.stream().filter(t -> Objects.isNull(t.getId()))
                .map(ReservationLimitDetailModel::getConfigDate).collect(toList());
        Set<Date> dates = existData.stream().map(ReservationRateLimit::getConfigDate).collect(toSet());
        return configDate.stream()
                .filter(t -> dates.contains(DateUtil.localDateToDate(t)))
                .findFirst();
    }

    private List<ReservationRateLimit> existData(String orgName, List<LocalDate> configDate) {
        if (CollectionUtils.isNotEmpty(configDate)) {
            Example example = new Example(ReservationRateLimit.class);
            example.createCriteria().andEqualTo("orgName", orgName).andIn("configDate", configDate);
            return mapper.selectByExample(example);
        }
        return Lists.newArrayList();
    }
}
