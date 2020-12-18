package com.yunya.modules.sms.config;

import com.yunya.framework.common.handler.GlobalExceptionHandler;
import com.yunya.framework.common.interceptor.RepeatSubmitInterceptor;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 配置请求需要拦截的路径
 *
 * @author chow
 */
@Configuration("systemWebConfig")
@Primary
public class WebConfiguration implements WebMvcConfigurer {
  /** 核心线程数 */
  private static final int CORE_POOL_SIZE = 1;

  @Autowired private RepeatSubmitInterceptor repeatSubmitInterceptor;

  @Bean
  GlobalExceptionHandler getGlobalExceptionHandler() {
    return new GlobalExceptionHandler();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(getUserAuthRestInterceptor()).addPathPatterns("/**");
    registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
  }

  @Bean
  public ScheduledExecutorService getScheduledExecutorService() throws Exception {
    return new ScheduledThreadPoolExecutor(CORE_POOL_SIZE,
            new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
  }

  /**
   * 获取当前用户信息拦截器
   *
   * @return
   */
  @Bean
  CurrentUserInfoRestInterceptor getUserAuthRestInterceptor() {
    return new CurrentUserInfoRestInterceptor();
  }
}
