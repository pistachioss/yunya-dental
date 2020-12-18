package com.yunya.framework.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
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
    return conversionObjToMap(obj);
  }

  /**
   * 对象转map
   *
   * @param obj 对象
   * @return
   */
  public static Map<String, Object> conversionObjToMap(Object obj) {
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

  /**
   * Map转成实体对象
   *
   * @author chow
   * @create 2020/7/17
   * @since 1.0.0
   */
  public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
    if (map == null) {
      return null;
    }
    T obj = null;
    try {
      obj = clazz.newInstance();

      Field[] fields = obj.getClass().getDeclaredFields();
      for (Field field : fields) {
        int mod = field.getModifiers();
        if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
          continue;
        }
        field.setAccessible(true);
        String filedTypeName = field.getType().getName();
        if ("java.util.date".equalsIgnoreCase(filedTypeName)) {
          String dateTimestamp = String.valueOf(map.get(field.getName()));
          field.set(
              obj,
              "null".equalsIgnoreCase(dateTimestamp)
                  ? null
                  : new Date(Long.parseLong(dateTimestamp)));
        } else {
          field.set(obj, map.get(field.getName()));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return obj;
  }
}
