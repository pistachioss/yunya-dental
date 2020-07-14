package com.yunya.auth;

import com.yunya.feign.annotation.EnableYunyaFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 云牙鉴权服务启动类
 *
 * @author chow
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.yunya.auth.mapper")
@EnableCircuitBreaker
@EnableYunyaFeignClients
@ComponentScan(basePackages = {"com.yunya.auth", "com.yunya.framework.redis"})
public class YunyaAuthBootstrap {

  public static void main(String[] args) {
    SpringApplication.run(YunyaAuthBootstrap.class, args);
  }
}
