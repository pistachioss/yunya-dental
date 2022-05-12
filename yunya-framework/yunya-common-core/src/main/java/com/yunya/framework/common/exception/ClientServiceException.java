package com.yunya.framework.common.exception;

import com.yunya.framework.common.model.RestError;

/**
 * 简介: 客户端服务异常
 *
 * @author: chow
 * @date: 2020/7/12 10:24
 * @description:
 * @since: 1.0.0
 */
public class ClientServiceException extends BaseException {

  /**
   * 客户端异常信息
   *
   * @param msg 提示信息
   * @param exceptionCode 状态码
   */
  public ClientServiceException(String msg, Integer exceptionCode) {
    super(msg, exceptionCode);
  }

  public ClientServiceException(Throwable cause) {
    super(cause);
  }

  public ClientServiceException(int code, String message, Throwable cause) {
    super(code, message, cause);
  }

  public ClientServiceException(int code, String message, Object... args) {
    super(code, message, args);
  }

  public ClientServiceException(RestError restError, Object...param) {
    super(String.format(restError.getMessage(), param), restError.getCode());
  }

  public static ClientServiceException wrap(RestError ex) {
     return new ClientServiceException(ex.getMessage(), ex.getCode());
  }

  public static ClientServiceException wrap(RestError ex, Object... args) {
    return new ClientServiceException(ex.getCode(), ex.getMessage(), args);
  }

  public static ClientServiceException wrap(RestError ex, Throwable cause) {
    return new ClientServiceException(ex.getCode(), ex.getMessage(), cause);
  }

  public static ClientServiceException wrap(int code, String message, Object... args) {
    return new ClientServiceException(code, message, args);
  }

}
