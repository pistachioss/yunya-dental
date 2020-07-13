package com.yunya.framework.redis.annotation;

import java.lang.annotation.*;

/**
 * redis缓存注解
 *
 * @author chow
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {

  /**
   * 键名
   *
   * @return
   */
  String key() default "";

  /**
   * 主键
   *
   * @return
   * @author zmr
   */
  String fieldKey();

  /**
   * 过期时间
   *
   * @return
   */
  long expired() default 3600;

  /**
   * 是否为查询操作 如果为写入数据库的操作，该值需置为 false
   *
   * @return
   */
  boolean read() default true;
}
