package com.yunya365.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.wechat.domain.vo.WxKfAllVo;
import com.yunya.feign.wechat.domain.vo.WxKfListVo;
import com.yunya.feign.wechat.domain.vo.WxKfOnlineVo;
import com.yunya.framework.common.constant.WXConstant;
import com.yunya365.wechat.config.WXConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
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
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private RestTemplate restTemplate;

    public WxKfOnlineVo listOnlineKf() {
        String redisKey = String.format(WXConstant.ACCESS_TOKEN_KEY, wxConfig.getAppId());
        String accessToken = redisTemplate.opsForValue().get(redisKey);
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
        String accessToken = redisTemplate.opsForValue().get(redisKey);
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

    public void beforeLogin(String code) {
        String authAccessTokenUrl = String.format(WXConstant.WX_AUTH_ACCESS_TOKEN_URL, wxConfig.getAppId(), wxConfig.getAppSecret(), code);
        String resultStr = restTemplate.getForObject(authAccessTokenUrl, String.class);
    }

    public void getUserInfo(String openId) {
        String redisKey = String.format(WXConstant.ACCESS_TOKEN_KEY, wxConfig.getAppId());
        String accessToken = redisTemplate.opsForValue().get(redisKey);
        String url = String.format(WXConstant.WX_USER_INFO_URL, accessToken, openId);
        String resultStr = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        System.out.println(jsonObject.getString("subscribe"));
        System.out.println(jsonObject.getString("openid"));
        System.out.println(jsonObject.getString("nickname"));
        System.out.println(jsonObject.getString("sex"));
        log.info("会员信息：{}", resultStr);
    }

    public void pushMsg(String msgId) {
        String redisKey = String.format(WXConstant.ACCESS_TOKEN_KEY, wxConfig.getAppId());
        String accessToken = redisTemplate.opsForValue().get(redisKey);
        String url = String.format(WXConstant.WX_SEND_TEMPLATE_MSG_URL, accessToken);
        String resultStr = restTemplate.postForObject(url, String.class, String.class);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        System.out.println(jsonObject.getString("subscribe"));
        System.out.println(jsonObject.getString("openid"));
        System.out.println(jsonObject.getString("nickname"));
        System.out.println(jsonObject.getString("sex"));
        log.info("会员信息：{}", resultStr);
    }
}
