package com.yunya.framework.common.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈Map集合工具类〉
 *
 * @author chow
 * @create 2019/7/6
 * @since 1.0.0
 */
public class MapUtil {

  /**
   * obj转map
   *
   * @param obj 对象
   * @return map
   */
  public static Map<String, Object> objectToMap(Object obj) {
    Map<String, Object> map = new HashMap<>(16);
    Class<?> clazz = obj.getClass();
    try {
      for (Field field : clazz.getDeclaredFields()) {
        // 设置可以访问私有变量
        field.setAccessible(true);
        String fieldName = field.getName();
        Object value = null;
        if (field.get(obj) != null) {
          value = field.get(obj);
        }
        map.put(fieldName, value);
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return map;
  }
}
