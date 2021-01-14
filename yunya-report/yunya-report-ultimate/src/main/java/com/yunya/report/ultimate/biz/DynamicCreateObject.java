package com.yunya.report.ultimate.biz;

import javassist.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2021/1/13 14:15
 * @description:
 * @since: 1.0.0
 */
public class DynamicCreateObject {

  public static void main(String[] args)
      throws NotFoundException, CannotCompileException, IllegalAccessException,
          InstantiationException, NoSuchMethodException, InvocationTargetException,
          ClassNotFoundException {
    DynamicCreateObject dco = new DynamicCreateObject();
    Object student1 = null, team = null;
    // 属性-取值map
    Map<String, Object> fieldMap = new HashMap<String, Object>(16);
    fieldMap.put("name", "xiao ming");
    fieldMap.put("age", 27);
    // 创建一个名称为Student的类
    student1 = dco.addField("Student", fieldMap);
    Class<?> c = Class.forName("Student");
    // 创建Student类的对象
    Object s1 = c.newInstance();
    Object s2 = c.newInstance();
    // 创建对象s1赋值
    dco.setFieldValue(s1, "name", " xiao ming ");
    dco.setFieldValue(s2, "name", "xiao zhang");
    fieldMap.clear();
    List<Object> students = new ArrayList<Object>();
    students.add(s1);
    students.add(s2);
    fieldMap.put("students", students);
    // 创建一个名称为Team的类
    team = dco.addField("Team", fieldMap);
    Field[] fields = team.getClass().getDeclaredFields();
    for (Field field : fields) {
      System.out.println(field.getName() + "=" + dco.getFieldValue(team, field.getName()));
    }
  }
  /**
   * 为对象动态增加属性，并同时为属性赋值
   *
   * @param className 需要创建的java类的名称
   * @param fieldMap 字段-字段值的属性map，需要添加的属性
   * @return
   * @throws NotFoundException
   * @throws CannotCompileException
   */
  public Object addField(String className, Map<String, Object> fieldMap)
      throws NotFoundException, CannotCompileException, IllegalAccessException,
          InstantiationException {
    // 获取javassist类池
    ClassPool pool = ClassPool.getDefault();
    // 创建javassist类
    CtClass ctClass = pool.makeClass(className, pool.get(Object.class.getName()));

    // 为创建的类ctClass添加属性
    Iterator<Entry<String, Object>> it = fieldMap.entrySet().iterator();
    while (it.hasNext()) {
      // 遍历所有的属性
      Entry entry = it.next();
      String fieldName = (String) entry.getKey();
      Object fieldValue = entry.getValue();
      // 增加属性，这里仅仅是增加属性字段
      String fieldType = fieldValue.getClass().getName();
      CtField ctField = new CtField(pool.get(fieldType), fieldName, ctClass);
      ctField.setModifiers(Modifier.PUBLIC);
      ctClass.addField(ctField);
    }
    // 为创建的javassist类转换为java类
    Class<?> c = ctClass.toClass();
    // 为创建java对象
    Object newObject = c.newInstance();

    // 为创建的类newObject属性赋值
    it = fieldMap.entrySet().iterator();
    while (it.hasNext()) {
      // 遍历所有的属性
      Entry entry = it.next();
      String fieldName = (String) entry.getKey();
      Object fieldValue = entry.getValue();
      // 为属性赋值
      this.setFieldValue(newObject, fieldName, fieldValue);
    }
    return newObject;
  }
  /**
   * 获取对象属性赋值
   *
   * @param dObject
   * @param fieldName 字段别名
   * @return
   */
  public Object getFieldValue(Object dObject, String fieldName) {
    Object result = null;
    try {
      // 获取对象的属性域
      Field fu = dObject.getClass().getDeclaredField(fieldName);
      try {
        // 设置对象属性域的访问属性
        fu.setAccessible(true);
        // 获取对象属性域的属性值
        result = fu.get(dObject);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    return result;
  }
  /**
   * 给对象属性赋值
   *
   * @param dObject
   * @param fieldName
   * @param val
   */
  public void setFieldValue(Object dObject, String fieldName, Object val) {
    Object result = null;
    try {
      // 获取对象的属性域¬
      Field fu = dObject.getClass().getDeclaredField(fieldName);
      try {
        // 设置对象属性域的访问属性
        fu.setAccessible(true);
        // 设置对象属性域的属性值
        fu.set(dObject, val);
        // 获取对象属性域的属性值
        result = fu.get(dObject);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }
}
