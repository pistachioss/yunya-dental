package com.yunya.employee.common;

import com.yunya.feign.EnableYunyaFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.yunya.employee.common.mapper")
@EnableYunyaFeignClients
@EnableDiscoveryClient
public class EmployeeCommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeCommonApplication.class, args);
    }

}
