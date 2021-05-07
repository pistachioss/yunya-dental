package com.yunya365.wechat.service.impl;

import com.yunya365.wechat.config.WXConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2021/3/30 14:38
 **/
@Service
public class WxServerConfigVerify {
    @Resource
    private WXConfig wxConfig;

    public String verifyServerConfig(String timestamp, String nonce) {
        // 1）将token、timestamp、nonce三个参数进行字典序排序
        // 2）将三个参数字符串拼接成一个字符串进行sha1加密
        String sortedParams = this.getSortedParams(timestamp, nonce);
        return this.getEncryptedParams(sortedParams);
    }

    public void getWxUserIfo() {

    }

    private String getSortedParams(String timestamp, String nonce) {
        List<String> params = new ArrayList<>();
        params.add(wxConfig.getToken());
        params.add(timestamp);
        params.add(nonce);
        params.sort(Comparator.naturalOrder());
        StringBuilder validateString = new StringBuilder();
        for (String param : params) {
            validateString.append(param);
        }
        return validateString.toString();
    }

    private String getEncryptedParams(String sortedParams) {
        MessageDigest crypt = null;
        try {
            crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(sortedParams.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new BigInteger(1, crypt.digest()).toString(16);
    }
}
