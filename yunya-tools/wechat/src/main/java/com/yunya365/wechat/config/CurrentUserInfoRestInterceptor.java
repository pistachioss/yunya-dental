package com.yunya365.wechat.config;

import com.yunya.framework.common.constant.WXConstant;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya365.wechat.enums.WeChatError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.yunya.framework.common.constant.WXConstant.*;

/**
 * 用户请求权限拦截器
 *
 * @author ace
 * @date 2017/9/10
 */
@Slf4j
public class CurrentUserInfoRestInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private WXConfig wxConfig;
    @Resource
    private RestTemplate restTemplate;
    @Resource
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
        log.info("前端请求路径：{}", request.getRequestURI());
        Object openId = request.getSession().getAttribute(GZH_SESSION_KEY);
        if (openId == null) {
            throw new ClientServiceException(WeChatError.WX_USER_NOT_REGISTER);
        }
        String openInfo = redisUtils.get(String.format(WXConstant.WECHAT_OPENID_KEY,openId));
        if (StringUtils.isBlank(openInfo)) {
            throw new ClientServiceException(WeChatError.WX_USER_NOT_REGISTER);
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
