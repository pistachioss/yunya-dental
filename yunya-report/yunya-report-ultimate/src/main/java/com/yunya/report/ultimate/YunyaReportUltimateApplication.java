package com.yunya.report.ultimate;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 简介: 云牙报表服务服务启动器
 *
 * @author: chow
 * @date: 2020/7/31 13:19
 * @description:
 * @since: 1.0.0
 */
@SpringBootApplication
@EnableYunyaFeignClients
@MapperScan("com.yunya.report.ultimate.mapper")
@EnableTransactionManagement
@EnableDiscoveryClient
@EnableScheduling
@ComponentScan(basePackages = {"com.yunya.report.ultimate", "com.yunya.framework"})
@EnableCustomSwagger2
public class YunyaReportUltimateApplication {
  public static void main(String[] args) {
    SpringApplication.run(YunyaReportUltimateApplication.class, args);
  }
}
