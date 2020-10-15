package com.yunya.modules.appointment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: yunya-dental
 * @description: 线程管理
 * @author: LHB
 * @create: 2020-10-13 16:23
 **/
@Slf4j
@Configuration
@EnableAsync
public class ThreadPoolManagerConfig {
    /** 根据CPU数量动态配置核心线程数和最大线程数 */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /** 核心线程数 =  CPU_COUNT + 1 */
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    /** 最大线程数 =  CPU_COUNT * 2 + 1 */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /** 闲置线程存活时间 1s */
    private static final int KEEP_ALIVE = 1;
    /** 线程名称 */
    private static final String NAME_PREFIX = "yunya-appoint-";
    /** 线程尾部ID */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /** 缓冲队列 */
    private static final BlockingQueue<Runnable> blockQueue = new LinkedBlockingQueue<>();
    /** 缓冲队列大小 = (CORE_POOL_SIZE / 每个任务花费时间) * 系统允许容忍的最大响应时间 */
    private static final int QUEUE_CAPACITY = (int) (CORE_POOL_SIZE / 0.1 * 5);

    @Bean("customizeExecutor")
    public ThreadPoolTaskExecutor customizeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAXIMUM_POOL_SIZE);
        executor.setKeepAliveSeconds(KEEP_ALIVE);
        executor.setThreadFactory(customizeThreadFactory());
        executor.setRejectedExecutionHandler(customizeRejectedHandler());
        executor.setQueueCapacity(QUEUE_CAPACITY);
        return executor;
    }

    /**
     * 自定义线程工厂
     * @return 线程工厂实例
     */
    private ThreadFactory customizeThreadFactory() {
        return (r) -> {
            Thread t = new Thread(null,r,NAME_PREFIX + threadNumber.getAndIncrement(),0);
            // 守护线程
            if (t.isDaemon()) {
                t.setDaemon(true);
            }
            // 线程优先级
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            // 处理未捕获的异常
            t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    log.error("线程创建异常");
                }
            });
            return t;
        };
    }

    /**
     * 自定义拒绝处理
     * @return
     */
    private RejectedExecutionHandler customizeRejectedHandler() {
        return (r,executor) ->{
          log.info("线程池已满，任务加入到缓冲队列");
          blockQueue.offer(r);
        };
    }
}
