package com.yunya.modules.treatment.other;

import com.yunya.feign.EnableYunyaFeignClients;
import com.yunya.framework.swagger.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@MapperScan("com.yunya.modules.treatment.other.mapper")
@EnableYunyaFeignClients
@EnableDiscoveryClient
@EnableCustomSwagger2
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.yunya.modules.treatment.other","com.yunya.framework"})
public class YunyaTreatmentOtherApplication {
    public static void main(String[] args) {
        SpringApplication.run(YunyaTreatmentOtherApplication.class, args);
//        MyWebService webService = SpringContextUtil.getBean(MyWebService.class);
//        String path = "http://127.0.0.1:8595/user";
//        Endpoint.publish(path, webService);
//        System.out.println("webservice服务发布成功，在线的wsdl：" + path + "?wsdl");
    }

}
