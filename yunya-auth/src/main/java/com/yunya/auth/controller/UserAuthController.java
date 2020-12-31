package com.yunya.auth.controller;

import com.yunya.auth.domain.UserAuthResponse;
import com.yunya.auth.service.UserAuthService;
import com.yunya.feign.system.form.JwtRequestFrom;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户鉴权控制器
 *
 * @author chow
 */
@Api("用户鉴权")
@RestController
@RequestMapping("jwt")
@Slf4j
public class UserAuthController {

  /** 注入对象 */
  @Value("${jwt.token-header}")
  private String tokenHeader;

  @Autowired private UserAuthService userAuthService;

  /**
   * 用户登陆
   *
   * @param paramForm 参数封装
   * @return
   * @throws Exception
   */
  @ApiOperation("登陆")
  @PostMapping("/token")
  public ResponseResult<UserAuthResponse> createAuthenticationToken(
      @RequestBody @Validated JwtRequestFrom paramForm, HttpServletRequest request) throws Exception {
    log.info(paramForm.getUsername() + " require logging...");
    UserAuthResponse loginUser = userAuthService.login(paramForm,request);
    return ResponseUtil.success(loginUser);
  }

  /**
   * 刷新token
   *
   * @param request 请求
   * @return
   * @throws Exception
   */
  @ApiOperation("token刷新")
  @GetMapping("/refresh")
  public ResponseResult<UserAuthResponse> refreshAndGetAuthenticationToken(HttpServletRequest request)
      throws Exception {
    // 获取请求头携带的token
    String token = request.getHeader(tokenHeader);
    UserAuthResponse userAuthResponse = userAuthService.refresh(token);
    return ResponseUtil.success(userAuthResponse);
  }

  /**
   * token校验
   *
   * @param token token
   * @return
   * @throws Exception
   */
  @ApiOperation("token校验")
  @GetMapping("/verify")
  public ResponseResult verify(String token) throws Exception {
    userAuthService.validate(token);
    return ResponseUtil.success();
  }

  /**
   * 退出登陆
   *
   * @param request 请求
   * @return
   */
  @ApiOperation("登出")
  @PostMapping("/logout")
  public ResponseResult logout(HttpServletRequest request) {
    String token = request.getHeader(tokenHeader);
    userAuthService.logout(token);
    return ResponseUtil.success();
  }
}
