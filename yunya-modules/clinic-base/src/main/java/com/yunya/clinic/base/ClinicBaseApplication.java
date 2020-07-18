package com.yunya.clinic.base;

import com.yunya.feign.EnableYunyaFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.yunya.clinic.base.mapper")
@EnableYunyaFeignClients
@EnableDiscoveryClient
public class ClinicBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicBaseApplication.class, args);
    }

}
