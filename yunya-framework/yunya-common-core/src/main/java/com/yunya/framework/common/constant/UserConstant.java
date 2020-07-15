package com.yunya.framework.common.constant;

/**
 * 用户常量
 *
 * @author wanghaobin
 * @create 2017-06-14 8:36
 */
public class UserConstant {

  /** 密码加盐 */
  public static int PW_ENCODER_SALT = 12;
  /** 用户初始化密码 */
  public static final CharSequence DEFAULT_USER_PASSWORD = "123456";
  /** jwt用户ID */
  public static final String JWT_KEY_USER_ID = "userId";
  /** jwt用户名 */
  public static final String JWT_KEY_NAME = "name";
}
