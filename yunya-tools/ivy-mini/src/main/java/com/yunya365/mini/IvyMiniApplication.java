package com.yunya365.mini;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.yunya365.mini.mapper")
@EnableCustomSwagger2
@EnableDiscoveryClient
@EnableYunyaFeignClients
@ComponentScan(basePackages = {"com.yunya365.mini", "com.yunya.framework"})
public class IvyMiniApplication {

    public static void main(String[] args) {
        SpringApplication.run(IvyMiniApplication.class, args);
    }

}
