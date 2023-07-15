package com.yunya.modules.treatment.config;

import com.yunya.framework.common.handler.GlobalExceptionHandler;
import com.yunya.framework.common.interceptor.RepeatSubmitInterceptor;
import com.yunya.framework.common.utils.StringHelper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

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

  @Bean("requestInterceptor")
  public RequestInterceptor requestInterceptor() {
    return new RequestInterceptor() {
      @Override
      public void apply(RequestTemplate template) {
        //1.RequestContextHolder拿到request请求(通过threadLocal)
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();//旧请求
        //同步请求头数据，cookie  将当前请求头的cookie携带在发往的请求头中
        if (StringHelper.isNotNull(request)) {
          //2.同步请求头信息Cookie
          String cookie = request.getHeader("Cookie");
          //给新请求同步了老请求的cookie
          template.header("Cookie", cookie);
          //System.out.println("feign远程之前先进行requestInterceptor()");
        }
      }
    };
  }
}
