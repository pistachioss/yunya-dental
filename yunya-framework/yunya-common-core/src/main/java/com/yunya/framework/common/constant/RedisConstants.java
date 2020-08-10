package com.yunya.framework.common.constant;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: redis通用常量
 *
 * @author: chow
 * @date: 2020/7/9 13:16
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class RedisConstants implements Serializable {

  /** ---------------------------用户信息缓存通用key------------------------------- */
  /** 当前用户ID */
  public static final String REDIS_KEY_USER_ID = "current_userId_";
  /** 当前用户登陆名 */
  public static final String REDIS_KEY_USERNAME = "current_userName_";
  /** 用户真实姓名 */
  public static final String REDIS_KEY_USER_NAME = "current_Name_";
  /** 当前用户token */
  public static final String REDIS_KEY_USER_TOKEN = "current_userToken_";

  /** ---------------------------组织（公司、门诊）信息缓存通用key------------------------ */
  /** 组织（公司、门诊）ID */
  public static final String REDIS_KEY_ORG_ID = "orgId_";
  /** 组织（公司、门诊）信息列表 */
  public static final String REDIS_KEY_ORG_LIST = "orgList_";

  /** ---------------------------*****缓存通用key------------------------------- */

  /** ----------------------- 电子病例----------------------------------------*/
  public static final String LOCK_DRAFT_APPLY_NS ="lock:draft:apply";
  public static final String LOCK_CHANGE_APPLY_NS ="lock:change:apply";
}
