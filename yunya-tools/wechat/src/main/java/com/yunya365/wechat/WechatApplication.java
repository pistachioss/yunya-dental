package com.yunya365.wechat;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.yunya365.wechat.mapper")
@EnableCustomSwagger2
@EnableDiscoveryClient
@EnableYunyaFeignClients
@ComponentScan(basePackages = {"com.yunya365.wechat", "com.yunya.framework"})
public class WechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatApplication.class, args);
    }

}
