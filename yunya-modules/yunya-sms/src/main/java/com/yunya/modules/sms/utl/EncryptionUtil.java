package com.yunya.modules.sms.utl;

import com.yunya.framework.common.utils.StringHelper;
import com.yunya.modules.sms.exception.SignException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/18 9:34
 * @since: 1.0.0
 */
public class EncryptionUtil {
    private static Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);
    /**
     * 获取签名
     * @param params
     * @param key
     * @return
     */
    public static String getSign(Map<String, String> params, String key) {
        StringBuffer sb = new StringBuffer();
        //排序
        List<Map.Entry<String, String>> infoIds = new ArrayList<>(params.entrySet());
        //对参数数组进行按key升序排列,然后拼接，最后调用5签名方法
        Collections.sort(infoIds, Comparator.comparing(Map.Entry::getKey));
        int size  = infoIds.size();
        for(int i = 0; i < size; i++) {
            Map.Entry<String, String> info = infoIds.get(i);
            if(StringHelper.isNotEmpty(info.getValue())) {//不为空，为空的不参与签名
                sb.append(info.getKey() + "=" + info.getValue() + "&");
            }
        }
        String newStrTemp = sb.toString()+"key="+key.trim();
        //获取sign_method
        return encryptWithMD5(newStrTemp,"UTF-8");
    }

    /**
     * 使用md5算法进行加密
     *
     * @param target
     *            要加密的字符串
     * @param charset
     *            编码（请设置为UTF-8)
     * @return 加密后的字符串
     */
    public static String encryptWithMD5(String target,String charset) {
        String md5Str = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            byte[] bytes = md5.digest(charset==null?target.getBytes():target.getBytes(charset));
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bytes) {
                int bt = b & 0xff;
                if (bt < 16) {
                    stringBuffer.append(0);
                }
                stringBuffer.append(Integer.toHexString(bt));
            }
            md5Str = stringBuffer.toString();
        } catch (Exception ex) {
            logger.error("encrypt error,target:" + target, ex);
        }
        return md5Str;
    }

    /**
     * 验证签名
     *
     * @param content 签证签名内容串
     * @param sign 签名
     * @param publicKey 公钥
     * @param charset 字符集
     * @param signType 签名类型
     * @return
     * @throws SignException
     */
    public static boolean rsaCheck(String content, String sign, String publicKey, String charset, String signType) throws SignException {
        if ("RSA".equals(signType)) {
            return rsaCheckContent(content, sign, publicKey, charset);
        } else if ("RSA2".equals(signType)) {
            return rsa256CheckContent(content, sign, publicKey, charset);
        } else {
            throw new SignException("Sign Type is Not Support : signType=" + signType);
        }
    }

    public static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        StringWriter writer = new StringWriter();
        IOUtils.copy(ins, writer, "UTF-8");
        byte[] encodedKey = writer.toString().getBytes();
        encodedKey = Base64.getDecoder().decode(encodedKey);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    public static boolean rsaCheckContent(String content, String sign, String publicKey, String charset) throws SignException {
        try {
            PublicKey e = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey.getBytes()));
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(e);
            if (StringHelper.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.getDecoder().decode(sign.getBytes()));
        } catch (Exception e) {
            throw new SignException("RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
        }
    }

    public static boolean rsa256CheckContent(String content, String sign, String publicKey, String charset) throws SignException {
        try {
            PublicKey e = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey.getBytes()));
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initVerify(e);
            if (StringHelper.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.getDecoder().decode(sign.getBytes()));
        } catch (Exception e) {
            throw new SignException("RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
        }
    }
}
