package com.yunya.report.ultimate.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiangyang
 * @date 2020/8/19
 */
@Slf4j
@Component
public class ThreadPoolManager {

    /**
     * 根据cpu的数量动态的配置核心线程数和最大线程数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 核心线程数 = CPU核心数 + 1
     */
    private static final int CORE_POOL_SIZE = CPU_COUNT;
    /**
     * 线程池最大线程数 = CPU核心数 * 2 + 1
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * 非核心线程闲置时超时1s
     */
    private static final int KEEP_ALIVE = 1;
    /**
     * 线程池的对象
     */
    private ThreadPoolExecutor executor;
    /**
     * 线程名称
     */
    private static final String NAME_PREFIX = "yunya-report-thread-";
    /**
     * 线程尾部id
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * 缓冲队列，存储阻塞的任务
     */
    public static final Queue<Runnable> blockQueue = new LinkedBlockingQueue<>();
    /**
     * 线程池的定时任务
     */
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(CPU_COUNT / 2);

    private ThreadPoolManager() {
    }

    @Bean(value = "customizeThreadPool")
    public ThreadPoolExecutor getExecutor() {
        /*
         * corePoolSize:核心线程数
         * maximumPoolSize：线程池所容纳最大线程数(workQueue队列满了之后才开启)
         * keepAliveTime：非核心线程闲置时间超时时长
         * unit：keepAliveTime的单位
         * workQueue：等待队列，存储还未执行的任务
         * threadFactory：线程创建的工厂
         * handler：异常处理机制
         *
         */
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
                customizeThreadFactory(), customizeRejectHandler());
    }

    /**
     * 把任务移除等待队列
     *
     * @param r r
     */
    public void cancel(Runnable r) {
        if (r != null) {
            executor.getQueue().remove(r);
        }
    }

    private ThreadFactory customizeThreadFactory() {
        return (r) -> {
            Thread t = new Thread(null, r,
                    NAME_PREFIX + threadNumber.getAndIncrement(),
                    0);
            log.info("线程名称：{}", t.getName());
            //守护线程
            if (t.isDaemon()) {
                t.setDaemon(true);
            }
            //线程优先级
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }

            //处理未捕捉的异常
            t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    log.warn("线程工厂创建异常");
                }
            });
            return t;
        };
    }

    private RejectedExecutionHandler customizeRejectHandler() {
        /**
         * 线程池满了，加入到队列里定时执行
         */
        return (r, executor) -> {
            log.info("线程池已满，任务加入到缓冲队列");
            blockQueue.offer(r);
        };
    }

}