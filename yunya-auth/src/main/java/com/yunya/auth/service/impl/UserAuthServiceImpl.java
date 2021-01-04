package com.yunya.auth.service.impl;

import com.yunya.auth.domain.UserAuthResponse;
import com.yunya.auth.service.UserAuthService;
import com.yunya.auth.utils.JwtTokenUtil;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.JwtRequestFrom;
import com.yunya.feign.system.vo.FrontUserInfoVO;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.exception.auth.UserAuthException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.jwt.IJWTInfo;
import com.yunya.framework.common.utils.jwt.JWTHelper;
import com.yunya.framework.common.utils.jwt.JWTInfo;
import com.yunya.framework.redis.util.RedisUtils;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static com.yunya.framework.common.constant.BusinessConstants.ADMIN_ACCOUNT;
import static com.yunya.framework.common.constant.BusinessConstants.USER_RESIGNATION_STATUS;

/**
 * 简单介绍: 用户鉴权接口实现
 *
 * @author: chow
 * @date: 2020/7/5 15:37
 * @description:
 * @since: 1.0.0
 */
@Slf4j
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
  public UserAuthResponse login(JwtRequestFrom paramForm, HttpServletRequest request) throws Exception {
    // 调用远程服务获取用户信息
    FrontUserInfoVO userInfo = systemServiceFeign.validate(paramForm);
    checkUserInfo(userInfo);
    String userId = userInfo.getId();
    if (!StringUtils.isEmpty(userId)) {
      String userAgentStr = request.getHeader("User-Agent");
      UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
      OperatingSystem operatingSystem = userAgent.getOperatingSystem();
      DeviceType deviceType = operatingSystem.getDeviceType();
      String tokenStr = redisUtils.get(USER_ID + userId);
      if (StringHelper.isBlank(tokenStr)) {
        String token = this.setTokenInfoInCache(userInfo,userId,deviceType);
        return new UserAuthResponse(token, userInfo);
      } else {
        String deviceName = deviceType.getName();
        IJWTInfo infoFromToken = jwtTokenUtil.getInfoFromToken(tokenStr);
        if (deviceName.equals(infoFromToken.getDeviceType())) {
          String token = this.setTokenInfoInCache(userInfo,userId,deviceType);
          return new UserAuthResponse(token, userInfo);
        } else {
          return new UserAuthResponse(tokenStr,userInfo);
        }
      }
    }
    throw new UserAuthException("用户不存在或账户密码错误!");
  }

  /**
   * 校验用户信息是否合法(管理员不用校验)
   *
   * @param userInfo 用户信息
   */
  private void checkUserInfo(FrontUserInfoVO userInfo) {
    // 管理员账号
    if (null != userInfo.getId() && !ADMIN_ACCOUNT.equals(userInfo.getUsername())) {
      if (USER_RESIGNATION_STATUS.equals(userInfo.getWorkStatus())) {
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
    FrontUserInfoVO userInfo = redisUtils.get(USER_TOKEN + oldToken, FrontUserInfoVO.class);
    if (null == userInfo) {
      throw new UserAuthException("当前token已失效，请重新登陆！");
    }
    // 解析token获取JWT声明信息，重生成token
    String refreshToken = jwtTokenUtil.refreshToken(oldToken);
    // 获取token过期时间
    long expireTime = jwtTokenUtil.getExpireTime(refreshToken);
    redisUtils.set(USER_TOKEN + refreshToken, userInfo, expireTime, TimeUnit.MILLISECONDS);
    redisUtils.set(USER_ID + userInfo.getId(), refreshToken, expireTime, TimeUnit.MILLISECONDS);
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
    FrontUserInfoVO userInfo = redisUtils.get(USER_TOKEN + token, FrontUserInfoVO.class);
    if (null != userInfo) {
      // todo 记录登出信息
      // 从缓存中移除用户的token、用户信息
      redisUtils.delete(USER_ID + userInfo.getId());
      redisUtils.delete(USER_TOKEN + token);
    }
  }

  /**
   * 将token信息设置到redis缓冲中
   * @param userInfo  用户信息
   * @param userId    用户ID
   * @param deviceType 当前访问设备类型 Computer(电脑) Mobile(移动设备) Tablet(平板) Game console(游戏机) Digital media receiver(数字媒体设备) Wearable computer(嵌入式设备) Unknown(未知)
   * @throws Exception 异常
   * @return 返回生成的token
   */
  public String setTokenInfoInCache(FrontUserInfoVO userInfo,String userId, DeviceType deviceType) throws Exception {
    // todo 记录登陆信息
    String token =
            jwtTokenUtil.generateToken(
                    new JWTInfo(userInfo.getUsername(), userId, userInfo.getName(),deviceType.getName()));
    // 获取token的过期时间
    long expireTime = jwtTokenUtil.getExpireTime(token);
    // 缓存用户信息、用户token
    redisUtils.set(USER_TOKEN + token, userInfo, expireTime, TimeUnit.MILLISECONDS);
    redisUtils.set(USER_ID + userId, token, expireTime, TimeUnit.MILLISECONDS);
    return token;
  }
}
