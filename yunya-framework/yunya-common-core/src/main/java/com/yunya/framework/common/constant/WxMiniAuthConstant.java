package com.yunya.framework.common.constant;

/**
 * 微信小程序登录数据
 *
 * @author xiangyang
 */
public class WxMiniAuthConstant {
  /** token过期时间 */
  public static final long TOKEN_EXPIRE = 24 * 3600L * 30;
  /** token过期天数（天）*/
  public static final Integer TOKEN_GAP = 30;
  /**
   * JWT token 签名
   *
   * <p>签名密钥长度至少32位!!!
   */
  public static final String JWT_SIGN_KEY = "mini_program_is_a_fantastic_project!";
  /** Client信息认证请求头前缀 */
  public static final String BASIC_HEADER_PREFIX = "Basic";
  /** User信息 认证请求头前缀 */
  public static final String BEARER_HEADER_PREFIX = "Bearer";
  /** JWT中封装的 token 类型 */
  public static final String JWT_KEY_TOKEN_TYPE = "token_type";
  /** JWT中封装的 用户id */
  public static final String JWT_KEY_USER_ID = "user_id";
  /** JWT中封装的 用户名称 */
  public static final String JWT_KEY_NAME = "name";
  /** User信息 认证请求头 */
  public static final String BEARER_HEADER_KEY = "token";
  /** 微信小程序accessToken刷新时间间隔 */
  public static final Integer ACCESS_TOKEN_EXPIRE_GAP = 7140;
}
