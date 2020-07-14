package com.yunya.auth.service.impl;

import com.yunya.auth.domain.UserAuthResponse;
import com.yunya.auth.service.UserAuthService;
import com.yunya.auth.utils.JwtTokenUtil;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.JwtRequestFrom;
import com.yunya.feign.system.vo.UserInfo;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.exception.auth.UserAuthException;
import com.yunya.framework.common.utils.jwt.JWTInfo;
import com.yunya.framework.redis.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 简单介绍: 用户鉴权接口实现
 *
 * @author: chow
 * @date: 2020/7/5 15:37
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserAuthServiceImpl implements UserAuthService {

  /** 注入对象 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;

  @Autowired private JwtTokenUtil jwtTokenUtil;

  @Autowired private RedisUtils redisUtils;

  private final String USER_TOKEN = RedisConstants.REDIS_KEY_USER_TOKEN;

  private final String USER_ID = RedisConstants.REDIS_KEY_USER_ID;

  /**
   * 用户登陆
   *
   * @param paramForm 参数封装
   * @return String（token）
   */
  @Override
  public UserAuthResponse login(JwtRequestFrom paramForm) throws Exception {
    // 调用远程服务获取用户信息
    UserInfo userInfo = systemServiceFeign.validate(paramForm);
    checkUserInfo(userInfo);
    String userId = userInfo.getId();
    if (!StringUtils.isEmpty(userId)) {
      // todo 记录登陆信息
      String token =
          jwtTokenUtil.generateToken(
              new JWTInfo(userInfo.getUsername(), userId, userInfo.getName()));
      // 获取token的过期时间
      long expireTime = jwtTokenUtil.getExpireTime(token);
      // 缓存用户信息、用户token
      redisUtils.set(USER_TOKEN + token, userInfo, expireTime);
      redisUtils.set(USER_ID + userId, token, expireTime);
      return new UserAuthResponse(token, userInfo);
    }
    throw new UserAuthException("用户不存在或账户密码错误!");
  }

  /**
   * 校验用户信息是否合法(管理员不用校验)
   *
   * @param userInfo 用户信息
   */
  private void checkUserInfo(UserInfo userInfo) {
    /** 管理员账号 */
    if (null != userInfo.getId()
        && !BusinessConstants.ADMIN_ACCOUNT.equals(userInfo.getUsername())) {
      if (BusinessConstants.USER_RESIGNATION_STATUS.equals(userInfo.getWorkStatus())) {
        throw new UserAuthException("当前员工已离职，账号无法登陆！");
      }
    }
  }

  /**
   * 刷新token
   *
   * @param oldToken 旧的token
   * @return
   * @throws Exception
   */
  @Override
  public UserAuthResponse refresh(String oldToken) throws Exception {
    // 获取缓存中的用户信息
    UserInfo userInfo = redisUtils.get(USER_TOKEN + oldToken, UserInfo.class);
    if (null == userInfo) {
      throw new UserAuthException("当前token已失效，请重新登陆！");
    }
    // 解析token获取JWT声明信息，重生成token
    String refreshToken = jwtTokenUtil.refreshToken(oldToken);
    // 获取token过期时间
    long expireTime = jwtTokenUtil.getExpireTime(refreshToken);
    redisUtils.set(USER_TOKEN + refreshToken, userInfo, expireTime);
    redisUtils.set(USER_ID + userInfo.getId(), refreshToken, expireTime);
    return new UserAuthResponse(refreshToken, userInfo);
  }

  /**
   * 校验token
   *
   * @param token token
   * @throws Exception
   */
  @Override
  public void validate(String token) throws Exception {
    jwtTokenUtil.getInfoFromToken(token);
  }

  /**
   * 退出登陆
   *
   * @param token token
   */
  @Override
  public void logout(String token) {
    // 从缓存中获取用户
    UserInfo userInfo = redisUtils.get(USER_TOKEN + token, UserInfo.class);
    if (null != userInfo) {
      // todo 记录登出信息
      // 从缓存中移除用户的token、用户信息
      redisUtils.delete(USER_ID + userInfo.getId());
      redisUtils.delete(USER_TOKEN + token);
    }
  }
}
