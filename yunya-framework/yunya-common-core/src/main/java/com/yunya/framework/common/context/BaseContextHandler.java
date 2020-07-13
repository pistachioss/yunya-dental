package com.yunya.framework.common.context;

import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.utils.StringHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户基本信息上下文处理
 *
 * @author chow
 */
public class BaseContextHandler {

  public static ThreadLocal<Map<String, Object>> threadLocal =
      new ThreadLocal<Map<String, Object>>();

  /**
   * 设置线程局部变量
   *
   * @param key 键
   * @param value 值
   */
  public static void set(String key, Object value) {
    Map<String, Object> map = threadLocal.get();
    if (map == null) {
      map = new HashMap<String, Object>(16);
      threadLocal.set(map);
    }
    map.put(key, value);
  }

  /**
   * 获取线程局部变量对象
   *
   * @param key 键
   * @return
   */
  public static Object get(String key) {
    Map<String, Object> map = threadLocal.get();
    if (map == null) {
      map = new HashMap<String, Object>(16);
      threadLocal.set(map);
    }
    return map.get(key);
  }

  /**
   * 获取用户ID
   *
   * @return
   */
  public static String getUserID() {
    Object value = get(CommonConstants.CONTEXT_KEY_USER_ID);
    return returnObjectValue(value);
  }

  /**
   * 获取用户名
   *
   * @return
   */
  public static String getUsername() {
    Object value = get(CommonConstants.CONTEXT_KEY_USERNAME);
    return returnObjectValue(value);
  }

  /**
   * 获取用户姓名
   *
   * @return
   */
  public static String getName() {
    Object value = get(CommonConstants.CONTEXT_KEY_USER_NAME);
    return StringHelper.getObjectValue(value);
  }

  /**
   * 获取用户token
   *
   * @return
   */
  public static String getToken() {
    Object value = get(CommonConstants.CONTEXT_KEY_USER_TOKEN);
    return StringHelper.getObjectValue(value);
  }

  /**
   * 设置用户token
   *
   * @param token token
   */
  public static void setToken(String token) {
    set(CommonConstants.CONTEXT_KEY_USER_TOKEN, token);
  }

  /**
   * 设置用户姓名
   *
   * @param name 姓名
   */
  public static void setName(String name) {
    set(CommonConstants.CONTEXT_KEY_USER_NAME, name);
  }

  /**
   * 设置用户ID
   *
   * @param userID 用户ID
   */
  public static void setUserID(String userID) {
    set(CommonConstants.CONTEXT_KEY_USER_ID, userID);
  }

  /**
   * 设置用户名
   *
   * @param username 用户名
   */
  public static void setUsername(String username) {
    set(CommonConstants.CONTEXT_KEY_USERNAME, username);
  }

  /**
   * 将对象信息转字符串
   *
   * @param value 值
   * @return
   */
  private static String returnObjectValue(Object value) {
    return value == null ? null : value.toString();
  }

  /** 清空局部线程 */
  public static void remove() {
    threadLocal.remove();
  }
}
