package com.yunya.modules.emr.interceptor;

import com.alibaba.fastjson.JSON;
import com.yunya.feign.system.vo.UserInfo;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author bruce
 * @date 2020/7/28
 */
public class WebHandlertInterceptor extends HandlerInterceptorAdapter {
//    @Resource
//    private UserAuthConfig userAuthConfig;

    @Resource(name = "stringRedisTemplate")
    private ValueOperations<String, String> valueOperations;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String redisToken = request.getHeader("Authorization");

        // 获取redis中存储的用户信息
        String userInfoStr = valueOperations.get(RedisConstants.REDIS_KEY_USER_TOKEN + redisToken);
        UserInfo userInfo = JSON.parseObject(userInfoStr, UserInfo.class);
        BaseContextHandler.setName(userInfo.getName());
        BaseContextHandler.setUserID(userInfo.getId());
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        BaseContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
