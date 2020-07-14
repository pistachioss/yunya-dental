package com.yunya.auth.configuration;

import com.yunya.auth.interceptor.UserAuthRestInterceptor;
import com.yunya.framework.common.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Web配置信息
 *
 * @author ace
 * @date 2017/9/8
 */
@Primary
@Configuration("adminWebConfig")
public class WebConfiguration implements WebMvcConfigurer {

  /**
   * 注入用户权限认证拦截器
   *
   * @return
   */
  @Bean
  UserAuthRestInterceptor getUserAuthRestInterceptor() {
    return new UserAuthRestInterceptor();
  }

  /**
   * 注入全局异常处理类
   *
   * @return
   */
  @Bean
  GlobalExceptionHandler getGlobalExceptionHandler() {
    return new GlobalExceptionHandler();
  }

  /**
   * 添加拦截器
   *
   * @param registry 拦截器注册表
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(getUserAuthRestInterceptor()).addPathPatterns(getIncludePathPatterns());
  }

  // todo 调整需要鉴权的路径
  /**
   * 需要用户和服务认证判断的路径
   *
   * @return
   */
  private ArrayList<String> getIncludePathPatterns() {
    ArrayList<String> list = new ArrayList<>();
    String[] urls = {
      "/element/**",
      "/gateLog/**",
      "/group/**",
      "/groupType/**",
      "/menu/**",
      "/user/**",
      "/api/permissions",
      "/api/user/un/**",
      "/service/**"
    };
    Collections.addAll(list, urls);
    return list;
  }
}
