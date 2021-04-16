package com.yunya365.wechat.config;

import com.alibaba.fastjson.*;
import com.yunya.framework.common.constant.*;
import com.yunya.framework.common.context.*;
import com.yunya.framework.common.exception.*;
import com.yunya.framework.common.exception.auth.*;
import com.yunya.framework.redis.util.*;
import com.yunya365.wechat.enums.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.springframework.web.client.*;
import org.springframework.web.method.*;
import org.springframework.web.servlet.handler.*;

import javax.annotation.*;
import javax.servlet.http.*;
import java.util.concurrent.*;

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
        String openId = request.getParameter("openId");
        if (StringUtils.isBlank(openId)) {
            throw new UserAuthException("您还没注册会员，请先注册！");
        }
        //微信用户信息放入缓存
        String userInfo = this.getAndCheckUserInfo(openId);
        redisUtils.set(openId, userInfo, 1, TimeUnit.DAYS);
        return super.preHandle(request, response, handler);
    }

    public String getAndCheckUserInfo(String openId) {
        String redisKey = String.format(WXConstant.ACCESS_TOKEN_KEY, wxConfig.getAppId());
        String accessToken = redisUtils.get(redisKey);
        String url = String.format(WXConstant.WX_USER_INFO_URL, accessToken, openId);
        String resultStr = restTemplate.getForObject(url, String.class);
        log.info("获取用户信息结果，{}", resultStr);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        this.checkWxResult(jsonObject);
        return resultStr;
    }

    private void checkWxResult(JSONObject jsonObject) {
        if (jsonObject == null) {
            throw new ClientServiceException(WeChatError.USER_NOT_FOLLOW);
        }
        Integer errCode = jsonObject.getInteger("errcode");
        if (errCode != null) {
            if (errCode == 40003) {
                throw new BaseException(WeChatError.USER_NOT_FOLLOW.getMessage(), errCode);
            }
            String errMsg = jsonObject.getString("errmsg");
            throw new BaseException(errMsg, errCode);
        }
        boolean subscribe = jsonObject.getBooleanValue("subscribe");
        //是否关注
        if (!subscribe) {
            throw new ClientServiceException(WeChatError.USER_NOT_FOLLOW);
        }
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
