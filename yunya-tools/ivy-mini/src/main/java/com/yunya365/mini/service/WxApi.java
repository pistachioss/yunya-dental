package com.yunya365.mini.service;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.ivy_mini.domain.vo.WxAccessTokenVo;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya365.mini.config.WxMiniProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static com.yunya.framework.common.constant.RedisConstants.*;
import static com.yunya.framework.common.constant.WxMiniAuthConstant.*;
import static com.yunya.framework.common.constant.WxMiniUri.*;
import static com.yunya365.mini.enums.IvyMiniError.*;


/**
 * @description:
 * @author: xy
 * @date 2021/12/20 16:06
 **/
@Service
@Slf4j
public class WxApi {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private WxMiniProperties wxMiniProperties;
    @Resource
    private RedisUtils redisUtils;

    public <T> T getWxResult(String url, Class<T> clazz) {
        return this.getWxResult(url).toJavaObject(clazz);
    }

    public JSONObject getWxResult(String url) {
        log.info("微信get请求，url：{}", url);
        String resultStr = restTemplate.getForObject(url, String.class);
        return getRequestRes(resultStr);
    }

    public JSONObject wxPostObject(String url, Object obj) {
        log.info("微信片post请求，url：{}", url);
        String resultStr = restTemplate.postForObject(url, obj, String.class);
        return getRequestRes(resultStr);
    }

    public void refreshToken(){
        String url = String.format(WX_ACCESS_TOKEN_URL, wxMiniProperties.getAppId(), wxMiniProperties.getAppSecret());
        WxAccessTokenVo accessTokenRes = getWxResult(url, WxAccessTokenVo.class);
        log.info("调用微信access_token返回结果是: {}", accessTokenRes);
        if (accessTokenRes == null) {
            log.info("小程序token获取失败");
            return;
        }
        String accessToken = accessTokenRes.getAccessToken();
        String redisKey = RedisConstants.buildLockCacheKey(MINI_ACCESS_TOKEN_KEY, wxMiniProperties.getAppId());
        //redis工具根据项目自行修改
        redisUtils.set(redisKey, accessToken, 7200, TimeUnit.SECONDS);
    }

    public String getAccessToken() {
        return redisUtils.get(RedisConstants.buildLockCacheKey(MINI_ACCESS_TOKEN_KEY, wxMiniProperties.getAppId()));
    }

    /**
     * 解密成json
     *
     * @param encryptedData encryptedData
     * @param iv iv
     * @return JSONObject
     */
    public JSONObject decrypt(String encryptedData, String iv) {
        byte[] encryptedDataDecode = Base64.getDecoder().decode(encryptedData);
        byte[] sessionKeyDecode = Base64.getDecoder().decode(getSession(BaseContextHandler.getOpenId()));
        byte[] ivDecode = Base64.getDecoder().decode(iv);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        byte[] decodeData = decode(encryptedDataDecode, sessionKeyDecode, ivDecode);
        String stringData = new String(decodeData);
        return JSONObject.parseObject(stringData);
    }

    public void storageSessionKey(String openId, String sessionKey) {
        String sessionKeyPre = RedisConstants.buildLockCacheKey(MINI_SESSION_KEY, openId);
        redisUtils.set(sessionKeyPre, sessionKey, TOKEN_EXPIRE, TimeUnit.SECONDS);
    }

    public String getSession(String openId) {
        String sessionKeyPre = RedisConstants.buildLockCacheKey(MINI_SESSION_KEY, openId);
        return redisUtils.get(sessionKeyPre);
    }

    /**
     * 解密算法 AES-128-CBC
     * 填充模式 PKCS#7
     *
     * @param encryptedDataDecode 目标密文
     * @return byte[]
     */
    private byte[] decode(byte[] encryptedDataDecode, byte[] sessionKeyDecode, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Key sKeySpec = new SecretKeySpec(sessionKeyDecode, "AES");
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(iv));
            return cipher.doFinal(encryptedDataDecode);
        } catch (Exception e) {
            log.error("小程序解密失败：", e);
            throw new ClientServiceException(e);
        }
    }

    /**
     * 处理iv
     * @param iv:
     * @return AlgorithmParameters
     */
    private AlgorithmParameters generateIV(byte[] iv) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(iv));
        return params;
    }

    private JSONObject getRequestRes(String resultStr) {
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        Integer errCode = jsonObject.getInteger("errcode");
        if (errCode != null && errCode != 0) {
            log.error("微信api调用失败：{}", jsonObject);
            throw ClientServiceException.wrap(errCode, WX_SERVER_ERROR.getMessage());
        }
        return jsonObject;
    }


}
