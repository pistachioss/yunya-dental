package com.yunya.auth.controller;

import com.yunya.auth.domain.UserAuthResponse;
import com.yunya.auth.form.JwtRequestFrom;
import com.yunya.auth.service.UserAuthService;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
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
   * @param authenticationRequest 参数封装
   * @return
   * @throws Exception
   */
  @PostMapping("/token")
  public ResponseResult createAuthenticationToken(
      @RequestBody @Validated JwtRequestFrom authenticationRequest) throws Exception {
    log.info(authenticationRequest.getUsername() + " require logging...");
    UserAuthResponse loginUser = userAuthService.login(authenticationRequest);
    return ResponseUtil.success(loginUser);
  }

  /**
   * 刷新token
   *
   * @param request 请求
   * @return
   * @throws Exception
   */
  @GetMapping("/refresh")
  public ResponseResult refreshAndGetAuthenticationToken(HttpServletRequest request)
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
  @PostMapping("/logout")
  public ResponseResult logout(HttpServletRequest request) {
    String token = request.getHeader(tokenHeader);
    userAuthService.logout(token);
    return ResponseUtil.success();
  }
}
