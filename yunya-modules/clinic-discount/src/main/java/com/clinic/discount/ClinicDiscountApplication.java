package com.clinic.discount;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.clinic.discount.mapper")
@EnableCustomSwagger2
@EnableDiscoveryClient
@EnableYunyaFeignClients
@ComponentScan(basePackages = {"com.clinic.discount.*", "com.yunya.framework.common"})
public class ClinicDiscountApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicDiscountApplication.class, args);
    }

}
