package com.yunya.framework.common.handler;

import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.exception.auth.ClientTokenException;
import com.yunya.framework.common.exception.auth.UserAuthException;
import com.yunya.framework.common.exception.auth.UserTokenException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理
 *
 * @author ace
 * @date 2017/9/8
 */
@Slf4j
@ControllerAdvice("com.yunya")
@ResponseBody
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ClientTokenException.class)
  public ResponseResult clientTokenExceptionHandler(
      HttpServletResponse response, ClientTokenException ex) {
    response.setStatus(403);
    log.error(ex.getMessage(), ex);
    return ResponseUtil.fail(ex.getStatus(), ex.getMessage(), null);
  }

  @ExceptionHandler(UserTokenException.class)
  public ResponseResult userTokenExceptionHandler(
      HttpServletResponse response, UserTokenException ex) {
    response.setStatus(200);
    log.error(ex.getMessage(), ex);
    return ResponseUtil.fail(ex.getStatus(), ex.getMessage(), null);
  }

  @ExceptionHandler(UserAuthException.class)
  public ResponseResult userInvalidExceptionHandler(
      HttpServletResponse response, UserAuthException ex) {
    response.setStatus(200);
    log.error(ex.getMessage(), ex);
    return ResponseUtil.fail(ex.getStatus(), ex.getMessage(), null);
  }

  @ExceptionHandler(ClientServiceException.class)
  public ResponseResult clientServiceExceptionHandler(
      HttpServletResponse response, ClientServiceException ex) {
    response.setStatus(200);
    log.error(ex.getMessage(), ex);
    return ResponseUtil.fail(ex.getStatus(), ex.getMessage(), null);
  }

  @ExceptionHandler(BaseException.class)
  public ResponseResult baseExceptionHandler(HttpServletResponse response, BaseException ex) {
    response.setStatus(200);
    log.error(ex.getMessage(), ex);
    return ResponseUtil.fail(ex.getStatus(), ex.getMessage(), null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseResult otherExceptionHandler(HttpServletResponse response, Exception ex) {
    response.setStatus(500);
    log.error(ex.getMessage(), ex);
    return ResponseUtil.fail(CommonConstants.EX_OTHER_CODE, ex.getMessage(), null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseResult methodArgumentNotValidHandler(
      MethodArgumentNotValidException e, HttpServletRequest request) {
    String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    logger.error("error in \nurl :{} \nmsg:{}", request.getRequestURI(), message);
    return ResponseUtil.fail(CommonConstants.EX_OTHER_CODE, message, null);
  }
}
