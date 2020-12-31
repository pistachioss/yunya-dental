package com.yunya.framework.common.utils.jwt;

import com.yunya.framework.common.constant.UserConstant;
import com.yunya.framework.common.utils.StringHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * token生成解析工具类
 *
 * @author ace
 * @date 2017/9/10
 */
public class JWTHelper {

  private static final RsaKeyHelper RSA_KEY_HELPER = new RsaKeyHelper();

  /**
   * 密钥加密token
   *
   * @param jwtInfo jwt信息
   * @param priKeyPath 密钥
   * @param expire 过期时间
   * @return
   * @throws Exception
   */
  public static String generateToken(IJWTInfo jwtInfo, String priKeyPath, int expire)
      throws Exception {
    String compactJws =
        Jwts.builder()
            .setSubject(jwtInfo.getUniqueName())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .claim(UserConstant.JWT_KEY_USER_ID, jwtInfo.getId())
            .claim(UserConstant.JWT_KEY_NAME, jwtInfo.getName())
            .claim(UserConstant.JWT_KEY_DEVICE_TYPE,jwtInfo.getDeviceType())
            .setExpiration(DateTime.now().plusSeconds(expire).toDate())
            .signWith(SignatureAlgorithm.RS256, RSA_KEY_HELPER.getPrivateKey(priKeyPath))
            .compact();
    return compactJws;
  }

  /**
   * 密钥加密token(不设置token过期时间)
   *
   * @param jwtInfo jwt信息
   * @param priKey 密钥
   * @return
   * @throws Exception
   */
  public static String generateToken(IJWTInfo jwtInfo, byte[] priKey) throws Exception {
    String compactJws =
        Jwts.builder()
            // 主体（所有人）
            .setSubject(jwtInfo.getUniqueName())
            // 签发时间
            .setIssuedAt(new Date(System.currentTimeMillis()))
            // 私有声明
            .claim(UserConstant.JWT_KEY_USER_ID, jwtInfo.getId())
            .claim(UserConstant.JWT_KEY_NAME, jwtInfo.getName())
            .claim(UserConstant.JWT_KEY_DEVICE_TYPE,jwtInfo.getDeviceType())
            // 加密
            .signWith(SignatureAlgorithm.RS256, RSA_KEY_HELPER.getPrivateKey(priKey))
            .compact();
    return compactJws;
  }

  /**
   * 密钥加密token
   *
   * @param jwtInfo jwt信息
   * @param priKey 密钥
   * @param expire 过期时间
   * @return
   * @throws Exception
   */
  public static String generateToken(IJWTInfo jwtInfo, byte[] priKey, int expire) throws Exception {
    String compactJws =
        Jwts.builder()
            // 主体（所有人）
            .setSubject(jwtInfo.getUniqueName())
            // 签发时间
            .setIssuedAt(new Date(System.currentTimeMillis()))
            // 私有声明
            .claim(UserConstant.JWT_KEY_USER_ID, jwtInfo.getId())
            .claim(UserConstant.JWT_KEY_NAME, jwtInfo.getName())
            .claim(UserConstant.JWT_KEY_DEVICE_TYPE, jwtInfo.getDeviceType())
            // 过期时间
            .setExpiration(DateTime.now().plusSeconds(expire).toDate())
            // 加密
            .signWith(SignatureAlgorithm.RS256, RSA_KEY_HELPER.getPrivateKey(priKey))
            .compact();
    return compactJws;
  }

  /**
   * 公钥解析token
   *
   * @param token token
   * @return
   * @throws Exception
   */
  public static Jws<Claims> parserToken(String token, String pubKeyPath) throws Exception {
    Jws<Claims> claimsJws =
        Jwts.parser().setSigningKey(RSA_KEY_HELPER.getPublicKey(pubKeyPath)).parseClaimsJws(token);
    return claimsJws;
  }

  /**
   * 公钥解析token获取JWS声明
   *
   * @param token token
   * @return
   * @throws Exception
   */
  public static Jws<Claims> parserToken(String token, byte[] pubKey) throws Exception {
    Jws<Claims> claimsJws =
        Jwts.parser().setSigningKey(RSA_KEY_HELPER.getPublicKey(pubKey)).parseClaimsJws(token);
    return claimsJws;
  }

  /**
   * 获取token中的用户信息
   *
   * @param token token
   * @param pubKeyPath 公钥地址
   * @return
   * @throws Exception
   */
  public static IJWTInfo getInfoFromToken(String token, String pubKeyPath) throws Exception {
    Jws<Claims> claimsJws = parserToken(token, pubKeyPath);
    Claims body = claimsJws.getBody();
    return new JWTInfo(
        body.getSubject(),
        StringHelper.getObjectValue(body.get(UserConstant.JWT_KEY_USER_ID)),
        StringHelper.getObjectValue(body.get(UserConstant.JWT_KEY_NAME)),
        StringHelper.getObjectValue(body.get(UserConstant.JWT_KEY_DEVICE_TYPE)));
  }

  /**
   * 获取token中的用户信息
   *
   * @param token token
   * @param pubKey 公钥字节码
   * @return
   * @throws Exception
   */
  public static IJWTInfo getInfoFromToken(String token, byte[] pubKey) throws Exception {
    // 解析token
    Jws<Claims> claimsJws = parserToken(token, pubKey);
    Claims body = claimsJws.getBody();
    return new JWTInfo(
        body.getSubject(),
        StringHelper.getObjectValue(body.get(UserConstant.JWT_KEY_USER_ID)),
        StringHelper.getObjectValue(body.get(UserConstant.JWT_KEY_NAME)),
        StringHelper.getObjectValue(body.get(UserConstant.JWT_KEY_DEVICE_TYPE)));
  }
}
