package com.yunya.framework.common.exception.auth;

import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.exception.BaseException;

/**
 * 用户token异常
 *
 * @author ace
 * @date 2017/9/8
 */
public class UserTokenException extends BaseException {
  public UserTokenException(String message) {
    super(message, CommonConstants.EX_USER_INVALID_CODE);
  }
}
