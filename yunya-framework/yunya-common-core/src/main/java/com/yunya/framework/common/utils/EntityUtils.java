package com.yunya.framework.common.utils;

import com.yunya.framework.common.context.BaseContextHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 实体类相关工具类 解决问题： 1、快速对实体的常驻字段，如：crtUser、crtHost、updUser等值快速注入
 *
 * @author Ace
 * @version 1.0
 * @date 2016年4月18日
 * @since 1.7
 */
@Slf4j
public class EntityUtils {

  /**
   * 快速将bean的crtUser、crtHost、crtTime、updUser、updHost、updTime附上相关值
   *
   * @param entity 实体bean
   * @author 王浩彬
   */
  public static <T> void setCreatAndUpdatInfo(T entity) {
    setCreateInfo(entity);
    setUpdatedInfo(entity);
  }

  /**
   * 快速将bean的crtUser、crtHost、crtTime附上相关值
   *
   * @param entity 实体bean
   * @author 王浩彬
   */
  public static <T> void setCreateInfo(T entity) {
    HttpServletRequest request = ServletUtils.getRequest();
    String hostIp = "";
    String name = "";
    Integer id = null;
    hostIp =
        StringUtils.defaultIfBlank(
            request.getHeader("userHost"), ClientIPUtil.getClientIp(request));
    name = StringUtils.trimToEmpty(request.getHeader("userName"));
    try {
      name = URLEncoder.encode(name, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    id =
        StringUtils.isBlank(request.getHeader("userId"))
            ? null
            : Integer.valueOf(request.getHeader("userId"));

    if (StringUtils.isBlank(name)) {
      name = BaseContextHandler.getUsername();
    }
    if (id == null) {
      id =
          StringUtils.isBlank(BaseContextHandler.getUserID())
              ? null
              : Integer.valueOf(BaseContextHandler.getUserID());
    }
    if( id == null) {
      id = 0;
    }
    if( name == null) {
      name = "guest";
    }

    // 默认属性
    String[] fields = {"crtName", "crtId", "crtHost", "crtTime"};
    Field field = ReflectionUtils.getAccessibleField(entity, "crtTime");
    // 默认值
    Object[] value = null;
    if (field != null && field.getType().equals(Date.class)) {
      value = new Object[] {name, id, hostIp, new Date()};
    }
    // 填充默认属性值
    setDefaultValues(entity, fields, value);
  }

  /**
   * 快速将bean的updUser、updHost、updTime附上相关值
   *
   * @param entity 实体bean
   * @author 王浩彬
   */
  public static <T> void setUpdatedInfo(T entity) {
    HttpServletRequest request = ServletUtils.getRequest();
    String hostIp = "";
    String name = "";
    Integer id = null;
    hostIp =
        StringUtils.defaultIfBlank(
            request.getHeader("userHost"), ClientIPUtil.getClientIp(request));
    name = StringUtils.trimToEmpty(request.getHeader("userName"));
    try {
      name = URLEncoder.encode(name, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    id =
        StringUtils.isBlank(request.getHeader("userId"))
            ? null
            : Integer.valueOf(request.getHeader("userId"));

    if (StringUtils.isBlank(name)) {
      name = BaseContextHandler.getUsername();
    }
    if (id == null) {
      id =
          StringUtils.isBlank(BaseContextHandler.getUserID())
              ? null
              : Integer.valueOf(BaseContextHandler.getUserID());
    }
    if( id == null) {
      id = 0;
    }
    if( name == null) {
      name = "guest";
    }

    // 默认属性
    String[] fields = {"updName", "updId", "updHost", "updTime"};
    Field field = ReflectionUtils.getAccessibleField(entity, "updTime");
    Object[] value = null;
    if (field != null && field.getType().equals(Date.class)) {
      value = new Object[] {name, id, hostIp, new Date()};
    }
    // 填充默认属性值
    setDefaultValues(entity, fields, value);
  }

  /**
   * 依据对象的属性数组和值数组对对象的属性进行赋值
   *
   * @param entity 对象
   * @param fields 属性数组
   * @param value 值数组
   * @author 王浩彬
   */
  private static <T> void setDefaultValues(T entity, String[] fields, Object[] value) {
    for (int i = 0; i < fields.length; i++) {
      String field = fields[i];
      if (ReflectionUtils.hasField(entity, field)) {
        ReflectionUtils.invokeSetter(entity, field, value[i]);
      }
    }
  }

  /**
   * 根据主键属性，判断主键是否值为空
   *
   * @param entity
   * @param field
   * @return 主键为空，则返回false；主键有值，返回true
   * @author 王浩彬
   * @date 2016年4月28日
   */
  public static <T> boolean isPKNotNull(T entity, String field) {
    if (!ReflectionUtils.hasField(entity, field)) {
      return false;
    }
    Object value = ReflectionUtils.getFieldValue(entity, field);
    return value != null && !"".equals(value);
  }

  public static <T> T build(Object fromObj, Class<T> targetClass) {
    if (null == targetClass || null == fromObj) {
      return null;
    }
    try {
      T targetObj = targetClass.newInstance();
      BeanInfo fromBeanInfo = Introspector.getBeanInfo(fromObj.getClass(), Object.class);
      PropertyDescriptor[] fromPds = fromBeanInfo.getPropertyDescriptors();

      for (PropertyDescriptor fromPd : fromPds) {
        PropertyDescriptor targetPd =
            BeanUtils.getPropertyDescriptor(targetClass, fromPd.getName());
        if (targetPd == null) {
          continue;
        }
        Class<?> targetType = targetPd.getPropertyType();
        Class<?> fromType = fromPd.getPropertyType();
        Method targetWriteMethod = targetPd.getWriteMethod();
        Method fromReadMethod = fromPd.getReadMethod();
        if (targetWriteMethod != null && fromReadMethod != null) {
          Object value = fromReadMethod.invoke(fromObj);
          if (value == null) {
            continue;
          }
          // 类型相同
          if (targetType.equals(fromType)) {
            targetWriteMethod.invoke(targetObj, value);
            continue;
          }
          // 类型不同 针对字符型、数值型进行兼容处理
          String literalValue = String.valueOf(value);
          if (String.class.equals(targetType)) {
            targetWriteMethod.invoke(targetObj, literalValue);
            continue;
          }
          if (StringUtils.isNumeric(literalValue)) {
            if (Byte.class.equals(targetType) || byte.class.equals(targetType)) {
              targetWriteMethod.invoke(targetObj, NumberUtils.toByte(literalValue));
              continue;
            }
            if (Short.class.equals(targetType) || short.class.equals(targetType)) {
              targetWriteMethod.invoke(targetObj, NumberUtils.toShort(literalValue));
              continue;
            }
            if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
              targetWriteMethod.invoke(targetObj, NumberUtils.toInt(literalValue));
              continue;
            }
            if (Long.class.equals(targetType) || long.class.equals(targetType)) {
              targetWriteMethod.invoke(targetObj, NumberUtils.toLong(literalValue));
              continue;
            }
            if (Double.class.equals(targetType) || double.class.equals(targetType)) {
              targetWriteMethod.invoke(targetObj, NumberUtils.toDouble(literalValue));
              continue;
            }
            if (Float.class.equals(targetType) || float.class.equals(targetType)) {
              targetWriteMethod.invoke(targetObj, NumberUtils.toFloat(literalValue));
            }
          }
        }
      }
      return targetObj;
    } catch (Exception e) {
      log.error("build fail!", e);
    }
    return null;
  }

  public static <T> List<T> build(List<?> objects, Class<T> targetClass) {
    List<T> list = new ArrayList<T>();
    if (objects == null || objects.size() == 0) {
      return list;
    }
    for (Object object : objects) {
      T t = build(object, targetClass);
      if (null != t) {
        list.add(t);
      }
    }
    return list;
  }
}
