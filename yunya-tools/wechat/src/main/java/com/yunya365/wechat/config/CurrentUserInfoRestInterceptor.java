package com.yunya365.wechat.config;

import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.auth.UserAuthException;
import com.yunya.framework.redis.util.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户请求权限拦截器
 *
 * @author ace
 * @date 2017/9/10
 */
public class CurrentUserInfoRestInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 用户请求权限预处理
     *
     * @param request  请求
     * @param response 响应
     * @param handler  方法处理器
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }
        String openId = request.getParameter("openId");
        if (StringUtils.isBlank(openId)) {
            throw new UserAuthException("您还没注册会员，请先注册！");
        }
        return super.preHandle(request, response, handler);
    }

    /**
     * 请求完成后移除线程局部变量
     *
     * @param request  请求
     * @param response 响应
     * @param handler  方法处理器
     * @param ex       异常
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
