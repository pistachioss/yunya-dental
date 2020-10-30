package com.yunya365.rabbitmq;


import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCustomSwagger2
public class RabbitMqApplication {

    public static void main(String[] args) {

        SpringApplication.run(RabbitMqApplication.class, args);
    }
}
