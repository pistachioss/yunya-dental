package com.yunya.framework.common.constant;

import java.util.regex.Pattern;

/**
 * 公共常量类
 *
 * @author ace
 * @date 2017/8/29
 */
public class CommonConstants {

  /** UTF-8 字符集 */
  public static final String UTF8 = "UTF-8";

  public static final Integer EX_TOKEN_ERROR_CODE = 40101;
  /** 用户token异常 */
  public static final Integer EX_USER_INVALID_CODE = 40101;

  public static final Integer EX_USER_PASS_INVALID_CODE = 40001;

  /** 客户端token异常 */
  public static final Integer EX_CLIENT_INVALID_CODE = 40131;

  public static final Integer EX_CLIENT_FORBIDDEN_CODE = 40331;

  public static final Integer TOKEN_FORBIDDEN_CODE = 40301;

  public static final Integer EX_OTHER_CODE = 500;

  /** 当前组织ID */
  public static final String CONTEXT_KEY_ORG_ID = "currentOrgId";
  /** 当前用户ID */
  public static final String CONTEXT_KEY_USER_ID = "currentUserId";
  /** 当前用户名 */
  public static final String CONTEXT_KEY_USERNAME = "currentUserName";
  /** 用户姓名 */
  public static final String CONTEXT_KEY_USER_NAME = "currentUser";
  /** 当前用户token */
  public static final String CONTEXT_KEY_USER_TOKEN = "currentUserToken";

  /** 社会信用统一码 */
  public static final Pattern CREDIT_PATTERN = Pattern.compile("^[0-9A-Z]{18}$");

  public static final String CLINIC_BUSINESS_PATTER = "HH:mm";
}
