package com.yunya.modules.emr.interceptor;

import com.yunya.framework.common.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Web配置信息
 *
 * @author ace
 * @date 2017/9/8
 */
@Primary
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  @Resource
  private WebHandlertInterceptor webHandlertInterceptor;

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
    registry.addInterceptor(webHandlertInterceptor).addPathPatterns(getIncludePathPatterns());
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
      "/medical/**"
    };
    Collections.addAll(list, urls);
    return list;
  }
}
