package com.yunya.modules.sms.utl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.yunya.framework.common.constant.OperationCodeConstants.OPERATION_FAIL;

/**
 * 简介：采宝
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/12 14:39
 * @since: 1.0.0
 */
@Component
public class WikiUtl {
    private static Logger logger = LoggerFactory.getLogger(WikiUtl.class);

    private static final String WIKI_URL = "http://openapi.caibaopay.com/gatewayOpen.htm";

    @Value("${wiki.app}")
    private String app;
    @Value("${wiki.operatorId}")
    private String operatorId;
    @Value("${wiki.key}")
    private String key;
    @Value("${wiki.notifyUrl}")
    private String notifyUrl;
    @Value("${wiki.redirectUrl}")
    private String redirectUrl;
    /**
     * 订单支付有效时长
     */
    private static final String EXPIRE_IN = "3600";

    /** 回调通知地址 */
    private static String NOTIFY_URL;
    /** 同步通知地址 */
    private static String REDIRECT_URL;
    private static String APP;
    private static String OPERATOR_ID;
    private static String KEY;

    @Autowired
    private static SSLClient sslClient;

    /**
     * 根据订单号查询订单信息
     * @param orderNo
     * @return
     */
    public static JSONObject queryOrder(String orderNo, String cbOrderNo) {
        JSONObject data = null;
        Map<String, String> params = new HashMap<>();
        params.put("command", "open.api.query");
        params.put("app", APP);
        params.put("operator_id", OPERATOR_ID);
        params.put("version", "2.0");
        params.put("sign_type", "MD5");
        params.put("request_id", UUID.randomUUID().toString());
        params.put("request_time", new DateTime().toString("yyyyMMddHHmmss"));
        params.put("cb_order_no", cbOrderNo);
        params.put("local_order_no", orderNo);
        JSONObject result = null;
        try {
            params.put("sign", EncryptionUtil.getSign(params, KEY));
            logger.info("queryOrder param: {}", JSONObject.toJSON(params).toString());
            String responseResult = SSLClient.formHttp(WIKI_URL, params);
            logger.info("queryOrder result: {}", responseResult);
            JSONObject object = JSONObject.parseObject(responseResult);
            result = object.getJSONObject("result");
            data = object.getJSONObject("data");
        } catch (Exception e) {
            logger.error("queryOrder order error", e);
            throw new ClientServiceException("查询充值订单失败！", OPERATION_FAIL);
        }
        if (!result.getBoolean("success")) {
            throw new ClientServiceException(result.getString("error_msg"), OPERATION_FAIL);
        }
        return data;
    }

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
     * @param goods 商品
     * @return
     */
    public static String createOrder(String orderNo, Long amount, JSONObject goods) {
        JSONArray goodList = new JSONArray();
        goodList.add(goods);
        Map<String, String> params = new HashMap<>();
        params.put("command", "open.api.h5");
        params.put("app", APP);
        params.put("operator_id", OPERATOR_ID);
        params.put("version", "2.0");
        params.put("sign_type", "MD5");
        params.put("expire_in", EXPIRE_IN);
        params.put("subject", goods.getString("goods_name"));
        params.put("request_id", UUID.randomUUID().toString());
        params.put("request_time", new DateTime().toString("yyyyMMddHHmmss"));
        params.put("local_order_no", orderNo);
        params.put("amount", amount+"");
        params.put("goods_list", goodList.toJSONString());
        params.put("notify_url", NOTIFY_URL);
        params.put("redirect_url", REDIRECT_URL);
        JSONObject result = null;
        JSONObject data = null;
        try {
            params.put("sign", EncryptionUtil.getSign(params, KEY));
            logger.info("createOrder param: {}", JSONObject.toJSON(params).toString());
            String responseResult = SSLClient.formHttp(WIKI_URL, params);
            logger.info("createOrder result: {}", responseResult);
            JSONObject object = JSONObject.parseObject(responseResult);
            result = object.getJSONObject("result");
            data = object.getJSONObject("data");
        } catch (Exception e) {
            logger.error("create qrcode order error", e);
            throw new ClientServiceException("创建充值订单失败！", OPERATION_FAIL);
        }
        if (!result.getBoolean("success")) {
            throw new ClientServiceException(result.getString("error_msg"), OPERATION_FAIL);
        }
        return data.getString("url");
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
        String signContent = getSignContent(params);
        EncryptionUtil.rsaCheck(signContent, sign, APP, "UTF-8", "RSA");
        return false;
    }
}
