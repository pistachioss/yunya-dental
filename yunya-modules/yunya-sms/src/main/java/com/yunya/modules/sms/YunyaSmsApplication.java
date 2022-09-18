package com.yunya.modules.sms;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 简介：短信模块
 *
 * @author: chenlin
 * @Description: 短信模块
 * @Date: 2020/12/10 20:27
 * @since: 1.0.0
 */
@EnableAsync
@SpringBootApplication
@MapperScan("com.yunya.modules.sms.mapper")
@EnableYunyaFeignClients
@EnableDiscoveryClient
@EnableCustomSwagger2
@ComponentScan(basePackages = {"com.yunya.modules.sms","com.yunya.framework.common","com.yunya.framework.redis"})
public class YunyaSmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(YunyaSmsApplication.class, args);
    }
}
