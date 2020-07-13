package com.yunya.framework.redis.annotation;

import java.lang.annotation.*;

/**
 * File：RedisEvict.java
 *
 * <p>Title: redis删除注解
 *
 * <p>Description:
 *
 * <p>Copyright: Copyright (c) 2018 2018年12月6日 下午4:33:31
 *
 * <p>Company:
 *
 * @author zmr
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisEvict {

  String key();

  String fieldKey();
}
