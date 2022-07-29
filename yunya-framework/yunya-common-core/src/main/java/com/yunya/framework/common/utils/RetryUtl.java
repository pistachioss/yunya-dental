package com.yunya.framework.common.utils;

import com.github.rholder.retry.*;
import com.google.common.base.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 简介：重试器
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/6/24 17:34
 * @since: 1.0.0
 */
@Slf4j
public class RetryUtl {

    /**
     * 重试：固定间隔时长，重试固定次数
     * @param condition
     * @param task
     * @param sleepTime
     * @param retryTimes
     * @param <V>
     * @return
     */
    public static <V> Optional<V> retry(Predicate<V> condition, Callable<V> task, int sleepTime, int retryTimes) {
        return retry(condition, task, WaitStrategies.fixedWait(sleepTime, TimeUnit.SECONDS), retryTimes);
    }

    /**
     * 每次尝试失败后添加到上一个睡眠时间的增量
     *
     * @param condition
     * @param task
     * @param sleepTime 第一次重试之前的睡眠时间
     * @param incrTime 每次尝试失败后添加到上一个睡眠时间的增量
     * @param retryTimes
     * @param <V>
     * @return
     */
    public static <V> Optional<V> retryIncrWait(Predicate<V> condition, Callable<V> task, int sleepTime, int incrTime, int retryTimes) {
        return retry(condition, task, WaitStrategies.incrementingWait(sleepTime,TimeUnit.SECONDS,incrTime,TimeUnit.SECONDS), retryTimes);
    }

    /**
     * 根据输入的condition重复做task,在规定的次数内达到condition则返回,
     * 如果超过retryTimes则返回null, 重试次数,整个重试时间以及retry exception都会记录log
     *
     * @param condition  重试条件,比如接口返回errorCode为处理中,或不是最终需要的结果
     * @param task       重试做的任务
     * @param waitStrategy  重试等待策略
     * @param retryTimes 重试次数
     * @return targetBean
     */
    public static <V> Optional<V> retry(Predicate<V> condition, Callable<V> task, WaitStrategy waitStrategy, int retryTimes) {
        Optional<V> result = Optional.empty();
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            Retryer<V> retry = RetryerBuilder.<V>newBuilder()
                    // 默认任务执行过程中发生异常自动重试
                    .retryIfException()
                    // 重试条件（按照业务场景）
                    .retryIfResult(condition)
                    // 等待策略
                    .withWaitStrategy(waitStrategy)
                    // 重试策略
                    .withStopStrategy(StopStrategies.stopAfterAttempt(retryTimes))
                    // 重试监听器
                    .withRetryListener(new RetryListener() {
                        @Override
                        public <V> void onRetry(Attempt<V> attempt) {
                            // 记录重试次数和异常信息
                            log.info(MessageFormat.format("{0}th retry", attempt.getAttemptNumber()));
                            if (attempt.hasException()) {
                                log.error(MessageFormat.format("retry exception:{0}", attempt.getExceptionCause()));
                            }
                        }
                    }).build();

            // 开始执行重试任务
            result = Optional.ofNullable(retry.call(task));
        } catch (Exception e) {
            log.error("retry fail:", e.getMessage());
        } finally {
            stopWatch.stop();
            log.info("retry execute time", stopWatch.getTime());
        }
        return result;
    }
}
