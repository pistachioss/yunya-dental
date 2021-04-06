package com.yunya365.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.yunya.feign.wechat.domain.model.WxRegisterModel;
import com.yunya.feign.wechat.domain.vo.WxAuthVo;
import com.yunya.models.patient_central.WxFans;
import com.yunya365.wechat.config.WXConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

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

    public WxAuthVo getAuthInfo(String code) {
        WxAuthVo vo = new WxAuthVo();
        //1.根据code获取access_token和openid(非基础的那个)
        String authOpenId = super.getAuthOpenId(code);
        //2.根据openid获取用户信息
        String userInfoStr = super.getUserInfo(authOpenId);
        WxFans wxFans = JSONObject.parseObject(userInfoStr, WxFans.class);
        vo.setIsSubscribe( wxFans.getSubscribe());
        vo.setOpenId(wxFans.getOpenId());
        return vo;
    }

    public void register(String openId, WxRegisterModel model) {
        String userInfoStr = super.getUserInfo(openId);
        WxFans wxFans = JSONObject.parseObject(userInfoStr, WxFans.class);
        this.jsonToFans(wxFans, userInfoStr);
        //调用患者服务的保存微信用户接口，患者绑定关系表

    }

    private void jsonToFans(WxFans wxFans, String userInfoStr) {
        JSONObject userJson = JSONObject.parseObject(userInfoStr);
        wxFans.setTagidList(Joiner.on(",").join(userJson.getJSONArray("tagid_list")));
    }
}
