package com.yunya.modules.clinic.base;

import com.yunya.feign.EnableYunyaFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;
import com.yunya.framework.swagger.EnableCustomSwagger2;

@SpringBootApplication
@MapperScan("com.yunya.clinic.base.mapper")
@EnableYunyaFeignClients
@EnableDiscoveryClient
@EnableCustomSwagger2
@ComponentScan(basePackages = {"com.yunya.modules.clinic.base","com.yunya.framework.common","com.yunya.framework.redis","com.yunya.framework.swagger"})
public class ClinicBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicBaseApplication.class, args);
    }

}
