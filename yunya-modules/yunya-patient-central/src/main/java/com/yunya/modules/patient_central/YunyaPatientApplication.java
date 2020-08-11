package com.yunya.modules.patient_central;

/**
 * 简单介绍:</br> 云牙系统患者模块后台服务启动器
 *
 * @author: WY
 * @date 2020/7/24 19:27
 * @description:
 * @since: 1.0.0
 */

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 *
 *
 * @author wy
 */
@SpringBootApplication
@EnableYunyaFeignClients
@MapperScan("com.yunya.modules.patient_central.mapper")
@EnableTransactionManagement
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.yunya.modules.patient_central", "com.yunya.framework"})
@EnableCustomSwagger2
public class YunyaPatientApplication {
    public static void main(String[] args) {
        SpringApplication.run(YunyaPatientApplication.class, args);
    }
}
