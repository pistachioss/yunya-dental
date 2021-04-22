package com.yunya365.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.wechat.domain.model.WxTemplatePushModel;
import com.yunya.feign.wechat.domain.vo.WxKfAllVo;
import com.yunya.feign.wechat.domain.vo.WxKfListVo;
import com.yunya.feign.wechat.domain.vo.WxKfOnlineVo;
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
        return redisUtils.get(redisKey);
    }

    public String getAndCheckUserInfo(String openId) {
        String accessToken = this.getAccessToken();
        String url = String.format(WXConstant.WX_USER_INFO_URL, accessToken, openId);
        String resultStr = restTemplate.getForObject(url, String.class);
        log.info("获取用户信息结果，{}", resultStr);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        this.checkWxResult(jsonObject);
        return resultStr;
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

    public String getAuthOpenId(String code) {
        String authAccessTokenUrl = String.format(WXConstant.WX_AUTH_ACCESS_TOKEN_URL, wxConfig.getAppId(), wxConfig.getAppSecret(), code);
        String resultStr = restTemplate.getForObject(authAccessTokenUrl, String.class);
        log.info("用户授权信息：{}", resultStr);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        Integer errCode = jsonObject.getInteger("errcode");
        if (errCode != null && errCode != 0) {
            String errMsg = jsonObject.getString("errmsg");
            throw new BaseException(errMsg, errCode);
        }
        return jsonObject.getString("openid");
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
}
