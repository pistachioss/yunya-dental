package com.yunya365.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.wechat.domain.model.WxAutoTextReplyModel;
import com.yunya.feign.wechat.domain.model.WxTemplatePushModel;
import com.yunya.feign.wechat.domain.vo.*;
import com.yunya.framework.common.constant.WXConstant;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya365.wechat.config.WXConfig;
import com.yunya365.wechat.enums.WeChatError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2021/3/26 9:28
 **/
@Slf4j
public abstract class AbstractWxBaseApi {

    @Resource
    private WXConfig wxConfig;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RestTemplate restTemplate;

    private String getAccessToken() {
        String redisKey = String.format(WXConstant.ACCESS_TOKEN_KEY, wxConfig.getAppId());
        String accessToken = redisUtils.get(redisKey);
        log.info("微信公众号token：{}", accessToken);
        return redisUtils.get(redisKey);
    }

    public JSONObject getAndCheckUserInfo(String openId) {
        String accessToken = this.getAccessToken();
        String url = String.format(WXConstant.WX_USER_INFO_URL, accessToken, openId);
        String resultStr = restTemplate.getForObject(url, String.class);
        log.info("获取用户信息结果，{}", resultStr);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        this.checkWxResult(jsonObject);
        return jsonObject;
    }

    public String listTemplate() {
        String accessToken = getAccessToken();
        String url = String.format(WXConstant.WX_TEMPLATE_URL, accessToken);
        String resultStr = restTemplate.postForObject(url, String.class, String.class);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        Integer errCode = jsonObject.getInteger("errcode");
        if (errCode != null && errCode != 0) {
            throw new ClientServiceException(jsonObject.getString("errmsg"), errCode);
        }
        return jsonObject.getString("template_list");
    }

    public String pushTemplate(WxTemplatePushModel pushModel) {
        String accessToken = getAccessToken();
        String url = String.format(WXConstant.WX_SEND_TEMPLATE_MSG_URL, accessToken);
        String resultStr = restTemplate.postForObject(url, pushModel, String.class);
        log.info("模板消息推送返回：{}", resultStr);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        Integer errCode = jsonObject.getInteger("errcode");
        if (errCode != null && errCode != 0) {
            throw new ClientServiceException(jsonObject.getString("errmsg"), errCode);
        }
        return jsonObject.getString("msgid");
    }

    public String pushAutoReply(Object pushModel) {
        String accessToken = getAccessToken();
        String url = String.format(WXConstant.WX_SET_AUTO_REPLY_URL, accessToken);
        String resultStr = restTemplate.postForObject(url, pushModel, String.class);
        log.info("自动回复消息推送返回：{}", resultStr);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        Integer errCode = jsonObject.getInteger("errcode");
        if (errCode != null && errCode != 0) {
            throw new ClientServiceException(jsonObject.getString("errmsg"), errCode);
        }
        return jsonObject.getString("msgid");
    }

    public JSONObject menuGet() {
        String accessToken = getAccessToken();
        String url = String.format(WXConstant.WX_GET_MENU_URL, accessToken);
        String resultStr = restTemplate.getForObject(url, String.class);
        log.info("公众号获取菜单返回：{}", resultStr);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        Integer errCode = jsonObject.getInteger("errcode");
        if (errCode != null && errCode != 0) {
            throw new ClientServiceException(jsonObject.getString("errmsg"), errCode);
        }
        return jsonObject;
    }

    public JSONObject menuCreate(JSONObject menu) {
        String accessToken = getAccessToken();
        String url = String.format(WXConstant.WX_CREATE_MENU_URL, accessToken);
        String resultStr = restTemplate.postForObject(url, menu, String.class);
        log.info("公众号创建菜单返回：{}", resultStr);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        Integer errCode = jsonObject.getInteger("errcode");
        if (errCode != null && errCode != 0) {
            throw new ClientServiceException(jsonObject.getString("errmsg"), errCode);
        }
        return jsonObject;
    }

    public WxKfOnlineVo listOnlineKf() {
        String redisKey = String.format(WXConstant.ACCESS_TOKEN_KEY, wxConfig.getAppId());
        String accessToken = redisUtils.get(redisKey);
        log.info("access_token：{}", accessToken);
        String url = String.format(WXConstant.WX_ONLINE_KF_LIST_URL, accessToken);
        //HttpClient工具根据项目自行修改
        WxKfAllVo kfAllRes = restTemplate.getForObject(url, WxKfAllVo.class);
        log.info("在线客服列表：{}", kfAllRes);
        //在线客服
        assert kfAllRes != null;
        List<WxKfOnlineVo> kfOnlineList = kfAllRes.getKf_online_list();
        if (CollectionUtils.isNotEmpty(kfOnlineList)) {
            return kfOnlineList.get(0);
        }
        return null;
    }

    public WxKfListVo listAllKf() {
        String redisKey = String.format(WXConstant.ACCESS_TOKEN_KEY, wxConfig.getAppId());
        String accessToken = redisUtils.get(redisKey);
        String url = String.format(WXConstant.WX_LIST_KF_LIST_URL, accessToken);
        //HttpClient工具根据项目自行修改
        WxKfAllVo kfAllRes = restTemplate.getForObject(url, WxKfAllVo.class);
        log.info("所有客服列表：{}", kfAllRes);
        //在线客服
        assert kfAllRes != null;
        List<WxKfListVo> kf_list = kfAllRes.getKf_list();
        if (CollectionUtils.isNotEmpty(kf_list)) {
            return kf_list.get(0);
        }
        return null;
    }

    public JSONObject getAuthOpenId(String code) {
        return getWxApi(WXConstant.WX_AUTH_ACCESS_TOKEN_URL, wxConfig.getAppId(), wxConfig.getAppSecret(), code);
    }


    public JSONObject getWxApi(String url, Object ... param) {
        String requestUrl = String.format(url, param);
        String resultStr = restTemplate.getForObject(requestUrl, String.class);
        log.info("微信api返回结果：{}", resultStr);
        assert resultStr != null;
        JSONObject jsonObject = JSONObject.parseObject(new String(resultStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
        Integer errCode = jsonObject.getInteger("errcode");
        if (errCode != null && errCode != 0) {
            String errMsg = jsonObject.getString("errmsg");
            throw new BaseException(errMsg, errCode);
        }
        return jsonObject;
    }

    public WxSignatureVo getSignInfo(String url) {
        log.info("js-api传入的url：{}", url);
        String redisKey = String.format(WXConstant.JSAPI_TICKET_KEY, wxConfig.getAppId());
        String ticket = redisUtils.get(redisKey);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String sortedParams = this.getSortedParams(ticket, url, timestamp);
        String sign = this.getEncryptedParams(sortedParams);
        WxSignatureVo vo = new WxSignatureVo();
        vo.setSignature(sign);
        vo.setAppId(wxConfig.getAppId());
        vo.setNonceStr(wxConfig.getEncodingAESKey());
        vo.setTimeStamp(timestamp);
        log.info("js-sdk的config验证信息：{}", vo);
        return vo;
    }

    private void checkWxResult(JSONObject jsonObject) {
        if (jsonObject == null) {
            throw new ClientServiceException(WeChatError.USER_NOT_FOLLOW);
        }
        Integer errCode = jsonObject.getInteger("errcode");
        if (errCode != null && errCode != 0) {
            if (errCode == 40003) {
                throw new BaseException(WeChatError.USER_NOT_FOLLOW.getMessage(), errCode);
            }
            String errMsg = jsonObject.getString("errmsg");
            throw new BaseException(errMsg, errCode);
        }
        boolean subscribe = jsonObject.getBooleanValue("subscribe");
        //是否关注
        if (!subscribe) {
            throw new ClientServiceException(WeChatError.USER_NOT_FOLLOW);
        }
    }

    private String getSortedParams(String jsTicket, String url, String timestamp) {
        StringBuilder validateString = new StringBuilder();
        validateString.append("jsapi_ticket=").append(jsTicket)
               .append("&noncestr=").append(wxConfig.getEncodingAESKey())
               .append("&timestamp=").append(timestamp)
               .append("&url=").append(url);
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
