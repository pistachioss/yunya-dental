package com.yunya.gate;

import com.yunya.feign.annotation.EnableYunyaFeignClients;
import com.yunya.framework.auth.EnableYunyaAuthClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 云牙系统网关启动器
 *
 * @author ace
 * @create 2018/3/12.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableYunyaAuthClient
@EnableYunyaFeignClients
public class YunyaGatewayBootstrap {
  public static void main(String[] args) {
    // DBLog.getInstance().start();
    SpringApplication.run(YunyaGatewayBootstrap.class, args);
  }
}
