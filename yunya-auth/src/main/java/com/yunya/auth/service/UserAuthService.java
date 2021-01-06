package com.yunya.auth.service;

import com.yunya.auth.domain.UserAuthResponse;
import com.yunya.feign.system.form.JwtRequestFrom;

import javax.servlet.http.HttpServletRequest;

/**
 * 简单介绍: 用户jwt鉴权接口
 *
 * @author: chow
 * @date: 2020/7/5 15:32
 * @description:
 * @since: 1.0.0
 */
public interface UserAuthService {

  /**
   * 登陆（获取token）
   *
   * @param paramForm 参数封装
   * @return String
   */
  UserAuthResponse login(JwtRequestFrom paramForm, HttpServletRequest request) throws Exception;

  /**
   * 刷新token
   *
   * @param token token
   * @return String
   */
  UserAuthResponse refresh(String token) throws Exception;

  /**
   * token校验
   *
   * @param token token
   * @throws Exception
   */
  void validate(String token) throws Exception;

  /**
   * 退出登陆
   *
   * @param token token
   */
  void logout(String token,String deviceName);
}
