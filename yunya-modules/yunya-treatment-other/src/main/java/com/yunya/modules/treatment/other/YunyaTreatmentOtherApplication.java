package com.yunya.modules.treatment.other;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@MapperScan("com.yunya.modules.treatment.other.mapper")
@EnableYunyaFeignClients
@EnableDiscoveryClient
@EnableCustomSwagger2
@ComponentScan(basePackages = {"com.yunya.modules.treatment.other","com.yunya.framework.common","com.yunya.framework.redis"})
public class YunyaTreatmentOtherApplication{

    public static void main(String[] args) {
        SpringApplication.run(YunyaTreatmentOtherApplication.class, args);
    }

}
