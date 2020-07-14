package com.yunya.framework.auth.utils;

import com.yunya.framework.auth.config.UserAuthConfig;
import com.yunya.framework.common.exception.auth.UserTokenException;
import com.yunya.framework.common.utils.jwt.IJWTInfo;
import com.yunya.framework.common.utils.jwt.JWTHelper;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.security.SignatureException;

/**
 * 用户鉴权处理工具类
 *
 * @author ace
 * @date 2017/9/15
 */
@Configuration
public class UserAuthUtil {

  /** 注入对象 */
  @Autowired private UserAuthConfig userAuthConfig;

  /**
   * 解析token获取JWT信息
   *
   * @param token token
   * @return
   * @throws Exception
   */
  public IJWTInfo getInfoFromToken(String token) throws Exception {
    try {
      // 解析用户token信息并对token进行校验
      return JWTHelper.getInfoFromToken(token, userAuthConfig.getPubKeyByte());
    } catch (ExpiredJwtException ex) {
      throw new UserTokenException("User token expired!");
    } catch (SignatureException ex) {
      throw new UserTokenException("User token signature error!");
    } catch (IllegalArgumentException ex) {
      throw new UserTokenException("User token is null or empty!");
    }
  }
}
