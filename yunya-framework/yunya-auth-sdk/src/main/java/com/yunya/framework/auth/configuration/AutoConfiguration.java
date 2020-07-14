package com.yunya.framework.auth.configuration;

import com.yunya.framework.auth.config.ServiceAuthConfig;
import com.yunya.framework.auth.config.UserAuthConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置用户、服务鉴权配置信息
 *
 * @author ace
 * @date 2017/9/15
 */
@Configuration
@ComponentScan({"com.yunya.framework"})
public class AutoConfiguration {

  /**
   * 获取服务权限配置
   *
   * @return
   */
  @Bean
  ServiceAuthConfig getServiceAuthConfig() {
    return new ServiceAuthConfig();
  }

  /**
   * 获取用户权限配置
   *
   * @return
   */
  @Bean
  UserAuthConfig getUserAuthConfig() {
    return new UserAuthConfig();
  }
}
