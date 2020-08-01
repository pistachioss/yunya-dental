package com.yunya.modules.discount;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.yunya.modules.discount.mapper")
@EnableCustomSwagger2
@EnableDiscoveryClient
@EnableYunyaFeignClients
@ComponentScan(basePackages = {"com.yunya.modules.discount", "com.yunya.framework"})
public class ClinicDiscountApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicDiscountApplication.class, args);
    }

}
