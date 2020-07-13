package com.yunya.framework.auth;

import com.yunya.framework.auth.configuration.AutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 服务自动鉴权注解
 *
 * @author ace
 * @date 2017/9/15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AutoConfiguration.class)
@Documented
@Inherited
public @interface EnableYunyaAuthClient {}
