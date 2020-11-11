package com.yunya.modules.appointment.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.yunya.framework.common.handler.GlobalExceptionHandler;
import com.yunya.framework.common.interceptor.RepeatSubmitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.swing.*;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置请求需要拦截的路径
 *
 * @author chow
 */
@Configuration("systemWebConfig")
@Primary
public class WebConfiguration implements WebMvcConfigurer {

  @Autowired
  RepeatSubmitInterceptor repeatSubmitInterceptor;

  @Bean
  GlobalExceptionHandler getGlobalExceptionHandler() {
    return new GlobalExceptionHandler();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(getUserAuthRestInterceptor()).addPathPatterns("/**");
    registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    // 调用父类的配置
    WebMvcConfigurer.super.configureMessageConverters(converters);
    // 创建fastJson消息转化器
    FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
    List<MediaType> supportedMediaTypes = new ArrayList<>();
    supportedMediaTypes.add(MediaType.ALL);
    converter.setSupportedMediaTypes(supportedMediaTypes);
    converters.add(converter);
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
