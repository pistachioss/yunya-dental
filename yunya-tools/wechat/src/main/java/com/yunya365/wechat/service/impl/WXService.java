package com.yunya365.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.WxFansSaveForm;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.wechat.domain.model.WxRegisterModel;
import com.yunya.feign.wechat.domain.vo.WxAuthVo;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.WxFans;
import com.yunya.models.patient_central.WxFansBind;
import com.yunya.models.system.DictionaryItem;
import com.yunya365.wechat.config.WXConfig;
import com.yunya365.wechat.enums.WeChatError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

import static java.util.stream.Collectors.*;

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
    private RemotePatientCentralServiceFeign patientFeign;
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    public static String QIN_SHU_GUAN_XI = "亲属关系";
    public static String BEN_REN = "本人";

    public WxAuthVo getAuthInfo(String code) {
        WxAuthVo vo = new WxAuthVo();
        //1.根据code获取access_token和openid(非基础的那个)
        String authOpenId = super.getAuthOpenId(code);
        //2.根据openid获取用户信息
        String userInfoStr = super.getUserInfo(authOpenId);
        WxFans wxFans = JSONObject.parseObject(userInfoStr, WxFans.class);
        vo.setSubscribe(wxFans.getSubscribe());
        vo.setOpenId(wxFans.getOpenId());
        return vo;
    }

    public void register(String openId, WxRegisterModel model) {
        WxFansSaveForm fansSaveForm = new WxFansSaveForm();
        int count = patientFeign.countRegister(openId);
        if (count > 0) {
            throw new ClientServiceException(WeChatError.USER_IS_REGISTERED);
        }
        //获取微信用户信息
        String userInfoStr = super.getUserInfo(openId);
        WxFans wxFans = this.assembleWxFans(userInfoStr);
        //是否关注
        if (!wxFans.getSubscribe()) {
            throw new ClientServiceException(WeChatError.USER_NOT_FOLLOW);
        }
        List<WxFansBind> wxFansBinds = this.buildWxFansBind(wxFans, model);
        fansSaveForm.setWxFans(wxFans);
        fansSaveForm.setFansBind(wxFansBinds);
        //调用患者服务的保存微信用户接口，患者绑定关系表
        patientFeign.saveWx(fansSaveForm);
    }

    public void vipInfo(String openId) {

    }

    private WxFans assembleWxFans(String userInfoStr) {
        WxFans wxFans = JSONObject.parseObject(userInfoStr, WxFans.class);
        this.jsonToFans(wxFans, userInfoStr);
        return wxFans;
    }

    private List<WxFansBind> buildWxFansBind(WxFans wxFans, WxRegisterModel model) {
        List<WxFansBind> list = Lists.newArrayList();
        PatientBaseInfo baseInfo = new PatientBaseInfo();
        baseInfo.setMobile(model.getMobile());
        baseInfo.setName(model.getUserName());
        List<PatientBaseInfo> patientInfoList = patientFeign.findPatientInfoList(baseInfo);
        if (CollectionUtils.isNotEmpty(patientInfoList)) {
            DictionaryItem dictItem = systemServiceFeign.getDictItemByNames(QIN_SHU_GUAN_XI, BEN_REN);
            if (dictItem == null) {
                throw new ClientServiceException(WeChatError.DICT_NO_CONFIG);
            }
            wxFans.setPatientId(patientInfoList.get(0).getId());
            list =  patientInfoList.stream().map(obj -> {
                WxFansBind wxFansBind = new WxFansBind();
                wxFansBind.setPatientId(obj.getId());
                wxFansBind.setOpenId(wxFans.getOpenId());
                wxFansBind.setDictionaryId(dictItem.getId());
                wxFansBind.setBind(true);
                wxFansBind.setBindTime(wxFans.getBindTime());
                wxFansBind.setIsVip(true);
                wxFansBind.setIsOwner(true);
                wxFansBind.setUpdId(wxFansBind.getPatientId());
                wxFansBind.setCrtId(wxFansBind.getPatientId());
                return wxFansBind;
            }).collect(toList());
        }
        return list;
    }

    private void jsonToFans(WxFans wxFans, String userInfoStr) {
        JSONObject userJson = JSONObject.parseObject(userInfoStr);
        JSONArray tagList = userJson.getJSONArray("tagid_list");
        if (CollectionUtils.isNotEmpty(tagList)) {
            wxFans.setTagidList(Joiner.on(",").join(tagList));
        }
    }
}
