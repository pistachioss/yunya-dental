package com.yunya.clinic.base.constant;

import java.util.regex.Pattern;

/**
 * 简单介绍:</br> 公司端全局常量
 *
 * @author: chow
 * @date: 2020/5/28 17:29
 * @description:
 * @since: 1.0.0
 */
public class ClinicBaseConstant {

  public static final Pattern CREDIT_PATTERN = Pattern.compile("^[0-9A-Z]{18}$");

  public static final String CLINIC_BUSINESS_PATTER = "HH:mm";

  /** 数据名称已存在 */
  public static final Integer NAME_IS_OCCUPIED = 30001;

  /** 请求参数为空 */
  public static final Integer QUERY_RESULT_INVALID = 30002;

}
