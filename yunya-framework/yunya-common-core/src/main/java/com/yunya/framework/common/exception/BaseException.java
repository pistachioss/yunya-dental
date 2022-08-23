package com.yunya.framework.common.exception;

/**
 * 基础异常类
 *
 * @author ace
 * @date 2017/9/8
 */
public class BaseException extends RuntimeException {

  private int status;

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public BaseException() {}

  public BaseException(String message, int status) {
    super(message);
    this.status = status;
  }

  public BaseException(String message) {
    super(message);
  }

  public BaseException(String message, Throwable cause) {
    super(message, cause);
  }

  public BaseException(Throwable cause) {
    super(cause);
  }

  public BaseException(final int code, Throwable cause) {
    super(cause);
    this.status = code;
  }

  public BaseException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public BaseException(int code, String message, Throwable cause) {
    super(message, cause);
    this.status = code;
  }

  public BaseException(final int code, final String format, Object... args) {
    super(String.format(format, args));
    this.status = code;
  }
}
