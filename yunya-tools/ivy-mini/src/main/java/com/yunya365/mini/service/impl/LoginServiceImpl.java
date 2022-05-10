package com.yunya365.mini.service.impl;

import com.yunya.feign.ivy_mini.domain.bo.LoginUserBO;
import com.yunya.feign.ivy_mini.domain.form.WeChatLoginForm;
import com.yunya.feign.ivy_mini.domain.vo.AuthInfoVO;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.Token;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya365.mini.enums.IvyMiniError;
import com.yunya365.mini.enums.MemberStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.yunya.framework.common.constant.BusinessConstants.*;

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

    public AuthInfoVO login(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String[] tokenKeys = null; //getTokenKeys();
        AuthInfoVO authVO;
        //是否登出
        if (StringUtils.isNotBlank(authorization)) {
            authVO = this.repeatLogin(() -> tokenKeys[0], authorization);
            this.expireToRedis(authVO, () -> tokenKeys, TOKEN_EXPIRE);
            return authVO;
        }
        LoginUserBO adminUser = null;//getLoginUser(loginForm.getUserName(), loginForm.getPassword());
        //用户被禁用
        if (Objects.equals(adminUser.getMemberStatus(), MemberStatusEnum.DISABLE.getCode())) {
            return buildLoginVo(adminUser);
        }
        //是否重复登录
        authVO = redisUtils.get(RedisConstants.buildLockCacheKey(tokenKeys[1], adminUser.getUserId()), AuthInfoVO.class);
        if (authVO != null) {
            //授权和普通登录 合并登录信息
            return mergeAuthInfo(authVO, adminUser, tokenKeys);
        }
        //生成token等相关信息
        authVO = this.createAuthInfo(adminUser, TOKEN_EXPIRE);
        log.info("初次登录token：{}", authVO.getToken());
        if (authVO.getDisabled()) {
            return authVO;
        }
        //存redis
        this.storageToRedis(authVO, () -> tokenKeys, TOKEN_EXPIRE);
        return authVO;
    }

    public void logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isBlank(authorization)) {
            return;
        }
        String[] tokenKeys = null;//getTokenKeys();
        String tokenKey = RedisConstants.buildLockCacheKey(tokenKeys[0], authorization);
        AuthInfoVO authVo = redisUtils.get(tokenKey, AuthInfoVO.class);
        if (authVo != null) {
            redisUtils.delete(tokenKey);
            redisUtils.delete(RedisConstants.buildLockCacheKey(tokenKeys[1], authVo.getUserId()));
        }
    }



    public AuthInfoVO wechatLogin(WeChatLoginForm form, HttpServletRequest request) {
        return null;
    }

//    /**
//     * 获取登录人信息
//     *
//     * @param userName userName
//     * @param password password
//     * @return LoginUserBo
//     */
//    abstract LoginUserBO getLoginUser(String userName, String password);
//
//    /**
//     * 获取登录tokenKeys数组 [token, userId]
//     *
//     * @return String[]
//     */
//    abstract String[] getTokenKeys();

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
     * @param adminUser    adminUser
     * @param expireMillis expireMillis
     * @return AuthInfoVO
     */
    public AuthInfoVO createAuthInfo(LoginUserBO adminUser, long expireMillis) {
        //创建登录vo
        AuthInfoVO authInfoVO = buildLoginVo(adminUser);
        authInfoVO.setLoginType(adminUser.getLoginType().getCode());
        authInfoVO.setLastEnterDate(LocalDateTime.now());
        if (!authInfoVO.getDisabled()) {
            //生成token
            Token jwt = null; //JwtUtil.createJwt(authInfoVO, expireMillis);
            authInfoVO.setToken(adminUser.getLoginType().getValue() + jwt.getToken());
        }
        return authInfoVO;
    }

    public AuthInfoVO buildLoginVo(LoginUserBO adminUser) {
        AuthInfoVO authInfoVO = new AuthInfoVO();
        authInfoVO.setUserId(adminUser.getUserId());
        authInfoVO.setUserName(adminUser.getUserName());
        authInfoVO.setDisabled(false);
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

    private AuthInfoVO mergeAuthInfo(AuthInfoVO authVO, LoginUserBO adminUser, String[] tokenKeys) {
        if (!adminUser.getLoginType().getCode().equals(authVO.getLoginType())) {
            log.info("登录合并，用户userId：{}，登录方式：{}，token：{}", authVO.getUserId(), authVO.getLoginType(), authVO.getToken());
            authVO.setLoginType(adminUser.getLoginType().getCode());
            authVO.setLastEnterDate(LocalDateTime.now());
            //刷新用户信息
            storageToRedis(authVO, () -> tokenKeys, TOKEN_EXPIRE);
        }
        log.info("普通登录token：{}", authVO.getToken());
        return authVO;
    }
}
