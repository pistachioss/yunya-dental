package com.yunya.modules.system;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.common.swagger.anotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 云牙系统管理后台服务启动器
 *
 * @author chow
 */
@SpringBootApplication
@EnableYunyaFeignClients
@MapperScan("com.yunya.modules.system.mapper")
@EnableTransactionManagement
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.yunya.modules.system", "com.yunya.framework"})
@EnableCustomSwagger2
public class YunyaSystemApplication {

  public static void main(String[] args) {
    SpringApplication.run(YunyaSystemApplication.class, args);
  }
}
