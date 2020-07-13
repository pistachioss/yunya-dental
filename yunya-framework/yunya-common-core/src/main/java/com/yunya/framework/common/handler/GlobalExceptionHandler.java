package com.yunya.framework.common.handler;

import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.exception.auth.ClientTokenException;
import com.yunya.framework.common.exception.auth.UserAuthException;
import com.yunya.framework.common.exception.auth.UserTokenException;
import com.yunya.framework.common.model.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理
 *
 * @author ace
 * @date 2017/9/8
 */
@Slf4j
@ControllerAdvice("com.yunya.framework")
@ResponseBody
public class GlobalExceptionHandler {

  @ExceptionHandler(ClientTokenException.class)
  public BaseResponse clientTokenExceptionHandler(
      HttpServletResponse response, ClientTokenException ex) {
    response.setStatus(403);
    log.error(ex.getMessage(), ex);
    return new BaseResponse(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler(UserTokenException.class)
  public BaseResponse userTokenExceptionHandler(
      HttpServletResponse response, UserTokenException ex) {
    response.setStatus(200);
    log.error(ex.getMessage(), ex);
    return new BaseResponse(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler(UserAuthException.class)
  public BaseResponse userInvalidExceptionHandler(
      HttpServletResponse response, UserAuthException ex) {
    response.setStatus(200);
    log.error(ex.getMessage(), ex);
    return new BaseResponse(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler(BaseException.class)
  public BaseResponse baseExceptionHandler(HttpServletResponse response, BaseException ex) {
    log.error(ex.getMessage(), ex);
    response.setStatus(500);
    return new BaseResponse(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public BaseResponse otherExceptionHandler(HttpServletResponse response, Exception ex) {
    response.setStatus(500);
    log.error(ex.getMessage(), ex);
    return new BaseResponse(CommonConstants.EX_OTHER_CODE, ex.getMessage());
  }
}
