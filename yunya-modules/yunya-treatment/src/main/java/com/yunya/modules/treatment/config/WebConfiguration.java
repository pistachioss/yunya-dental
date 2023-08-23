package com.yunya.modules.treatment.config;

import com.yunya.framework.common.handler.GlobalExceptionHandler;
import com.yunya.framework.common.interceptor.RepeatSubmitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置请求需要拦截的路径
 *
 * @author chow
 */
@Configuration("treatmentWebConfig")
@Primary
public class WebConfiguration implements WebMvcConfigurer {

  /** 放重复提交拦截器 */
  @Autowired private RepeatSubmitInterceptor repeatSubmitInterceptor;

  /**
   * 配置拦截器
   *
   * @param registry 拦截注册表
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(getUserAuthRestInterceptor()).addPathPatterns("/**");
    registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
  }

  /**
   * 获取全局异常处理器
   *
   * @return
   */
  @Bean
  GlobalExceptionHandler getGlobalExceptionHandler() {
    return new GlobalExceptionHandler();
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


//  @Autowired
//  private MyWebService myWebService;
//
//  @Autowired
//  private Bus bus;

  /**
   * 发布服务
   * @return
   */
//  @Bean
//  public Endpoint userServiceEndpoint() {
//    //这里指定的端口不能跟应用的端口冲突, 单独指定
//    String path = "http://127.0.0.1:9090/user";
//
//    EndpointImpl userEndpoint = new EndpointImpl(bus, myWebService);
//    userEndpoint.publish(path);
//
//    System.out.println("webservice服务发布成功，在线的wsdl：" + path + "?wsdl");
//    return userEndpoint;
//  }
}
