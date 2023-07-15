package com.yunya.modules.treatment.config;

import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
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
    public static final int CORE_POOL_SIZE = CPU_COUNT;
    /**
     * 线程池最大线程数 = CPU核心数 * 2 + 1
     */
    public static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * 非核心线程闲置时超时1s
     */
    private static final int KEEP_ALIVE = 1;
    /**
     * 队列容量
     */
    private static final int QUEUE_CAPACITY_SIZE = CPU_COUNT * 2 * 10;
    /**
     * 线程池的对象
     */
    private ThreadPoolExecutor executor;
    /**
     * 线程名称
     */
    private static final String NAME_PREFIX = "yunya-thread-";
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

    @Bean(value = "treatmentThreadPool")
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

    @Bean("asyncExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAXIMUM_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY_SIZE);
        executor.setThreadNamePrefix("async-exec-");
        // 拒绝策略：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(KEEP_ALIVE);
        // 当调度器shutdown被调用时等待当前被调度的任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setTaskDecorator(runnable -> {
            try {
                // 将主线程中的token和线程局部变量，传递到异步线程中
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader(CommonConstants.TOKEN_HEADER);
                Map<String, Object> threadLocal = BaseContextHandler.threadLocal.get();
                return () -> {
                    try {
                        attributes.setAttribute(CommonConstants.TOKEN_HEADER, token, RequestAttributes.SCOPE_SESSION);
                        RequestContextHolder.setRequestAttributes(attributes);
                        BaseContextHandler.threadLocal.set(threadLocal);
                        runnable.run();
                    } finally {
                        RequestContextHolder.resetRequestAttributes();
                    }
                };
            } catch (IllegalStateException e) {
                return runnable;
            }
        });
        executor.initialize();
        return executor;
    }

    /**
     * 自定义异步任务执行器，在@Async标注方法执行时，将主线程中的请求上下文和登录信息，传递到到@Async执行线程
     */
    class ContextAwarePoolExecutor extends ThreadPoolTaskExecutor {

        class ContextAwareCallable<T> implements Callable<T> {
            /** 任务 */
            private Callable<T> task;
            /** 请求属性 */
            private RequestAttributes context;
            /** 主线程的局部变量 */
            private Map<String, Object> masterThreadLocal;

            public ContextAwareCallable(Callable<T> task, RequestAttributes context, Map<String, Object> masterThreadLocal) {
                this.task = task;
                this.context = context;
                this.masterThreadLocal = masterThreadLocal;
            }

            @Override
            public T call() throws Exception {
                if (StringHelper.isNotNull(context)) {
                    RequestContextHolder.setRequestAttributes(context);
                }
                if (StringHelper.isNotNull(masterThreadLocal)) {
                    BaseContextHandler.threadLocal.set(masterThreadLocal);
                }
                try {
                    return task.call();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            }
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return super.submit(new ContextAwareCallable(task, RequestContextHolder.currentRequestAttributes(), BaseContextHandler.threadLocal.get()));
        }

        @Override
        public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
            return super.submitListenable(new ContextAwareCallable(task, RequestContextHolder.currentRequestAttributes(), BaseContextHandler.threadLocal.get()));
        }
    }
}