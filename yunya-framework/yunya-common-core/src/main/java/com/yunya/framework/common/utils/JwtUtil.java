package com.yunya.framework.common.utils;


import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.StringPool;
import com.yunya.framework.common.enums.ExceptionCode;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.Token;
import io.jsonwebtoken.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.framework.common.constant.WxMiniAuthConstant.*;
import static com.yunya.framework.common.enums.ExceptionCode.*;


@Slf4j
public final class JwtUtil {

    /**
     * 将 签名（JWT_SIGN_KEY） 编译成BASE64编码
     */
    private static final String BASE64_SECURITY =
            Base64.getEncoder()
                    .encodeToString(JWT_SIGN_KEY.getBytes(StandardCharsets.UTF_8));

    private JwtUtil() {
    }

    /**
     * authorization: Basic clientId:clientSec 解析请求头中存储的 client 信息
     *
     * <p>Basic clientId:clientSec -截取-> clientId:clientSec后调用 extractClient 解码
     *
     * @param basicHeader Basic clientId:clientSec
     * @return clientId:clientSec
     */
    public static String[] getClient(String basicHeader) {
        if (StringHelper.isEmpty(basicHeader) || !basicHeader.startsWith(BASIC_HEADER_PREFIX)) {
            throw ClientServiceException.wrap(JWT_BASIC_INVALID);
        }

        String decodeBasic = StringHelper.subAfter(basicHeader, BASIC_HEADER_PREFIX, false);
        return extractClient(decodeBasic);
    }

    /**
     * 解析请求头中存储的 client 信息 clientId:clientSec 解码
     */
    public static String[] extractClient(String client) {
        String token = base64Decoder(client);
        int index = token.indexOf(StringPool.COLON);
        if (index == -1) {
            throw ClientServiceException.wrap(JWT_BASIC_INVALID);
        } else {
            return new String[]{token.substring(0, index), token.substring(index + 1)};
        }
    }

    /**
     * 使用 Base64 解码
     *
     * @param val 参数
     * @return 解码后的值
     */
    @SneakyThrows
    public static String base64Decoder(String val) {
        byte[] decoded = Base64.getDecoder().decode(val.getBytes(StandardCharsets.UTF_8));
        return new String(decoded, StandardCharsets.UTF_8);
    }

    /**
     * 创建令牌
     *
     * @param user   user
     * @param expire 过期时间（秒)
     * @return jwt
     */
    public static Token createJwt(Map<String, String> user, long expire) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 生成签名密钥
        byte[] apiKeySecretBytes = Base64.getDecoder().decode(BASE64_SECURITY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // 添加构成JWT的类
        JwtBuilder builder =
                Jwts.builder()
                        .setHeaderParam("type", "JsonWebToken")
                        .signWith(signatureAlgorithm, signingKey);

        // 设置JWT参数
        user.forEach(builder::claim);

        // 添加Token过期时间
        // allowedClockSkewMillis
        long expMillis = nowMillis + expire * 1000;
        Date exp = new Date(expMillis);
        builder
                // 发布时间
                .setIssuedAt(now)
                // token从时间什么开始生效
                .setNotBefore(now)
                // token从什么时间截止生效
                .setExpiration(exp);

        // 组装Token信息
        Token tokenInfo = new Token();
        tokenInfo.setToken(builder.compact());
        tokenInfo.setExpire(expire);
        tokenInfo.setExpiration(DateUtil.date2LocalDateTime(exp));
        return tokenInfo;
    }

    /**
     * 解析jwt
     *
     * @param jsonWebToken            待解析token
     * @param allowedClockSkewSeconds 允许的时间差
     * @return Claims
     */
    public static Claims parseJwt(String jsonWebToken, long allowedClockSkewSeconds) {
        try {
            return Jwts.parser()
                    .setSigningKey(Base64.getDecoder().decode(BASE64_SECURITY))
                    .setAllowedClockSkewSeconds(allowedClockSkewSeconds)
                    .parseClaimsJws(jsonWebToken)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            // 过期
            throw ex;
        } catch (IllegalArgumentException ex) {
            log.error("token=[{}] 为空", jsonWebToken, ex);
            // token 为空
            throw new ClientServiceException(
                    ExceptionCode.JWT_ILLEGAL_ARGUMENT.getCode(),
                    ExceptionCode.JWT_ILLEGAL_ARGUMENT.getMessage(),
                    ex);
        } catch (Exception e) {
            log.error(
                    "token=[{}] errCode:{}, message:{}",
                    jsonWebToken,
                    JWT_PARSER_TOKEN_FAIL.getCode(),
                    e.getMessage(),
                    e);
            throw new ClientServiceException(ExceptionCode.JWT_PARSER_TOKEN_FAIL.getCode(), JWT_PARSER_TOKEN_FAIL.getMessage(), e);
        }
    }

    public static String getToken(String token) {
        if (token == null) {
            throw ClientServiceException.wrap(JWT_PARSER_TOKEN_FAIL);
        }
        if (token.startsWith(BEARER_HEADER_PREFIX)) {
            return StringHelper.subAfter(token, BEARER_HEADER_PREFIX, false);
        }
        log.info("jsonWebToken={}", token);
        throw ClientServiceException.wrap(JWT_PARSER_TOKEN_FAIL);
    }

    /**
     * 获取Claims(声明)
     *
     * @param token                   待解析token
     * @param allowedClockSkewSeconds 允许存在的时间差
     */
    public static Claims getClaims(String token, int subIndex, long allowedClockSkewSeconds) {
        return parseJwt(token.substring(subIndex), allowedClockSkewSeconds);
    }
}
