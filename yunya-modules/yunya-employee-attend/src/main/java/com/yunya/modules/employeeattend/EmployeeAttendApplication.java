package com.yunya.modules.employeeattend;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableYunyaFeignClients
@MapperScan("com.yunya.modules.employeeattend.mapper")
@EnableTransactionManagement
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.yunya.modules.employeeattend", "com.yunya.framework"})
@EnableCustomSwagger2
public class EmployeeAttendApplication {

  public static void main(String[] args) {
    SpringApplication.run(EmployeeAttendApplication.class, args);
  }

}
