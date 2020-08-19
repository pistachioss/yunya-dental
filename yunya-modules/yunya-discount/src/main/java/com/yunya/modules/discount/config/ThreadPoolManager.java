package com.yunya.modules.discount.config;

import lombok.extern.slf4j.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * @author xiangyang
 * @date 2020/8/19
 */
@Slf4j
public class ThreadPoolManager {

    /**
     * 根据cpu的数量动态的配置核心线程数和最大线程数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 核心线程数 = CPU核心数 + 1
     */
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    /**
     * 线程池最大线程数 = CPU核心数 * 2 + 1
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * 非核心线程闲置时超时1s
     */
    private static final int KEEP_ALIVE = 1;
    /**
     * 阻塞队列size
     */
    private static final int queueSize = 20;
    /**
     * 线程池的对象
     */
    private ThreadPoolExecutor executor;
    /**
     * 线程名称
     */
    private static final String namePrefix = "yunya-thread-";
    /**
     * 线程尾部id
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private ThreadPoolManager() {
    }

    private static ThreadPoolManager sInstance;

    public synchronized static ThreadPoolManager getsInstance() {
        if (sInstance == null) {
            sInstance = new ThreadPoolManager();
        }
        return sInstance;
    }

    /**
     * 开启一个无返回结果的线程
     *
     * @param r r
     */
    public void execute(Runnable r) {
        if (executor == null) {
            executor = getExecutor();
        }
        // 把一个任务丢到了线程池中
        executor.execute(r);
    }

    /**
     * 开启一个有返回结果的线程
     *
     * @param r r
     * @return Future
     */
    public <T extends Object> Future<T> submit(Callable<T> r) {
        if (executor == null) {
            executor = getExecutor();
        }
        // 把一个任务丢到了线程池中
        return executor.submit(r);
    }

    private ThreadPoolExecutor getExecutor() {
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
                KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueSize),
                customThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
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

    private ThreadFactory customThreadFactory() {
        return (r) -> {
            Thread t = new Thread(null, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            //守护线程
            if (t.isDaemon())
                t.setDaemon(true);
            //线程优先级
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);

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

    public void shutdown(long timeout, TimeUnit timeUnit) {
        try {
            executor.awaitTermination(timeout, timeUnit);
            executor.shutdown();
        } catch (InterruptedException e) {
            log.warn("线程关闭异常");
        }
    }
}