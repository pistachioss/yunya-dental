package com.yunya365.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.yunya.feign.wechat.domain.model.WxRegisterModel;
import com.yunya.feign.wechat.domain.vo.WxAuthVo;
import com.yunya.models.patient_central.WxFans;
import com.yunya365.wechat.config.WXConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 15:35
 **/
@Slf4j
@Service
public class WXService extends AbstractWxBaseApi{
    @Resource
    private WXConfig wxConfig;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public WxAuthVo getAuthInfo(String code) {
        WxAuthVo vo = new WxAuthVo();
        //1.根据code获取access_token和openid(非基础的那个)
        String authOpenId = super.getAuthOpenId(code);
        //2.根据openid获取用户信息
        JSONObject userJson = super.getUserInfo(authOpenId);
        vo.setIsSubscribe( userJson.getBoolean("subscribe"));
        vo.setOpenId(userJson.getString("openid"));
        return vo;
    }

    public void register(String openId, WxRegisterModel model) {
        JSONObject userJson = super.getUserInfo(openId);
        WxFans wxFans = this.jsonToFans(userJson);

    }

    private WxFans jsonToFans(JSONObject userJson) {
        WxFans wxFans = new WxFans();
        wxFans.setOpenId(userJson.getString("openid"));
        wxFans.setNickName(userJson.getString("nickname"));
        wxFans.setSex(userJson.getShortValue("sex"));
        wxFans.setCountry(userJson.getString("country"));
        wxFans.setProvince(userJson.getString("province"));
        wxFans.setCity(userJson.getString("city"));
        wxFans.setLanguage(userJson.getString("language"));
        wxFans.setHeadImgurl(userJson.getString("headimgurl"));
        wxFans.setSubscribe(userJson.getBoolean("subscribe"));
        wxFans.setSubscribeTime(new Date(userJson.getLong("subscribe_time") * 1000));
        wxFans.setUnionId(userJson.getString("unionid"));
        wxFans.setGroupId(userJson.getString("groupid"));
        wxFans.setTagidList(Joiner.on(",").join(userJson.getJSONArray("tagid_list")));
        wxFans.setBind(true);
        wxFans.setBindTime(null);
        return wxFans;
    }
}
