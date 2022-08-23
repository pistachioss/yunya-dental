package com.yunya365.mini.config;

import com.yunya.framework.common.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置请求需要拦截的路径
 *
 * @author chow
 */
@Configuration
@Primary
public class WebConfiguration implements WebMvcConfigurer {



  @Bean
  GlobalExceptionHandler getGlobalExceptionHandler() {
    return new GlobalExceptionHandler();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(getUserAuthRestInterceptor())
            .addPathPatterns("/**").excludePathPatterns("/**/login");

  }

  /**
   * 获取当前用户信息拦截器
   *
   * @return
   */
  @Bean
  JwtInterceptor getUserAuthRestInterceptor() {
    return new JwtInterceptor();
  }
}
