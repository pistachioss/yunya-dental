package com.yunya.modules.patient_central.config;

import cn.hutool.extra.qrcode.QrConfig;
import com.yunya.framework.common.handler.GlobalExceptionHandler;
import com.yunya.framework.common.interceptor.RepeatSubmitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.awt.*;

/**
 * 配置请求需要拦截的路径
 *
 * @author chow
 */
@Configuration("patientWebConfig")
@Primary
public class WebConfiguration implements WebMvcConfigurer {

  @Bean
  GlobalExceptionHandler getGlobalExceptionHandler() {
    return new GlobalExceptionHandler();
  }

  @Autowired private RepeatSubmitInterceptor repeatSubmitInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(getUserAuthRestInterceptor()).addPathPatterns("/**");
    registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
  }

  /**
   * 获取当前用户信息拦截器
   *
   * @return CurrentUserInfoRestInterceptor
   */
  @Bean
  CurrentUserInfoRestInterceptor getUserAuthRestInterceptor() {
    return new CurrentUserInfoRestInterceptor();
  }

  @Bean
  public QrConfig qrConfig(){
    QrConfig qrConfig = new QrConfig();
    qrConfig.setBackColor(Color.white.getRGB());
    qrConfig.setForeColor(Color.black.getRGB());
    return qrConfig;
  }
}
