package com.yunya365.mini.config;

import com.yunya.feign.ivy_mini.domain.vo.AuthInfoVO;
import com.yunya.feign.system.vo.UserInfo;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.constant.*;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.exception.auth.UserAuthException;
import com.yunya.framework.common.model.Token;
import com.yunya.framework.common.utils.ClassUtil;
import com.yunya.framework.common.utils.JwtUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya365.mini.service.impl.LoginServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.yunya.framework.common.constant.RedisConstants.*;
import static com.yunya.framework.common.constant.WxMiniAuthConstant.*;
import static com.yunya365.mini.enums.IvyMiniError.*;
import static com.yunya365.mini.enums.LoginEnum.*;

/**
 * @description:
 * @author: xy
 **/
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //不用登录
        if (ClassUtil.getAnnotation(handlerMethod, IgnoreUserToken.class) != null) {
            return true;
        }
        // 放行自定义异常拦截Controller
        if (handlerMethod.getBeanType().getSuperclass().equals(BasicErrorController.class)) {
            return true;
        }
        String token = request.getHeader(CommonConstants.TOKEN_HEADER);
        String requestURI = request.getRequestURI();
        log.info("请求url:{}，token: {}", requestURI, token);
        if  (StringUtils.isNotBlank(requestURI) && requestURI.startsWith("/back/manager")) {
            pcInterceptor(token);
            return true;
        }
        //获取redis token前缀 是否登录
        String[] tokenArr = getTokenKeys(token);
        AuthInfoVO authInfo = redisUtils.get(RedisConstants.buildLockCacheKey(tokenArr[0], token), AuthInfoVO.class);
        if (authInfo == null) {
            throw ClientServiceException.wrap(JWT_NOT_LOGIN);
        }
        // 解析或重置jwt
        this.parseOrResetJwt(token, authInfo);
        //设置用户信息
        BaseContextHandler.setName(authInfo.getUserName());
        BaseContextHandler.setUserID(String.valueOf(authInfo.getUserId()));
        BaseContextHandler.setAuthorization(authInfo.getToken());
        BaseContextHandler.setOpenId(authInfo.getOpenId());
        //刷新过期时间
        this.expireToRedis(authInfo, tokenArr);
        return true;
    }

    private String[] getTokenKeys(String token) {
        if (StringUtils.isBlank(token)) {
            throw ClientServiceException.wrap(JWT_ILLEGAL_ARGUMENT);
        }
        String[] tokenArr;
        if (token.startsWith(BusinessConstants.MINI_TOKEN_PREFIX)) {
            tokenArr = Stream.of(MINI_TOKEN, MINI_USER_ID).toArray(String[]::new);
        } else {
            throw ClientServiceException.wrap(JWT_SIGNATURE);
        }
        if (StringUtils.isBlank(token)) {
            throw ClientServiceException.wrap(JWT_ILLEGAL_ARGUMENT);
        }
        return tokenArr;
    }

    private boolean whetherExpire(LocalDateTime lastEnterDate) {
        LocalDateTime now = LocalDateTime.now();
        long dayGap = Duration.between(now, lastEnterDate).toDays();
        return dayGap > TOKEN_GAP;
    }

    private void expireToRedis(AuthInfoVO authInfo, String[] tokenArr) {
        authInfo.setLastEnterDate(LocalDateTime.now());
        LoginServiceImpl.storageToRedis(authInfo, () -> tokenArr, TOKEN_EXPIRE, redisUtils);
    }

    private void deleteToken(AuthInfoVO authInfo, String[] tokenArr) {
        String tokenKey = RedisConstants.buildLockCacheKey(tokenArr[0], authInfo.getToken());
        String userIdKey = RedisConstants.buildLockCacheKey(tokenArr[1], authInfo.getUserId());
        redisUtils.delete(tokenKey);
        redisUtils.delete(userIdKey);
    }

    private void parseOrResetJwt(String token, AuthInfoVO authInfo) {
        try {
            JwtUtil.getClaims(token, token.lastIndexOf(" ") + 1, 60L);
        } catch (ExpiredJwtException ex) {
            //删除redis旧的登录信息
            deleteToken(authInfo, getTokenKeys(authInfo.getToken()));
            if (whetherExpire(authInfo.getLastEnterDate())) {
                log.error("token=[{}], 过期", token, ex);
                // 过期
                throw ClientServiceException.wrap(JWT_NOT_LOGIN, ex);
            } else {
                //重新生成token
                Token jwt = createJwt(authInfo, TOKEN_EXPIRE);
                authInfo.setToken(MINI_AUTH + jwt.getToken());
                log.info("用户：[{}]，续期，token=[{}], jwt.getToken()", authInfo.getUserName(), token);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContextHandler.remove();
    }

    public static Token createJwt(AuthInfoVO authInfoVO, long expireMillis) {
        return LoginServiceImpl.getToken(authInfoVO, expireMillis);
    }

    private void pcInterceptor(String token) {
        UserInfo userInfo = redisUtils.get(RedisConstants.REDIS_KEY_USER_TOKEN + token, UserInfo.class);
        if (null == userInfo) {
            throw new UserAuthException("您还没有登录，请先登录！");
        }
        BaseContextHandler.setAuthorization(token);
        BaseContextHandler.setUsername(userInfo.getUsername());
        BaseContextHandler.setName(userInfo.getName());
        BaseContextHandler.setUserID(userInfo.getId());
        BaseContextHandler.setOrgId(userInfo.getCurrentOrgId() == null ? null : userInfo.getCurrentOrgId().toString());
    }
}
