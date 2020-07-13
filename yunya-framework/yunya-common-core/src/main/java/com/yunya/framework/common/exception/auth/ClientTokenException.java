package com.yunya.framework.common.exception.auth;

import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.exception.BaseException;

/**
 * 客户端token异常
 *
 * @author ace
 * @date 2017/9/10
 */
public class ClientTokenException extends BaseException {
  public ClientTokenException(String message) {
    super(message, CommonConstants.EX_CLIENT_INVALID_CODE);
  }
}
