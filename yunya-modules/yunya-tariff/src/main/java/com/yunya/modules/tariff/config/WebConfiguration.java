package com.yunya.modules.tariff.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.yunya.framework.common.handler.GlobalExceptionHandler;
import com.yunya.modules.tariff.config.CurrentUserInfoRestInterceptor;
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

  @Bean
  GlobalExceptionHandler getGlobalExceptionHandler() {
    return new GlobalExceptionHandler();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(getUserAuthRestInterceptor()).addPathPatterns("/**");
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    //创建fastJson消息转换器
    List<MediaType> supportedMediaTypes = getSupportedMediaTypes();

    // 调用父类的配置
    WebMvcConfigurer.super.configureMessageConverters(converters);
    // 创建fastJson消息转化器
    FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
    // 创建fastJson的配置对象
    FastJsonConfig config = new FastJsonConfig();
    config.setSerializerFeatures(
            SerializerFeature.PrettyFormat,
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteMapNullValue,
            // 禁止循环引用
            SerializerFeature.DisableCircularReferenceDetect);
    config.setCharset(Charset.forName("UTF-8"));
    converter.setFastJsonConfig(config);
    converter.setSupportedMediaTypes(supportedMediaTypes);
    converters.add(converter);
  }

  /**
   * 消息转换器
   * @return
   */
  public List<MediaType> getSupportedMediaTypes() {
    //创建fastJson消息转换器
    List<MediaType> supportedMediaTypes = new ArrayList<>();
    supportedMediaTypes.add(MediaType.APPLICATION_JSON);
    supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
    supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
    supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
    supportedMediaTypes.add(MediaType.APPLICATION_PDF);
    supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
    supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
    supportedMediaTypes.add(MediaType.APPLICATION_XML);
    supportedMediaTypes.add(MediaType.IMAGE_GIF);
    supportedMediaTypes.add(MediaType.IMAGE_JPEG);
    supportedMediaTypes.add(MediaType.IMAGE_PNG);
    supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
    supportedMediaTypes.add(MediaType.TEXT_HTML);
    supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
    supportedMediaTypes.add(MediaType.TEXT_PLAIN);
    supportedMediaTypes.add(MediaType.TEXT_XML);
    supportedMediaTypes.add(MediaType.ALL);
    return supportedMediaTypes;
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
