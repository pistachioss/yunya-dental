package com.yunya.framework.common.utils;

/**
 * 字符串处理
 *
 * @author ace
 * @date 2017/9/10
 */
public class StringHelper {

  /**
   * 对象转字符串
   *
   * @param obj
   * @return
   */
  public static String getObjectValue(Object obj) {
    return obj == null ? "" : obj.toString();
  }

  /**
   * 是否包含字符串
   *
   * @param str 验证字符串
   * @param strs 字符串组
   * @return 包含返回true
   */
  public static boolean inStringIgnoreCase(String str, String... strs) {
    if (str != null && strs != null) {
      for (String s : strs) {
        if (str.equalsIgnoreCase(s.trim())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * * 判断一个对象数组是否为空
   *
   * @param objects 要判断的对象数组 * @return true：为空 false：非空
   */
  public static boolean isEmpty(Object[] objects) {
    return isNull(objects) || (objects.length == 0);
  }

  /**
   * 判断数组对象是否为空
   *
   * @param objects 对象数组
   * @return
   */
  public static boolean isNull(Object[] objects) {
    return objects == null;
  }
}
