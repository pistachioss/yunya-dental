package com.yunya.framework.redis.util;

import com.alibaba.fastjson.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.*;
import org.springframework.stereotype.*;

import javax.annotation.*;
import java.nio.charset.*;
import java.util.concurrent.*;

/**
 * Redis工具类
 *
 * @author chow
 */
@Component
@Slf4j
public class RedisUtils {

  @Autowired private RedisTemplate<String, Object> redisTemplate;

  @Autowired private StringRedisTemplate stringRedisTemplate;

  @Resource(name = "redisTemplate")
  private ValueOperations<String, Object> valOpsObj;

  @Resource(name = "stringRedisTemplate")
  private ValueOperations<String, String> valueOperations;

  /** 默认过期时长(24h)，单位：秒 */
  public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

  /** 不设置过期时长 */
  public static final long NOT_EXPIRE = -1;

  /**
   * 释放锁脚本，原子操作，lua脚本
   */
  private static final String UNLOCK_LUA;

  static {
    StringBuilder sb = new StringBuilder();
    sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
    sb.append("then ");
    sb.append("    return redis.call(\"del\",KEYS[1]) ");
    sb.append("else ");
    sb.append("    return 0 ");
    sb.append("end ");
    UNLOCK_LUA = sb.toString();
  }


  /**
   * 判断缓存中是否存在
   *
   * @param key 键
   * @return
   */
  public Boolean hasKey(String key) {
    return redisTemplate.hasKey(key);
  }

  /**
   * 插入缓存默认时间
   *
   * @param key 键
   * @param value 值
   * @author zmr
   */
  public void set(String key, Object value) {
    set(key, value, DEFAULT_EXPIRE);
  }

  /**
   * 插入缓存
   *
   * @param key 键
   * @param value 值
   * @param expire 过期时间(s)
   * @author zmr
   */
  public void set(String key, Object value, long expire) {
    valueOperations.set(key, toJson(value));
    redisTemplate.expire(key, expire, TimeUnit.SECONDS);
  }

  /**
   * 返回字符串结果
   *
   * @param key 键
   * @return
   * @author zmr
   */
  public String get(String key) {
    return valueOperations.get(key);
  }

  /**
   * 返回指定类型结果
   *
   * @param key 键
   * @param clazz 类型class
   * @return
   * @author zmr
   */
  public <T> T get(String key, Class<T> clazz) {
    String value = valueOperations.get(key);
    return value == null ? null : fromJson(value, clazz);
  }

  /**
   * 删除缓存
   *
   * @param key 键
   * @author zmr
   */
  public void delete(String key) {
    redisTemplate.delete(key);
  }

  /**
   * Object转成JSON数据
   *
   * @param object 对象
   * @return
   */
  private String toJson(Object object) {
    if (object instanceof Integer
        || object instanceof Long
        || object instanceof Float
        || object instanceof Double
        || object instanceof Boolean
        || object instanceof String) {
      return String.valueOf(object);
    }
    return JSON.toJSONString(object);
  }

  /**
   * JSON数据，转成Object
   *
   * @param json
   * @param clazz
   * @param <T>
   * @return
   */
  private <T> T fromJson(String json, Class<T> clazz) {
    return JSON.parseObject(json, clazz);
  }

  /**
   * 加锁
   */
  public boolean setLock(String key, String val, long expire, TimeUnit timeUnit) {
    try {
      RedisCallback<Boolean> callback = (connection) ->
              connection.set(key.getBytes(StandardCharsets.UTF_8),
                      val.getBytes(StandardCharsets.UTF_8),
                      Expiration.seconds(timeUnit.toSeconds(expire)),
                      RedisStringCommands.SetOption.SET_IF_ABSENT);
      return redisTemplate.execute(callback);
    } catch (Exception e) {
      log.error("Failed to LOCK because of undefined redis connection");
    }
    return false;
  }

  /**
   * 释放锁
   */
  public boolean unlock(String lockKey, String lockValue) {
    RedisCallback<Boolean> callback = (connection) ->
            connection.eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN, 1,
                    lockKey.getBytes(StandardCharsets.UTF_8),
                    lockValue.getBytes(StandardCharsets.UTF_8));
    return redisTemplate.execute(callback);
  }
}
