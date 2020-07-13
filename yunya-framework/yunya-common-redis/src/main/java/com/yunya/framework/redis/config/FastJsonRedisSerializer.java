package com.yunya.framework.redis.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 缓存数据序列化
 *
 * @author chow
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

  public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  private Class<T> clazz;

  public FastJsonRedisSerializer(Class<T> clazz) {
    super();
    this.clazz = clazz;
  }

  /**
   * 序列化
   *
   * @param t
   * @return
   * @throws SerializationException
   */
  @Override
  public byte[] serialize(T t) throws SerializationException {
    if (t == null) {
      return new byte[0];
    }
    return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
  }

  /**
   * 反序列化
   *
   * @param bytes
   * @return
   * @throws SerializationException
   */
  @Override
  public T deserialize(byte[] bytes) throws SerializationException {
    if (bytes == null || bytes.length <= 0) {
      return null;
    }
    String str = new String(bytes, DEFAULT_CHARSET);
    return (T) JSON.parseObject(str, clazz);
  }
}
