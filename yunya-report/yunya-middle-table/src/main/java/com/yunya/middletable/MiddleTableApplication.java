package com.yunya.middletable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.yunya.middletable.dao")
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class MiddleTableApplication {

    public static void main(String[] args) {

        SpringApplication.run(MiddleTableApplication.class, args);
    }
}
