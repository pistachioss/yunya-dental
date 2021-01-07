package com.yunya365.aliyunoss;

import com.yunya.framework.swagger.EnableCustomSwagger2;
import com.yunya365.aliyunoss.property.AliyunOssProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCustomSwagger2
@EnableConfigurationProperties(AliyunOssProperties.class)
public class AliyunOssApplication {

    public static void main(String[] args) {
        SpringApplication.run(AliyunOssApplication.class, args);
    }
}
