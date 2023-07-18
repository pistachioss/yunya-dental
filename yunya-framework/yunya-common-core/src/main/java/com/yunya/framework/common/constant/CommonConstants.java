package com.yunya.framework.common.constant;

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

  public static final Integer ILLEGAL_PARAMETERS_CODE = 501;

  public static final Integer CONNECTION_REFUSED_CODE = 503;

  /** 请求头token */
  public static final String TOKEN_HEADER = "Authorization";

  /** 当前组织ID */
  public static final String CONTEXT_KEY_ORG_ID = "currentOrgId";
  /** 当前时间 */
  public static final String CONTEXT_KEY_TIME = "currentTime";
  /** 当前用户ID */
  public static final String CONTEXT_KEY_USER_ID = "currentUserId";
  /** 当前用户名 */
  public static final String CONTEXT_KEY_USERNAME = "currentUserName";
  /** 用户姓名 */
  public static final String CONTEXT_KEY_USER_NAME = "currentUser";
  /** 当前用户token */
  public static final String CONTEXT_KEY_USER_TOKEN = "currentUserToken";
  /** JWT中封装的 open_id */
  public static final String JWT_KEY_OPEN_ID = "open_id";
  public static final Integer INT_ONE = 1;
  public static final Integer INT_TWO = 2;
  public static final Integer INT_THREE = 3;
}
