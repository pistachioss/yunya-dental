package com.yunya.feign.annotation;

import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.*;

/**
 * 云牙系统FeignClients注解
 *
 * @author chow
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients
public @interface EnableYunyaFeignClients {

  String[] value() default {};

  String[] basePackages() default {"com.yunya"};

  Class<?>[] basePackageClasses() default {};

  Class<?>[] defaultConfiguration() default {};

  Class<?>[] clients() default {};
}
