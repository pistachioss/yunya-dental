package com.yunya.framework.common.exception.auth;

import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.exception.BaseException;

/**
 * 用户鉴权异常
 *
 * @author ace
 * @date 2017/9/8
 */
public class UserAuthException extends BaseException {

  public UserAuthException(String message) {
    super(message, CommonConstants.EX_USER_PASS_INVALID_CODE);
  }
}
