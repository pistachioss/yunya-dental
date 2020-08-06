package com.yunya.modules.tariff;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 简介: 云牙价目表服务启动器
 *
 * @author: chow
 * @date: 2020/7/31 13:19
 * @description:
 * @since: 1.0.0
 */
@SpringBootApplication
@EnableYunyaFeignClients
@MapperScan("com.yunya.modules.tariff.mapper")
@EnableTransactionManagement
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.yunya.modules.tariff", "com.yunya.framework"})
@EnableCustomSwagger2
public class YunyaTariffApplication {
  public static void main(String[] args) {
    SpringApplication.run(YunyaTariffApplication.class, args);
  }
}
