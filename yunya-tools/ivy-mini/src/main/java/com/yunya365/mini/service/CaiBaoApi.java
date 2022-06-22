package com.yunya365.mini.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.io.ByteStreams;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya365.mini.config.CaiBaoMiniProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import static com.yunya365.mini.enums.IvyMiniError.*;


/**
 * @description:
 * @author: xy
 * @date 2021/12/20 16:06
 **/
@Service
@Slf4j
public class CaiBaoApi {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private CaiBaoMiniProperties caiBaoMiniProperties;
    @Resource
    private RedisUtils redisUtils;

    private static final String CB_PAY_URL = "https://openapi.caibaopay.com/gatewayOpen.htm";

    public <T> T getCbResult(Class<T> clazz) {
        return this.getCbResult().toJavaObject(clazz);
    }

    public JSONObject getCbResult() {
        String resultStr = restTemplate.getForObject(CB_PAY_URL, String.class);
        return getRequestRes(resultStr);
    }

    public JSONObject cbPostObject(Object obj) {
        String resultStr = restTemplate.postForObject(CB_PAY_URL, obj, String.class);
        return getRequestRes(resultStr);
    }

    public <t> t cbPostFormObject(MultiValueMap<String, String> param, Class<t> clazz) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(param, headers);
        JSONObject resultStr = cbPostObject(request);
        return resultStr.toJavaObject(clazz);
    }

    /**
     * 使用md5算法进行加密
     *
     * @param target  要加密的字符串
     * @param charset 编码（请设置为UTF-8)
     * @return 加密后的字符串
     */
    public String encryptWithMD5(String target, String charset) {
        String md5Str = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            byte[] bytes = md5.digest(charset == null ? target.getBytes() : target.getBytes(charset));
            StringBuilder stringBuffer = new StringBuilder();
            for (byte b : bytes) {
                int bt = b & 0xff;
                if (bt < 16) {
                    stringBuffer.append(0);
                }
                stringBuffer.append(Integer.toHexString(bt));
            }
            md5Str = stringBuffer.toString();
        } catch (Exception ex) {
            log.error("encrypt error,target: {}" + target, ex);
        }
        return md5Str;
    }

    public boolean verifySign(Map<String, Object> params, String sign) {
        String content = getSignContent(params);
        return rsaCheck(content, sign,"UTF-8", "RSA2");
    }

    public String generateSign(Map<String, Object> params) {
        String sign;
        StringBuilder sb = new StringBuilder();
        //排序
        List<Map.Entry<String, Object>> infoIds =
                new ArrayList<>(params.entrySet());
        infoIds.sort(Map.Entry.comparingByKey());
        //对参数数组进行按key升序排列,然后拼接，最后调用5签名方法
        for (Map.Entry<String, Object> infoId : infoIds) {
            if (Objects.nonNull(infoId.getValue())) {//不为空，为空的不参与签名
                sb.append(infoId.getKey()).append("=").append(infoId.getValue().toString()).append("&");
            }
        }
        String newStrTemp = sb + "key=" + caiBaoMiniProperties.getKey().trim();
        //获取sign_method
        sign = encryptWithMD5(newStrTemp, "UTF-8");
        return sign;
    }

    /**
     * 排序需要签名的字段
     * @param sortedParams 参数Map
     */
    public static String getSignContent(Map<String, Object> sortedParams) {
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(sortedParams.keySet());
        Collections.sort(keys);
        int index = 0;
        for (String key : keys) {
            Object value = sortedParams.get(key);
            if (Objects.nonNull(value)) {
                content.append(index == 0 ? "" : "&").append(key).append("=").append(value.toString());
                ++index;
            }
        }
        return content.toString();
    }

    /**
     * 验证签名
     *
     * @param content   签证签名内容串
     * @param sign      签名
     * @param charset   字符集
     * @param signType  签名类型
     */
    private boolean rsaCheck(String content, String sign, String charset, String signType){
        if ("RSA".equals(signType)) {
            return rsaCheckContent(content, sign, charset);
        } else if ("RSA2".equals(signType)) {
            return rsa256CheckContent(content, sign, charset);
        } else {
            throw ClientServiceException.wrap(SIGN_TYPE_ERROR, signType);
        }
    }

    private PublicKey getPublicKeyFromX509(String algorithm, InputStream ins) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        byte[] encodedKey = ByteStreams.toByteArray(ins);
        encodedKey = Base64.getDecoder().decode(encodedKey);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    private  boolean rsaCheckContent(String content, String sign, String charset){
        try {
            PublicKey e = getPublicKeyFromX509("RSA", new ByteArrayInputStream(caiBaoMiniProperties.getPublicKey().getBytes()));
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(e);
            if (StringUtils.isNotBlank(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }
            return signature.verify(Base64.getDecoder().decode(sign.getBytes()));
        } catch (Exception var6) {
            log.error("验签失败：content={}, sign={},charset = {}",content, sign, charset, var6);
            throw ClientServiceException.wrap(SIGNATURE_ERROR);
        }
    }

    public boolean rsa256CheckContent(String content, String sign,  String charset){
        try {
            PublicKey e = getPublicKeyFromX509("RSA", new ByteArrayInputStream(caiBaoMiniProperties.getPublicKey().getBytes()));
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initVerify(e);
            if (StringUtils.isNotBlank(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }
            return signature.verify(Base64.getDecoder().decode(sign.getBytes()));
        } catch (Exception var6) {
            log.error("验签失败：content={}, sign={},charset = {}",content, sign, charset, var6);
            throw ClientServiceException.wrap(SIGNATURE_ERROR);
        }
    }

    private JSONObject getRequestRes(String resultStr) {
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        JSONObject result = jsonObject.getJSONObject("result");
        Boolean success = result.getBoolean("success");
        String errCode = result.getString("error_code");
        String errorMsg = result.getString("error_msg");
        if (!success || StringUtils.isNotBlank(errCode) || StringUtils.isNotBlank(errorMsg)) {
            log.error("采宝api调用失败：{}", result);
            throw ClientServiceException.wrap(CB_SERVER_ERROR, errCode, errorMsg);
        }
        return jsonObject;
    }
}
