package com.yunya.modules.sms.config;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * 配置请求需要拦截的路径
 *
 * @author chow
 */
@Configuration("scheduleExecutorConfig")
@Primary
public class ScheduleExecutorConfiguration implements WebMvcConfigurer {
  /** 核心线程数 */
  private static final int CORE_POOL_SIZE = 1;
  private static final String YUNYA_SMS_SCHEDULEPOOL = "yunya-sms-schedule-pool";


  @Bean
  public ThreadFactory threadFactory() {
    return new BasicThreadFactory.Builder()
            .namingPattern(YUNYA_SMS_SCHEDULEPOOL + "-%d")
            .daemon(true).build();
  }

  @Bean
  public ScheduledExecutorService scheduledExecutorService() throws Exception {
    return new ScheduledThreadPoolExecutor(CORE_POOL_SIZE, threadFactory());
  }
}
