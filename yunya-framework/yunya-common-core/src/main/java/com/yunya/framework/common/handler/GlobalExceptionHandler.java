package com.yunya.framework.common.handler;

import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.exception.auth.ClientInvalidException;
import com.yunya.framework.common.exception.auth.ClientTokenException;
import com.yunya.framework.common.exception.auth.UserAuthException;
import com.yunya.framework.common.exception.auth.UserTokenException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.ConnectException;

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
    return ResponseUtil.fail(CommonConstants.EX_OTHER_CODE, "服务器好像出故障了！请联系管理员", null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseResult methodArgumentNotValidHandler(
      MethodArgumentNotValidException e, HttpServletRequest request) {
    String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    logger.error("error in \nurl :{} \nmsg:{}", request.getRequestURI(), message);
    return ResponseUtil.fail(CommonConstants.EX_OTHER_CODE, message, null);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseResult httpMessageNotReadableExceptionHandler(
      HttpMessageNotReadableException exp, HttpServletRequest request) {
    logger.error("error in \n url:{} \nmsg:{}", request.getRequestURL(), exp.getCause());
    return ResponseUtil.fail(CommonConstants.ILLEGAL_PARAMETERS_CODE, "非法请求参数类型", null);
  }

  @ExceptionHandler(HttpMessageConversionException.class)
  public ResponseResult httpMessageConversionExceptionHandler(
      HttpMessageConversionException exp, HttpServletRequest request) {
    logger.error("error in \n url:{} \n msg:{}", request.getRequestURL(), exp.getCause());
    return ResponseUtil.fail(CommonConstants.ILLEGAL_PARAMETERS_CODE, "类型转换异常", null);
  }

  @ExceptionHandler(ClientInvalidException.class)
  public ResponseResult httpClientInvalidExceptionHandler(
      ClientInvalidException exp, HttpServletRequest request) {
    logger.error("error in \n url:{} \n msg:{}", request.getRequestURL(), exp.getCause());
    return ResponseUtil.fail(CommonConstants.ILLEGAL_PARAMETERS_CODE, "服务验证异常！", null);
  }

  @ExceptionHandler(ConnectException.class)
  public ResponseResult connectException(ConnectException e, HttpServletRequest request) {
    logger.error("error in \n url:{} \n msg:{}",request.getRequestURL(),e.getCause());
    return ResponseUtil.fail(CommonConstants.CONNECTION_REFUSED_CODE,"服务器繁忙！请稍后重试",null);
  }
}
