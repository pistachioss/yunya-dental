package com.yunya.gate;

import com.yunya.framework.auth.EnableYunyaAuthClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 云牙系统网关启动器
 *
 * @author ace
 * @create 2018/3/12.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableYunyaAuthClient
@EnableFeignClients({"com.yunya.framework.auth"})
public class YunyaGatewayBootstrap {
  public static void main(String[] args) {
    // DBLog.getInstance().start();
    SpringApplication.run(YunyaGatewayBootstrap.class, args);
  }

  /** 获取服务启动创建bean */
  /*  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      System.out.println("Let's inspect the beans provided by Spring Boot:");

      String[] beanNames = ctx.getBeanDefinitionNames();
      Arrays.sort(beanNames);
      for (String beanName : beanNames) {
        System.out.println(beanName);
      }

      AutoConfiguration bean =
          ctx.getBean(
              "com.yunya.framework.auth.configuration.AutoConfiguration", AutoConfiguration.class);
      Method[] methods = bean.getClass().getDeclaredMethods();
      for (Method method : methods) {
        if ("getServiceAuthConfig".equals(method.getName())) {
          method.setAccessible(true);
         ServiceAuthConfig s = (ServiceAuthConfig) method.invoke(bean);
          System.out.println("--------------------" + (s.toString()));
        }
      }
    };
  }*/
}
