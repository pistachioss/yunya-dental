package com.yunya.framework.common.utils;

import cn.hutool.core.text.StrFormatter;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.yunya.framework.common.constant.StringPool;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 字符串处理
 *
 * @author chow
 * @date 2020/7/18
 */
public class StringHelper extends StringUtils {

  /**
   * 对象转字符串
   *
   * @param obj
   * @return
   */
  public static String getObjectValue(Object obj) {
    return obj == null ? "" : obj.toString();
  }

  /** 空字符串 */
  private static final String NULLSTR = "";

  /** 下划线 */
  private static final char SEPARATOR = '_';

  /**
   * 获取参数，空值时返回默认值
   *
   * @param value defaultValue 要判断的value
   * @return value 返回值
   */
  public static <T> T nvl(T value, T defaultValue) {
    return isNotNull(value) ? value : defaultValue;
  }

  /**
   * * 判断一个Collection是否为空， 包含List，Set，Queue
   *
   * @param coll 要判断的Collection
   * @return true：为空 false：非空
   */
  public static boolean isEmpty(Collection<?> coll) {
    return isNull(coll) || coll.isEmpty();
  }

  /**
   * * 判断一个Collection是否非空，包含List，Set，Queue
   *
   * @param coll 要判断的Collection
   * @return true：非空 false：空
   */
  public static boolean isNotEmpty(Collection<?> coll) {
    return !isEmpty(coll);
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
   * * 判断一个对象数组是否非空
   *
   * @param objects 要判断的对象数组
   * @return true：非空 false：空
   */
  public static boolean isNotEmpty(Object[] objects) {
    return !isEmpty(objects);
  }

  /**
   * * 判断一个Map是否为空
   *
   * @param map 要判断的Map
   * @return true：为空 false：非空
   */
  public static boolean isEmpty(Map<?, ?> map) {
    return isNull(map) || map.isEmpty();
  }

  /**
   * * 判断一个Map是否为空
   *
   * @param map 要判断的Map
   * @return true：非空 false：空
   */
  public static boolean isNotEmpty(Map<?, ?> map) {
    return !isEmpty(map);
  }

  /**
   * * 判断一个字符串是否为空串
   *
   * @param str String
   * @return true：为空 false：非空
   */
  public static boolean isEmpty(String str) {
    return isNull(str) || NULLSTR.equals(str.trim()) || "null".equals(str);
  }

  /**
   * * 判断一个字符串是否为非空串
   *
   * @param str String
   * @return true：非空串 false：空串
   */
  public static boolean isNotEmpty(String str) {
    return !isEmpty(str);
  }

  /**
   * * 判断一个对象是否为空
   *
   * @param object Object
   * @return true：为空 false：非空
   */
  public static boolean isNull(Object object) {
    return object == null;
  }

  /**
   * * 判断一个对象是否非空
   *
   * @param object Object
   * @return true：非空 false：空
   */
  public static boolean isNotNull(Object object) {
    return !isNull(object);
  }

  /**
   * * 判断一个对象是否是数组类型（Java基本型别的数组）
   *
   * @param object 对象
   * @return true：是数组 false：不是数组
   */
  public static boolean isArray(Object object) {
    return isNotNull(object) && object.getClass().isArray();
  }

  /** 去空格 */
  public static String trim(String str) {
    return (str == null ? "" : str.trim());
  }

  /**
   * 截取字符串
   *
   * @param str 字符串
   * @param start 开始
   * @return 结果
   */
  public static String substring(final String str, int start) {
    if (str == null) {
      return NULLSTR;
    }

    if (start < 0) {
      start = str.length() + start;
    }

    if (start < 0) {
      start = 0;
    }
    if (start > str.length()) {
      return NULLSTR;
    }

    return str.substring(start);
  }

  /**
   * 截取字符串
   *
   * @param str 字符串
   * @param start 开始
   * @param end 结束
   * @return 结果
   */
  public static String substring(final String str, int start, int end) {
    if (str == null) {
      return NULLSTR;
    }

    if (end < 0) {
      end = str.length() + end;
    }
    if (start < 0) {
      start = str.length() + start;
    }

    if (end > str.length()) {
      end = str.length();
    }

    if (start > end) {
      return NULLSTR;
    }

    if (start < 0) {
      start = 0;
    }
    if (end < 0) {
      end = 0;
    }

    return str.substring(start, end);
  }

  /**
   * 格式化文本, {} 表示占位符<br>
   * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
   * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
   * 例：<br>
   * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
   * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
   * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
   *
   * @param template 文本模板，被替换的部分用 {} 表示
   * @param params 参数值
   * @return 格式化后的文本
   */
  public static String format(String template, Object... params) {
    return isEmpty(params) || isEmpty(template) ? template : StrFormatter.format(template, params);
  }

  /** 下划线转驼峰命名 */
  public static String toUnderScoreCase(String str) {
    if (str == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    // 前置字符是否大写
    boolean preCharIsUpperCase;
    // 当前字符是否大写
    boolean curreCharIsUpperCase;
    // 下一字符是否大写
    boolean nexteCharIsUpperCase = true;
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      preCharIsUpperCase = i > 0 && Character.isUpperCase(str.charAt(i - 1));

      curreCharIsUpperCase = Character.isUpperCase(c);

      if (i < (str.length() - 1)) {
        nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
      }

      if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
        sb.append(SEPARATOR);
      } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
        sb.append(SEPARATOR);
      }
      sb.append(Character.toLowerCase(c));
    }

    return sb.toString();
  }

  /**
   * 是否包含字符串
   *
   * @param str 验证字符串
   * @param strs 字符串组
   * @return 包含返回true
   */
  public static boolean inStringIgnoreCase(String str, String... strs) {
    return str != null
        && strs != null
        && Arrays.stream(strs).anyMatch(s -> str.equalsIgnoreCase(trim(s)));
  }

  /**
   * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。 例如：HELLO_WORLD->HelloWorld
   *
   * @param name 转换前的下划线大写方式命名的字符串
   * @return 转换后的驼峰式命名的字符串
   */
  public static String convertToCamelCase(String name) {
    String result;
    // 快速检查
    if (name == null || name.isEmpty()) {
      // 没必要转换
      return "";
    } else if (!name.contains("_")) {
      // 不含下划线，仅将首字母大写
      return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
    // 用下划线将原始字符串分割
    String[] camels = name.split("_");
    // 跳过原始字符串中开头、结尾的下换线或双重下划线
    result =
        Arrays.stream(camels)
            .filter(camel -> !camel.isEmpty())
            // 首字母大写
            .map(camel -> camel.substring(0, 1).toUpperCase() + camel.substring(1).toLowerCase())
            .collect(Collectors.joining());
    return result;
  }

  /** 驼峰式命名法 例如：user_name->userName */
  public static String toCamelCase(String s) {
    if (s == null) {
      return null;
    }
    s = s.toLowerCase();
    StringBuilder sb = new StringBuilder(s.length());
    boolean upperCase = false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);

      if (c == SEPARATOR) {
        upperCase = true;
      } else if (upperCase) {
        sb.append(Character.toUpperCase(c));
        upperCase = false;
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * 如果target不为空返回target,否则返回一个默认值
   *
   * @param target
   * @param defaultValue
   * @return
   */
  public static String assertNotNullOrEmpty(String target, String defaultValue) {
    return isNotEmpty(target) ? target : defaultValue;
  }

  /**
   * 截取endStr之前的内容，包含endStr
   *
   * @param s
   * @param endStr
   * @return String
   */
  public static String trim_before(String s, String endStr) {
    int index = s.indexOf(endStr);
    int len = endStr.length();
    return index < 0 ? s : s.substring(0, index + len);
  }

  /**
   * 截取beginStr之后的内容(包含beginStr)
   *
   * @param s
   * @param beginStr
   * @return String
   */
  public static String trim_end(String s, String beginStr) {
    int index = s.indexOf(beginStr);
    return index > 0 ? s.substring(index) : s;
  }

  /**
   * 截取endStr之前的内容(不包含endStr)
   *
   * @param s
   * @param endStr
   * @return String
   */
  public static String trim_before_exclu(String s, String endStr) {
    int index = s.indexOf(endStr);
    return index < 0 ? s : s.substring(0, index);
  }

  /**
   * 截取beginStr之后的内容(不包含beginStr)
   *
   * @param s
   * @param beginStr
   * @return String
   */
  public static String trim_end_exclu(String s, String beginStr) {
    int index = s.indexOf(beginStr);
    int len = beginStr.length();
    return index > 0 ? s.substring(index + len) : s;
  }

  /**
   * 返回beginStr和endStr之间的字符串(包含)
   *
   * @param s
   * @param beginStr
   * @param endStr
   * @return String
   */
  public static String trim_mid_inclu(String s, String beginStr, String endStr) {
    s = trim_before(s, endStr);
    return trim_end(s, beginStr);
  }

  /**
   * 返回beginStr和endStr之间的字符串(不包含)
   *
   * @param s
   * @param beginStr
   * @param endStr
   * @return String
   */
  public static String trim_mid_exclu(String s, String beginStr, String endStr) {
    s = trim_before_exclu(s, endStr);
    return trim_end_exclu(s, beginStr);
  }

  /**
   * 统计中指定子串在字符串出现的个数
   *
   * @param childStr 子串
   * @param sourceStr 源字符串
   * @return
   */
  public static int countChild(String childStr, String sourceStr) {
    // 定义一个count来存放字符串出现的次数
    int count = 0;
    // 调用String类的indexOf(String str)方法，返回第一个相同字符串出现的下标
    while (sourceStr.contains(childStr)) {
      // 如果存在相同字符串则次数加1
      count++;
      // 调用String类的substring(int beginIndex)方法，获得第一个相同字符出现后的字符串
      sourceStr = sourceStr.substring(sourceStr.indexOf(childStr) + childStr.length());
    }
    // 返回次数
    return count;
  }

  /**
   * 字符串分割成Int类型list
   * @param s 字符串
   * @param regex 分割符号
   * @return
   */
  public static List split2IntList(String s, String regex) {
    return split2List(s, regex, Integer.class);
  }

  public static List split2DecList(String s, String regex) {
    return split2List(s, regex, BigDecimal.class);
  }

  public static List split2List(String s, String regex) {
    return split2List(s, regex, String.class);
  }

  public static List split2List(String s, String regex, Class<?> clzz) {
    List list = new ArrayList<>();
    if (isEmpty(s)) {
      return list;
    }
    String[] array = split(s, regex);
    if (clzz == Integer.class) {
      for (String str : array) {
        list.add(Integer.parseInt(str));
      }
    } else if (clzz == BigDecimal.class) {
      for (String str : array) {
        list.add(new BigDecimal(str));
      }
    } else {
      for (String str : array) {
        list.add(str);
      }
    }
    return list;
  }

  /**
   * 截取分隔字符串之后的字符串，不包括分隔字符串<br>
   * 如果给定的字符串为空串（null或""），返回原字符串<br>
   * 如果分隔字符串为空串（null或""），则返回空串，如果分隔字符串未找到，返回空串
   *
   * <p>栗子：
   *
   * <pre>
   * StringHelper.subAfter(null, *)      = null
   * StringHelper.subAfter("", *)        = ""
   * StringHelper.subAfter(*, null)      = ""
   * StringHelper.subAfter("abc", "a")   = "bc"
   * StringHelper.subAfter("abcba", "b") = "cba"
   * StringHelper.subAfter("abc", "c")   = ""
   * StringHelper.subAfter("abc", "d")   = ""
   * StringHelper.subAfter("abc", "")    = "abc"
   * </pre>
   *
   * @param string 被查找的字符串
   * @param separator 分隔字符串（不包括）
   * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
   * @return 切割后的字符串
   * @since 3.1.1
   */
  public static String subAfter(
          CharSequence string, CharSequence separator, boolean isLastSeparator) {
    if (isEmpty(string)) {
      return null == string ? null : string.toString();
    }
    if (separator == null) {
      return StringPool.EMPTY;
    }
    final String str = string.toString();
    final String sep = separator.toString();
    final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
    if (pos == INDEX_NOT_FOUND) {
      return StringPool.EMPTY;
    }
    return str.substring(pos + separator.length());
  }

  public static String splitFirst(String str) {
    return StringUtils.isNotBlank(str) ? Lists.newArrayList(Splitter.on(",").split(str).iterator()).get(0) : null;
  }

  public static Integer defaultInt(Integer value) {
    return isNotNull(value) ? value : 0;
  }

  public static BigDecimal defaultBigDecimal(BigDecimal value) {
    return isNotNull(value) ? value : BigDecimal.ZERO;
  }

  /**
   * 判断是否存在任意一个元素为null
   *
   * @param objs
   * @return
   */
  public static boolean isAnyNull(Object...objs) {
    if (!ArrayUtils.isEmpty(objs)) {
      for (int i = 0; i < objs.length; ++i) {
        if (isNull(objs[i])) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean eqZero(BigDecimal value) {
    if (isNull(value)) {
      return false;
    }
    return eq(value, BigDecimal.ZERO);
  }

  /**
   * 大于
   *
   * @param val1
   * @param val2
   * @return
   */
  public static boolean lt(BigDecimal val1, BigDecimal val2) {
    if (isNull(val1) || isNull(val2)) {
      return false;
    }
    return cmp(val1, val2)<0;
  }

  /**
   * 小于等于
   *
   * @param val1
   * @param val2
   * @return
   */
  public static boolean le(BigDecimal val1, BigDecimal val2) {
    if (isNull(val1) || isNull(val2)) {
      return false;
    }
    return cmp(val1, val2)<=0;
  }

  /**
   * 大于
   *
   * @param val1
   * @param val2
   * @return
   */
  public static boolean gt(BigDecimal val1, BigDecimal val2) {
    if (isNull(val1) || isNull(val2)) {
      return false;
    }
    return cmp(val1, val2)>0;
  }

  /**
   * 大于等于
   *
   * @param val1
   * @param val2
   * @return
   */
  public static boolean ge(BigDecimal val1, BigDecimal val2) {
    if (isNull(val1) || isNull(val2)) {
      return false;
    }
    return cmp(val1, val2)>=0;
  }

  /**
   * 相等
   * @param val1
   * @param val2
   * @return
   */
  public static boolean eq(BigDecimal val1, BigDecimal val2) {
    if (isNull(val1) || isNull(val2)) {
      return false;
    }
    return cmp(val1, val2) == 0;
  }

  /**
   * 比较
   *
   * @param val1
   * @param val2
   * @return
   */
  public static Integer cmp(@NotNull BigDecimal val1, @NotNull BigDecimal val2) {
    return val1.compareTo(val2);
  }

  /**
   * 小于0
   * @param value
   * @return
   */
  public static boolean ltZero(BigDecimal value) {
    return lt(value, BigDecimal.ZERO);
  }

  /**
   * 小于等于0
   *
   * @param value
   * @return
   */
  public static boolean leZero(BigDecimal value) {
    return le(value, BigDecimal.ZERO);
  }

  /**
   * 大于0
   * @param value
   * @return
   */
  public static boolean gtZero(BigDecimal value) {
    return gt(value, BigDecimal.ZERO);
  }

  /**
   * 大于等于0
   *
   * @param value
   * @return
   */
  public static boolean geZero(BigDecimal value) {
    return ge(value, BigDecimal.ZERO);
  }

  /**
   * 判断一组对象是否全部为null
   *
   * @param objs
   * @return
   */
  public static boolean isAllNull(Object ...objs) {
    if (!ArrayUtils.isEmpty(objs)) {
      for (int i = 0; i < objs.length; ++i) {
        if (isNotNull(objs[i])) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
