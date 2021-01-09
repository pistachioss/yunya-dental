package com.yunya.auth.utils;

import com.yunya.auth.configuration.KeyConfiguration;
import com.yunya.framework.common.constant.UserConstant;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.jwt.IJWTInfo;
import com.yunya.framework.common.utils.jwt.JWTHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * JWT信息处理工具类 【用于生成、解析token】
 *
 * @author chow
 */
@Component
@Data
public class JwtTokenUtil {

  /** 注入对象 */
  @Value("${jwt.expire}")
  private int expire;

  private final KeyConfiguration keyConfiguration;

  public JwtTokenUtil(KeyConfiguration keyConfiguration) {
    this.keyConfiguration = keyConfiguration;
  }

  /**
   * 生成token
   *
   * @param jwtInfo jwt参数
   * @return
   * @throws Exception
   */
  public String generateToken(IJWTInfo jwtInfo) throws Exception {
    return JWTHelper.generateToken(jwtInfo, keyConfiguration.getUserPriKey(), expire);
  }

  /**
   * 解析token获取JWT信息（用户ID，用户名，用户姓名）
   *
   * @param token token
   * @return
   * @throws Exception
   */
  public IJWTInfo getInfoFromToken(String token) throws Exception {
    return JWTHelper.getInfoFromToken(token, keyConfiguration.getUserPubKey());
  }

  /**
   * 刷新token
   *
   * @param token 旧的token
   * @return
   */
  public String refreshToken(String token) throws Exception {
    IJWTInfo info = getInfoFromToken(token);
    return generateToken(info);
  }

  /**
   * 解析token获取Jws声明信息
   *
   * @param token token
   * @return
   * @throws Exception
   */
  public Jws<Claims> getJwsClaims(String token) throws Exception {
    return JWTHelper.parserToken(token, keyConfiguration.getUserPubKey());
  }

  /**
   * 判断token是否过期
   *
   * @param token token
   * @return
   */
  public Boolean isTokenExpired(String token) throws Exception {
    Date expiration = getJwsClaims(token).getBody().getExpiration();
    return expiration.before(new Date());
  }

  /**
   * 获取token中的用户名
   *
   * @param token token
   * @return
   * @throws Exception
   */
  public String getUsernameFromToken(String token) throws Exception {
    IJWTInfo jwtInfo = getInfoFromToken(token);
    return jwtInfo.getUniqueName();
  }

  /**
   * 获取token中的用户姓名
   *
   * @param token token
   * @return
   * @throws Exception
   */
  public String getUserNameFromToken(String token) throws Exception {
    IJWTInfo jwtInfo = getInfoFromToken(token);
    return jwtInfo.getName();
  }

  /**
   * 获取token中的用户ID
   *
   * @param token token
   * @return
   * @throws Exception
   */
  public String getUserIdFromToken(String token) throws Exception {
    IJWTInfo jwtInfo = getInfoFromToken(token);
    return jwtInfo.getId();
  }

  /**
   * 获取token的过期时间
   *
   * @param token token
   * @return 返回过期时间（ms）
   * @throws Exception
   */
  public long getExpireTime(String token) throws Exception {
    long expireTimeMs = getJwsClaims(token).getBody().getExpiration().getTime();
    String expireTime = StringHelper.getObjectValue(getJwsClaims(token).getBody().get(UserConstant.JWT_APPLY_TOKEN_TIME));
    long applyTimeMs = Long.parseLong(expireTime);
    return (expireTimeMs - applyTimeMs);
  }
}
