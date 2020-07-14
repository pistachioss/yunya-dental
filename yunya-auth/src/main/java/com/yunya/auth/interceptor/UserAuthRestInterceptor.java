package com.yunya.auth.interceptor;

import com.yunya.auth.configuration.UserConfiguration;
import com.yunya.auth.utils.JwtTokenUtil;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.jwt.IJWTInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户请求权限拦截器
 *
 * @author ace
 * @date 2017/9/10
 */
public class UserAuthRestInterceptor extends HandlerInterceptorAdapter {

  /** 注入对象 */
  private final Logger logger = LoggerFactory.getLogger(UserAuthRestInterceptor.class);

  @Autowired private JwtTokenUtil jwtTokenUtil;

  @Autowired private UserConfiguration userConfiguration;

  /**
   * 请求权限预处理
   *
   * @param request 请求
   * @param response 响应
   * @param handler 处理器
   * @return
   * @throws Exception
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    // 配置该注解，说明不进行用户拦截
    String token = request.getHeader(userConfiguration.getUserTokenHeader());
    if (StringUtils.isEmpty(token)) {
      if (request.getCookies() != null) {
        for (Cookie cookie : request.getCookies()) {
          if (cookie.getName().equals(userConfiguration.getUserTokenHeader())) {
            token = cookie.getValue();
          }
        }
      }
    }
    // 校验token获取JWT信息
    IJWTInfo infoFromToken = jwtTokenUtil.getInfoFromToken(token);
    BaseContextHandler.setUsername(infoFromToken.getUniqueName());
    BaseContextHandler.setName(infoFromToken.getName());
    BaseContextHandler.setUserID(infoFromToken.getId());
    return super.preHandle(request, response, handler);
  }

  /**
   * 请求完成后处理
   *
   * @param request 请求
   * @param response 响应
   * @param handler 处理器
   * @param ex 异常信息
   * @throws Exception
   */
  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    BaseContextHandler.remove();
    super.afterCompletion(request, response, handler, ex);
  }
}
