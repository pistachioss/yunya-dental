package com.yunya.gate.config;

import com.yunya.gate.handler.RequestBodyRoutePredicateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置
 *
 * @author ace
 * @create 2019/1/27.
 */
@Configuration
public class GatewayConfig {

  @Bean
  RequestBodyRoutePredicateFactory requestBodyRoutePredicateFactory() {
    return new RequestBodyRoutePredicateFactory();
  }
}
