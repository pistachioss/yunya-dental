package com.yunya.modules.emr;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.yunya.modules.emr.mapper")
@EnableYunyaFeignClients
@EnableDiscoveryClient
@EnableCustomSwagger2
@ComponentScan(basePackages = {"com.yunya.modules.emr","com.yunya.framework.common"})
public class YunyaEmrApplication {

    public static void main(String[] args) {
        SpringApplication.run(YunyaEmrApplication.class, args);
    }

}
