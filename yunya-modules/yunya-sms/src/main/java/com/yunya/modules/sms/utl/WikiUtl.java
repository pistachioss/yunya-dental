package com.yunya.modules.sms.utl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.modules.sms.exception.SignException;
import com.yunya.modules.sms.config.SSLClient;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.*;
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
 * @Date: 2020/12/12 14:39
 * @since: 1.0.0
 */
public class WikiUtl {
    private static Logger logger = LoggerFactory.getLogger(WikiUtl.class);

    private static final String WIKI_URL = "http://openapi.caibaopay.com/gatewayOpen.htm";

    @Value("${wiki.app}")
    private static String app;
    @Value("${wiki.operatorId}")
    private static String operatorId;
    @Value("${wiki.key}")
    private static String key;

    @Value("${wiki.notifyUrl}")
    private String notifyUrl;
    @Value("${wiki.redirectUrl}")
    private static String redirectUrl;

    /** 回调通知地址 */
    private static String NOTIFY_URL;
    /** 同步通知地址 */
    private static String REDIRECT_URL;
    private static String APP;
    private static String OPERATOR_ID;
    private static String KEY;

    @Autowired
    private static SSLClient sslClient;

    @PostConstruct
    public void init() {
        APP = app;
        OPERATOR_ID = operatorId;
        KEY = key;
        NOTIFY_URL = notifyUrl;
        REDIRECT_URL = redirectUrl;
    }

    /**
     * 创建支付订单，并生成支付二维码链接
     *
     * @param orderNo 订单号
     * @param amount 订单总金额
     * @param goodList 商品列表
     * @return
     */
    public static String createOrder(String orderNo, Long amount, JSONArray goodList) {
        DateTime dateTime = new DateTime();
        String now = dateTime.toString("yyyyMMddHHmmss");
        String qrcodeUrl = null;
        JSONObject json = new JSONObject();
        json.put("command", "open.api.h5");
        json.put("app", APP);
        json.put("operator_id", OPERATOR_ID);
        json.put("version", "2.0");
        json.put("sign_type", "MD5");
        json.put("request_id", UUID.randomUUID().toString());
        json.put("request_time", now);
        json.put("local_order_no", orderNo);
        json.put("amount", amount+"");
        json.put("goods_list", goodList.toJSONString());
        json.put("notify_url", NOTIFY_URL);
        json.put("redirect_url", REDIRECT_URL);
        Map<String, String> params = json.toJavaObject(Map.class);
        json.put("sign",getSign(params, KEY));
        logger.info("createOrder param: {}", json.toJSONString());
//        String result = sslClient.doPost(url, json.toJSONString());
        String result = SSLClient.doPost(WIKI_URL, json.toJSONString());
        logger.info("createOrder result: {}", result);
        return qrcodeUrl;
    }

    public static void main(String[] args) {
        JSONObject good = new JSONObject();
        good.put("goods_name", "500条/￥0.01（￥0.1/条）");
        good.put("sell_amount", "1");
        good.put("goods_price", "0.01");
        good.put("goods_id","");
        good.put("goods_num","");
        good.put("goods_sku_id","");
        JSONArray arr = new JSONArray();
        arr.add(good);
        createOrder("122aq232qc312", 1L, arr);
    }

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
     * 排序需要签名的字段
     * @param sortedParams 参数Map
     * @return
     */
    public static String getSignContent(Map<String, String> sortedParams) {
        StringBuffer content = new StringBuffer();
        ArrayList keys = new ArrayList(sortedParams.keySet());
        Collections.sort(keys);
        int index = 0;
        for(int i = 0; i < keys.size(); ++i) {
            String key = (String)keys.get(i);
            String value = sortedParams.get(key);
            if (StringHelper.isNotEmpty(value)) {
                content.append((index == 0 ? "" : "&") + key + "=" + value);
                ++index;
            }
        }

        return content.toString();
    }


    public static boolean rsaCheck(Map<String, String> params, String sign) {
        String signContent = WikiUtl.getSignContent(params);
        rsaCheck(signContent, sign, app, "UTF-8", "RSA");
        return false;
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
