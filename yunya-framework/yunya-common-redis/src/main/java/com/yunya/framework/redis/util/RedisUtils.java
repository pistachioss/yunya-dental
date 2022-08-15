package com.yunya.framework.redis.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
  private final ZSetOperations<String, String> zSetOps;
  private final GeoOperations<String, String> opsForGeo;

  public RedisUtils(RedisTemplate<String, String> redisTemplate) {
    zSetOps = redisTemplate.opsForZSet();
    opsForGeo = redisTemplate.opsForGeo();
  }

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
   * 插入缓存
   *
   * @param key 键
   * @param value 值
   * @param expire 过期时间(s)
   * @author zmr
   */
  public void set(String key, Object value, long expire, TimeUnit unit) {
    valueOperations.set(key, toJson(value));
    redisTemplate.expire(key, expire, unit);
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
   * 按delta递增
   */
  public Long incr(String key, long delta) {
    return valueOperations.increment(key, delta);
  }


  /**
   * 返回指定类型结果列表
   *
   * @param key 键
   * @param clazz 类型class
   * @return
   * @author zmr
   */
  public <T> List<T> getJSONArray(String key, Class<T> clazz) {
    String value = valueOperations.get(key);
    return value == null ? null : fromJSONArray(value, clazz);
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
   * JSON数据，转成List<T>
   *
   * @param json
   * @param clazz
   * @param <T>
   * @return
   */
  private <T> List<T> fromJSONArray(String json, Class<T> clazz) {
    return JSON.parseArray(json, clazz);
  }

  /**
   * 模糊查询key*
   * @param key [pattern]
   * @return set
   */
  public Set<String> keys(String key){
    return redisTemplate.keys(key);
  }

  /**
   * 设置过期时间
   *
   * @param key key
   * @param timeout time
   * @param unit unit
   * @return
   */
  public Boolean expire(String key, long timeout, TimeUnit unit) {
    return redisTemplate.expire(key, timeout, unit);
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

  /**
   * 往队列的左端添加
   *
   * @param key
   * @param value
   * @return
   */
  public Long lPush(String key, Object value) {
    return redisTemplate.opsForList().leftPush(key, toJson(value));
  }

  /**
   * 从队列的左端获取并移除
   *
   * @param key
   * @return
   */
  public <T> T lPop(String key, Class<T> clazz) {
    String value = (String) redisTemplate.opsForList().leftPop(key);
    return value == null ? null : fromJson(value, clazz);
  }

  /**
   * 往队列的左端添加
   *
   * @param key
   * @return
   */
  public Long rPush(String key, String value) {
    return redisTemplate.opsForList().rightPush(key, toJson(value));
  }

  /**
   * 从队列的右端获取并移除
   *
   * @param key
   * @return
   */
  public <T> T rPop(String key, Class<T> clazz) {
    String value = (String) redisTemplate.opsForList().rightPop(key);
    return value == null ? null : fromJson(value, clazz);
  }

  // ---------------------------- zSet start ----------------------------
  /**
   * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score
   * 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。 score 值可以是整数值或双精度浮点数。 如果 key 不存在，则创建一个空的有序集并执行 ZADD
   * 操作。 当 key 存在但不是有序集类型时，返回一个错误。
   *
   * @param key 一定不能为 {@literal null}.
   * @param score 得分
   * @param member 值
   * @return 是否成功
   * @see <a href="https://redis.io/commands/zadd">Redis Documentation: ZADD</a>
   */
  public Boolean zAdd(@NonNull String key, String member, double score) {
    return zSetOps.add(key, member, score);
  }

  /**
   * 为有序集 key 的成员 member 的 score 值加上增量 increment 。 可以通过传递一个负数值 increment ，让 score 减去相应的值，比如 ZINCRBY
   * key -5 member ，就是让 member 的 score 值减去 5 。 当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key increment
   * member 等同于 ZADD key increment member 。 当 key 不是有序集类型时，返回一个错误。 score 值可以是整数值或双精度浮点数。
   *
   * @param key 一定不能为 {@literal null}.
   * @param score 得分
   * @param member the value.
   * @return member 成员的新 score 值
   * @see <a href="https://redis.io/commands/zincrby">Redis Documentation: ZINCRBY</a>
   */
  public Double zIncrBy(@NonNull String key, String member, double score) {
    return zSetOps.incrementScore(key, member, score);
  }

  /**
   * 返回有序集 key 中，指定区间内的成员。 其中成员的位置按 score 值递减(从大到小)来排列。 具有相同 score 值的成员按字典序的逆序(reverse
   * lexicographical order)排列。 除了成员按 score 值递减的次序排列这一点外， ZREVRANGE 命令的其他方面和 ZRANGE key start stop
   * [WITHSCORES] 命令一样。
   *
   * @param key 一定不能为 {@literal null}.
   * @param start 索引
   * @param end 索引
   * @return 指定区间内，不带有 score 值(可选)的有序集成员的列表。
   * @see <a href="https://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
   */
  @Nullable
  public Set<String> zRevrange(@NonNull String key, long start, long end) {
    return zSetOps.reverseRange(key, start, end);
  }

  /**
   * 返回有序集 key 中，指定区间内的成员。 其中成员的位置按 score 值递减(从大到小)来排列。 具有相同 score 值的成员按字典序的逆序(reverse
   * lexicographical order)排列。 除了成员按 score 值递减的次序排列这一点外， ZREVRANGE 命令的其他方面和 ZRANGE key start stop
   * [WITHSCORES] 命令一样。
   *
   * @param key 一定不能为 {@literal null}.
   * @param start 索引
   * @param end 索引
   * @return 指定区间内，不带有 score 值(可选)的有序集成员的列表。
   * @see <a href="https://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
   */
  @Nullable
  public Set<ZSetOperations.TypedTuple<String>> zRevrangeWithScores(
          @NonNull String key, long start, long end) {
    return zSetOps.reverseRangeWithScores(key, start, end);
  }
}
