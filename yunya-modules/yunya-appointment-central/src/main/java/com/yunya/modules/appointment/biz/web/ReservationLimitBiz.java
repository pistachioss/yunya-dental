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
import com.yunya.modules.appointment.config.SelfTransactionManager;
import com.yunya.modules.appointment.config.ThreadPoolManagerConfig;
import com.yunya.modules.appointment.mapper.ReservationRateLimitMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
    @Resource(name = "syncExecutor")
    private ExecutorService executorService;
    @Resource
    private SelfTransactionManager selfTransactionManager;
    @Resource
    private PlatformTransactionManager transactionManager;

    //    @Transactional(rollbackFor = Exception.class)
    public void modify(ReservationLimitModel model) {
        List<ReservationLimitDetailModel> detail = model.getDetails();
        final AtomicBoolean flag = new AtomicBoolean(true);
        AtomicBoolean checkFlag = new AtomicBoolean(true);
        CountDownLatch mainDownLatch = new CountDownLatch(1);
        AtomicReference<Exception> ex = new AtomicReference<>();
        String orgName = model.getOrgName();
        List<LocalDate> configDate = detail.stream()
                .map(ReservationLimitDetailModel::getConfigDate).filter(Objects::nonNull).collect(toList());
        boolean anyMatch = configDate.stream().anyMatch(t -> t.isBefore(LocalDate.now()));
        if (anyMatch) {
            throw ClientServiceException.wrap(CONFIG_DATE_ERROR);
        }
        //查询已存在流量
        List<ReservationRateLimit> existData = existData(orgName, configDate);
        //校验日期是否存在
        Optional<LocalDate> checkMonthRepeat = checkMonthRepeat(detail, existData);
        if (checkMonthRepeat.isPresent()) {
            throw ClientServiceException.wrap(CONFIG_DATE_REPEAT, orgName, checkMonthRepeat.get());
        }
        Map<Integer, Integer> existLimit = existData.stream().collect(toMap(ReservationRateLimit::getId, ReservationRateLimit::getConfigLimit, (o, v) -> o));
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        List<List<ReservationLimitDetailModel>> partition = Lists.partition(detail, (detail.size() / ThreadPoolManagerConfig.CORE_POOL_SIZE) + 1);
        log.info("开始执行，需执行的任务数：{}", detail.size());
        CountDownLatch childDownLatch = new CountDownLatch(partition.size());
        for (List<ReservationLimitDetailModel> detailList : partition) {
            CompletableFuture.supplyAsync(() -> detailList.parallelStream().filter(t -> Objects.isNull(t.getId())).collect(toList()), executorService)
                    .thenAcceptAsync((details) -> {
                        if (CollectionUtils.isEmpty(details)) {
                            return;
                        }
                        TransactionStatus transactionStatus = null;
                        try {
                            log.info("该新增批次执行数:{}, 执行线程:{}", detailList.size(), Thread.currentThread().getName());
                            transactionStatus = selfTransactionManager.begin();
                            List<ReservationRateLimit> insertList = insertList(details, userId, orgName);
                            mapper.insertList(insertList);
                            limitLogBiz.saveLimitLog(false, insertList, null);
                            childDownLatch.countDown();
                            mainDownLatch.await();
                            if (flag.get()) {
                                log.info("所有新增任务正常完成,线程{}开始提交事务", Thread.currentThread().getName());
                                selfTransactionManager.commit(transactionStatus);
                            } else {
                                log.info("有其他任务出现异常, 新增业务开始回滚事务,线程{}", Thread.currentThread().getName());
                                selfTransactionManager.rollBack(transactionStatus);
                            }
                        } catch (Exception e) {
                            checkFlag.set(false);
                            childDownLatch.countDown();
                            ex.set(e);
                            Objects.requireNonNull(log).info("线程{}新增任务出现异常,开始回滚事务", Thread.currentThread().getName(), e);
                            selfTransactionManager.rollBack(transactionStatus);
                        }
                    }, executorService);
            CompletableFuture.supplyAsync(() -> detailList.parallelStream().filter(t -> Objects.nonNull(t.getId())).collect(toList()), executorService)
                    .thenAcceptAsync((details) -> {
                        if (CollectionUtils.isEmpty(details)) {
                            return;
                        }
                        TransactionStatus transactionStatus = null;
                        try {
                            log.info("该更新批次执行数:{}, 执行线程:{}", detailList.size(), Thread.currentThread().getName());
                            transactionStatus = selfTransactionManager.begin();
                            List<ReservationRateLimit> updateList = updateList(detailList, userId);
                            mapper.updateList(updateList);
                            limitLogBiz.saveLimitLog(true, updateList, existLimit);
                            childDownLatch.countDown();
                            mainDownLatch.await();
                            if (flag.get()) {
                                log.info("所有更新任务正常完成,线程{}开始提交事务", Thread.currentThread().getName());
                                transactionManager.commit(transactionStatus);
                            } else {
                                log.info("有其他任务出现异常, 更新业务开始回滚事务,线程{}", Thread.currentThread().getName());
                                selfTransactionManager.rollBack(transactionStatus);
                            }
                        } catch (Exception e) {
                            checkFlag.set(false);
                            childDownLatch.countDown();
                            ex.set(e);
                            log.info("线程{}更新任务出现异常,开始回滚事务", Thread.currentThread().getName(), e);
                            selfTransactionManager.rollBack(transactionStatus);
                        }
                    }, executorService);
        }
        try {
            childDownLatch.await();
            if (!checkFlag.get()) {
                flag.compareAndSet(true, false);
                log.info("flag标志为更改为false，开始回滚");
                throw ex.get();
            }
        } catch (Exception e) {
            throw new ClientServiceException(e);
        } finally {
            mainDownLatch.countDown();
        }
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

    private List<ReservationRateLimit> insertList(List<ReservationLimitDetailModel> details, Integer userId, String orgName) {
        return details.stream().map(t -> {
            ReservationRateLimit limit = new ReservationRateLimit();
            limit.setConfigDate(DateUtil.localDateToDate(t.getConfigDate()));
            limit.setConfigLimit(t.getConfigLimit());
//            limit.setOrgId(model.getOrgId());
            limit.setOrgName(orgName);
            limit.setCrtId(userId);
            limit.setUpdId(userId);
            return limit;
        }).collect(toList());
    }

    private List<ReservationRateLimit> updateList(List<ReservationLimitDetailModel> details, Integer userId) {
        return details.stream().map(t -> {
            ReservationRateLimit limit = new ReservationRateLimit();
            limit.setId(t.getId());
            limit.setConfigDate(DateUtil.localDateToDate(t.getConfigDate()));
            limit.setConfigLimit(t.getConfigLimit());
            limit.setUpdId(userId);
            return limit;
        }).collect(toList());
    }
}

