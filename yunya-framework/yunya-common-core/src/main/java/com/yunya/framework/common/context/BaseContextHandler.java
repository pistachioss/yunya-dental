package com.yunya.framework.common.context;

import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息上下文处理
 *
 * @author ace
 * @date 2017/9/8
 */
public class BaseContextHandler {

  public static ThreadLocal<Map<String, Object>> threadLocal =
      new ThreadLocal<Map<String, Object>>();

  /**
   * 获取当前组织ID
   *
   * @return
   */
  public static String getOrgId() {
    Object value = get(CommonConstants.CONTEXT_KEY_ORG_ID);
    return returnObjectValue(value);
  }

  /**
   * 获取当前时间 （接口中所有方法或feign调用接口都用同一时间作为操作时间）
   *
   * @return
   */
  public static Date getCurTime() {
    Date value = get(CommonConstants.CONTEXT_KEY_TIME);
    if (StringHelper.isNull(value)) {
      // 从请求头中获取
      ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      String curTime = requestAttributes.getRequest().getHeader(CommonConstants.CONTEXT_KEY_TIME);
      if (StringHelper.isNull(curTime)) {
        // 获取当前时间
        value = DateUtil.now();
      } else {
        value = DateUtil.parse(curTime);
      }
      setCurTime(value);
    }
    return value;
  }

  /**
   * 获取当前用户ID
   *
   * @return
   */
  public static String getUserID() {
    Object value = get(CommonConstants.CONTEXT_KEY_USER_ID);
    return returnObjectValue(value);
  }

  /**
   * 获取当前用户名
   *
   * @return
   */
  public static String getUsername() {
    Object value = get(CommonConstants.CONTEXT_KEY_USERNAME);
    return returnObjectValue(value);
  }

  /**
   * 获取当前用户姓名
   *
   * @return
   */
  public static String getName() {
    Object value = get(CommonConstants.CONTEXT_KEY_USER_NAME);
    return StringHelper.getObjectValue(value);
  }

  /**
   * 获取当前token
   *
   * @return
   */
  public static String getToken() {
    Object value = get(CommonConstants.CONTEXT_KEY_USER_TOKEN);
    return StringHelper.getObjectValue(value);
  }

  public static String getAuthorization() {
    Object value = get(CommonConstants.TOKEN_HEADER);
    return StringHelper.getObjectValue(value);
  }

  public static String getOpenId() {
    Object value = get(CommonConstants.JWT_KEY_OPEN_ID);
    return StringHelper.getObjectValue(value);
  }

  /**
   * 设置当前时间
   *
   * @param curTime
   */
  public static void setCurTime(Date curTime) {
    set(CommonConstants.CONTEXT_KEY_TIME, curTime);
  }
  /**
   * 设置组织ID
   *
   * @param orgId
   */
  public static void setOrgId(String orgId) {
    set(CommonConstants.CONTEXT_KEY_ORG_ID, orgId);
  }

  /**
   * 设置token
   *
   * @param token
   */
  public static void setToken(String token) {
    set(CommonConstants.CONTEXT_KEY_USER_TOKEN, token);
  }

  /**
   * 设置姓名
   *
   * @param name
   */
  public static void setName(String name) {
    set(CommonConstants.CONTEXT_KEY_USER_NAME, name);
  }

  /**
   * 设置用户ID
   *
   * @param userID
   */
  public static void setUserID(String userID) {
    set(CommonConstants.CONTEXT_KEY_USER_ID, userID);
  }

  /**
   * 设置用户名
   *
   * @param username
   */
  public static void setUsername(String username) {
    set(CommonConstants.CONTEXT_KEY_USERNAME, username);
  }

  /**
   * Authorization
   * @param authorization:
   * @return void
   */
  public static void setAuthorization(String authorization) {
    set(CommonConstants.TOKEN_HEADER, authorization);
  }

  /**
   * 设置open_id
   *
   * @param openId openId
   */
  public static void setOpenId(String openId) {
    set(CommonConstants.JWT_KEY_OPEN_ID, openId);
  }

  /**
   * 对象转字符串
   *
   * @param value
   * @return
   */
  private static String returnObjectValue(Object value) {
    return value == null ? null : value.toString();
  }

  /**
   * 设置到局部线程变量
   *
   * @param key
   * @param value
   */
  public static void set(String key, Object value) {
    Map<String, Object> map = threadLocal.get();
    if (map == null) {
      map = new HashMap<String, Object>();
      threadLocal.set(map);
    }
    map.put(key, value);
  }

  /**
   * 从局部线程变量中获取
   *
   * @param key
   * @return
   */
//  public static Object get(String key) {
//    Map<String, Object> map = threadLocal.get();
//    if (map == null) {
//      map = new HashMap<String, Object>();
//      threadLocal.set(map);
//    }
//    return map.get(key);
//  }

  /**
   * 从局部线程变量中获取
   *
   * @param key
   * @return
   */
  public static <T> T get(String key) {
    Map<String, Object> map = threadLocal.get();
    if (map == null) {
      map = new HashMap<String, Object>();
      threadLocal.set(map);
    }
    return (T) map.get(key);
  }

  /** 从线程局部变量中移除 */
  public static void remove() {
    threadLocal.remove();
  }
}
