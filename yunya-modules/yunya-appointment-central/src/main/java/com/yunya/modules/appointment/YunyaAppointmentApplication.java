package com.yunya.modules.appointment;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 云牙系统患者预约服务启动器
 *
 * @Author 李慧斌
 * @create 2020年7月20日
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.yunya.modules.appointment", "com.yunya.framework"})
@MapperScan("com.yunya.modules.appointment.mapper")
@EnableCustomSwagger2
@EnableYunyaFeignClients
@EnableTransactionManagement
@EnableDiscoveryClient
public class YunyaAppointmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(YunyaAppointmentApplication.class, args);
    }
}
