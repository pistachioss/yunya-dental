package com.yunya.framework.auth.interceptor;

import com.yunya.framework.auth.annotation.IgnoreUserToken;
import com.yunya.framework.auth.config.UserAuthConfig;
import com.yunya.framework.auth.utils.UserAuthUtil;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.auth.jwt.IJWTInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
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
@Component
public class UserAuthRestInterceptor extends HandlerInterceptorAdapter {

  /** 注入对象 */
  @Autowired private UserAuthUtil userAuthUtil;

  @Autowired private UserAuthConfig userAuthConfig;

  /**
   * 用户请求权限预处理
   *
   * @param request 请求
   * @param response 响应
   * @param handler 方法处理器
   * @return
   * @throws Exception
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    HandlerMethod handlerMethod = (HandlerMethod) handler;
    // 配置该注解，说明不进行用户拦截
    IgnoreUserToken annotation = handlerMethod.getBeanType().getAnnotation(IgnoreUserToken.class);
    if (annotation == null) {
      annotation = handlerMethod.getMethodAnnotation(IgnoreUserToken.class);
    }
    if (annotation != null) {
      return super.preHandle(request, response, handler);
    }
    String token = request.getHeader(userAuthConfig.getTokenHeader());
    if (StringUtils.isEmpty(token)) {
      if (request.getCookies() != null) {
        for (Cookie cookie : request.getCookies()) {
          if (cookie.getName().equals(userAuthConfig.getTokenHeader())) {
            token = cookie.getValue();
          }
        }
      }
    }
    // 对token进行鉴权，判断token是否过期，鉴权成功后将用户信息设置到线程局部变量
    IJWTInfo jwtInfo = userAuthUtil.getInfoFromToken(token);
    BaseContextHandler.setUsername(jwtInfo.getUniqueName());
    BaseContextHandler.setName(jwtInfo.getName());
    BaseContextHandler.setUserID(jwtInfo.getId());
    return super.preHandle(request, response, handler);
  }

  /**
   * 请求完成后移除线程局部变量
   *
   * @param request 请求
   * @param response 响应
   * @param handler 方法处理器
   * @param ex 异常
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
