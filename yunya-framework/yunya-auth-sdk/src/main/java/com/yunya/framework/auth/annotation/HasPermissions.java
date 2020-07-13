package com.yunya.framework.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * File：HasPermissions.java
 *
 * <p>Title: 接口访问权限注解
 *
 * <p>Description:
 **
 * <p>Company:
 *
 * @author chow
 * @version 1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasPermissions {
  String value();
}
