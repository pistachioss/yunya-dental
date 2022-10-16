package com.yunya365.mini.service.impl;

import cn.hutool.core.convert.Convert;
import com.google.common.collect.Maps;
import com.yunya.feign.ivy_mini.domain.bo.WeChatSessionBO;
import com.yunya.feign.ivy_mini.domain.form.WeChatLoginForm;
import com.yunya.feign.ivy_mini.domain.form.WxUserInfoForm;
import com.yunya.feign.ivy_mini.domain.vo.AuthInfoVO;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.Token;
import com.yunya.framework.common.utils.JwtUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.WxFans;
import com.yunya365.mini.config.WxMiniProperties;
import com.yunya365.mini.enums.IvyMiniError;
import com.yunya365.mini.service.IWxFansService;
import com.yunya365.mini.service.WxApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.yunya.framework.common.constant.RedisConstants.*;
import static com.yunya.framework.common.constant.WxMiniAuthConstant.*;
import static com.yunya.framework.common.constant.WxMiniUri.*;
import static com.yunya365.mini.enums.LoginEnum.*;

/**
 * @description:
 * @author: xy
 * @date 2021/11/17 15:59
 **/
@Slf4j
@Service
public class LoginServiceImpl {

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private WxMiniProperties wxMiniProperties;
    @Resource
    private WxApi wxApi;
    @Resource
    private IWxFansService wxFansService;

    public AuthInfoVO wechatLogin(WeChatLoginForm form, HttpServletRequest request) {
        String url = String.format(AUTH_CODE2SESSION_URL, wxMiniProperties.getAppId(), wxMiniProperties.getAppSecret(), form.getCode());
        WeChatSessionBO sessionBO = wxApi.getWxResult(url, WeChatSessionBO.class);
        String openId = sessionBO.getOpenId();
        WxFans wxFans = wxFansService.getByOpenId(openId);
        WxUserInfoForm userInfo = form.getUserInfo();
        log.info("授权登录：{}", userInfo);
        if (Objects.isNull(wxFans)) {
            //保存或更新微信用户信息
            wxFansService.saveMiniAuth(wxFans, userInfo, sessionBO);
        }
        //登录
        AuthInfoVO authInfoVO = login(openId, request, wxFans);
        //存储session_key
        wxApi.storageSessionKey(openId, sessionBO.getSessionKey());
        authInfoVO.setUnionId(sessionBO.getUnionId());
        return authInfoVO;
    }

    private AuthInfoVO login(String openId, HttpServletRequest request, WxFans wxFans) {
        String authorization = request.getHeader("Authorization");
        String[] tokenKeys = new String[]{MINI_TOKEN, MINI_USER_ID};
        AuthInfoVO authVO;
        //是否登出
        if (StringUtils.isNotBlank(authorization)) {
            authVO = this.repeatLogin(() -> tokenKeys[0], authorization);
            this.expireToRedis(authVO, () -> tokenKeys, TOKEN_EXPIRE);
            return authVO;
        }
        if (Objects.isNull(wxFans)) {
            wxFans = wxFansService.getByOpenId(openId);
            if (Objects.isNull(wxFans)) {
                throw ClientServiceException.wrap(IvyMiniError.FANS_NOT_EXIST);
            }
        }
        //是否重复登录
        authVO = redisUtils.get(RedisConstants.buildLockCacheKey(tokenKeys[1], wxFans.getId()), AuthInfoVO.class);
        if (authVO != null) {
            //已登录，刷新时间
            authVO.setLastEnterDate(LocalDateTime.now());
            log.info("重复登录信息：{}", authVO);
        } else {
            //生成token等相关信息
            authVO = this.createAuthInfo(wxFans, TOKEN_EXPIRE);
            authVO.setOpenId(openId);
            log.info("初次登录token：{}", authVO.getToken());
        }
        //存redis
        storageToRedis(authVO, () -> tokenKeys, TOKEN_EXPIRE);
        return authVO;
    }

    public void logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isBlank(authorization)) {
            return;
        }
        String[] tokenKeys = new String[]{MINI_TOKEN, MINI_USER_ID};
        String tokenKey = RedisConstants.buildLockCacheKey(tokenKeys[0], authorization);
        AuthInfoVO authVo = redisUtils.get(tokenKey, AuthInfoVO.class);
        if (authVo != null) {
            redisUtils.delete(tokenKey);
            redisUtils.delete(RedisConstants.buildLockCacheKey(tokenKeys[1], authVo.getUserId()));
        }
    }

    /**
     * @param tokenSupp:
     * @param keySuffix:
     * @return AuthInfoVO
     */
    public AuthInfoVO repeatLogin(Supplier<String> tokenSupp, String keySuffix) {
        String tokenKey = RedisConstants.buildLockCacheKey(tokenSupp.get(), keySuffix);
        AuthInfoVO authVO = redisUtils.get(tokenKey, AuthInfoVO.class);
        if (authVO == null) {
            throw ClientServiceException.wrap(IvyMiniError.ACCOUNT_IS_LOGOUT);
        }
        log.info("用户重复登录");
        return authVO;
    }

    /**
     * 生成缓存对象
     *
     * @param wxFans       wxFans
     * @param expireMillis expireMillis
     * @return AuthInfoVO
     */
    public AuthInfoVO createAuthInfo(WxFans wxFans, long expireMillis) {
        //创建登录vo
        AuthInfoVO authInfoVO = buildLoginVo(wxFans);
        //生成token
        Token jwt = createJwt(authInfoVO, expireMillis);
        authInfoVO.setToken(MINI_AUTH.getValue() + jwt.getToken());
        return authInfoVO;
    }

    public AuthInfoVO buildLoginVo(WxFans wxFans) {
        AuthInfoVO authInfoVO = new AuthInfoVO();
        authInfoVO.setUserId(wxFans.getId());
        authInfoVO.setUserName(wxFans.getNickName());
        authInfoVO.setDisabled(false);
        authInfoVO.setLastEnterDate(LocalDateTime.now());
        authInfoVO.setHeadImgurl(wxFans.getHeadImgurl());
        return authInfoVO;
    }

    /**
     * 存入缓存
     *
     * @param authInfo    authInfo
     * @param tokenPrefix tokenPrefix
     * @param expire      expire
     */
    public void storageToRedis(AuthInfoVO authInfo, Supplier<String[]> tokenPrefix, long expire) {
        storageToRedis(authInfo, tokenPrefix, expire, redisUtils);
    }

    public static void storageToRedis(AuthInfoVO authInfo, Supplier<String[]> tokenPrefix, long expire, RedisUtils redisUtils) {
        String[] tokenKeys = tokenPrefix.get();
        String tokenKey = RedisConstants.buildLockCacheKey(tokenKeys[0], authInfo.getToken());
        String userIdKey = RedisConstants.buildLockCacheKey(tokenKeys[1], authInfo.getUserId());
        redisUtils.set(tokenKey, authInfo, expire, TimeUnit.SECONDS);
        redisUtils.set(userIdKey, authInfo, expire, TimeUnit.SECONDS);
    }

    public void expireToRedis(AuthInfoVO authInfo, Supplier<String[]> tokenPrefix, long expire) {
        String[] tokenKeys = tokenPrefix.get();
        String tokenKey = RedisConstants.buildLockCacheKey(tokenKeys[0], authInfo.getToken());
        String userIdKey = RedisConstants.buildLockCacheKey(tokenKeys[1], authInfo.getUserId());
        redisUtils.expire(tokenKey, expire, TimeUnit.SECONDS);
        redisUtils.expire(userIdKey, expire, TimeUnit.SECONDS);
    }

    public static Token createJwt(AuthInfoVO authInfoVO, long expireMillis) {
        return getToken(authInfoVO, expireMillis);
    }

    public static Token getToken(AuthInfoVO authInfoVO, long expireMillis) {
        Map<String, String> param = Maps.newHashMapWithExpectedSize(16);
        param.put(JWT_KEY_TOKEN_TYPE, BEARER_HEADER_KEY);
        param.put(JWT_KEY_USER_ID, Convert.toStr(authInfoVO.getUserId(), "0"));
        param.put(JWT_KEY_NAME, authInfoVO.getUserName());
        return JwtUtil.createJwt(param, expireMillis);
    }
}
