package com.yunya.framework.common.constant;

/**
 * 简介: 常用操作状态码常量
 *
 * @author: chow
 * @date: 2020/7/12 11:17
 * @description:
 * @since: 1.0.0
 */
public class OperationCodeConstants {
  
  /** 数据名称已存在 */
  public static final Integer NAME_IS_OCCUPIED = 30001;
  /** 请求参数为空 */
  public static final Integer QUERY_RESULT_INVALID = 30002;
  /** 数据删除不被允许 */
  public static final Integer DELETE_NOT_ALLOW = 30003;
  /** 参数不允许为空 */
  public static final Integer PARAM_NOT_ALLOW_EMPTY = 30004;
  /** 对象更新失败 */
  public static final Integer OBJECT_EDIT_FAIL = 30005;
  /** 存在相同的数据 */
  public static final Integer SAME_DATA_EXIST = 30006;
  /** 非法参数 */
  public static final Integer PARAMETERS_IS_ILLEGAL = 30007;
  /** 查询结果为空 */
  public static final Integer RETURN_VALUE_ISNULL = 30008;
  /** 数据不存在 */
  public static final Integer DATA_NOT_EXIST = 30009;
  /** 时间格式转换异常 */
  public static final Integer DATA_TRANSFORMATION_EXIST = 30010;
}
