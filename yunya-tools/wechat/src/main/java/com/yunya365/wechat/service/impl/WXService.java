package com.yunya365.wechat.service.impl;

import com.alibaba.fastjson.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.yunya.feign.discount.*;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.feign.patient_central.*;
import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.system.*;
import com.yunya.feign.wechat.domain.model.*;
import com.yunya.feign.wechat.domain.vo.*;
import com.yunya.framework.common.exception.*;
import com.yunya.framework.redis.util.*;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.*;
import com.yunya365.wechat.enums.*;
import lombok.extern.slf4j.*;
import org.apache.commons.collections4.*;
import org.apache.commons.lang3.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

import javax.annotation.*;
import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 15:35
 **/
@Slf4j
@Service
public class WXService extends AbstractWxBaseApi {
    public static String QIN_SHU_GUAN_XI = "亲属关系";
    public static String BEN_REN = "本人";
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RemotePatientCentralServiceFeign patientFeign;
    @Resource
    private RemoteDiscountFeign discountFeign;
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;

    public WxAuthVo getAuthInfo(String code) {
        WxAuthVo vo = new WxAuthVo();
        //根据code获取access_token和openid(非基础的那个)
        String openId = super.getAuthOpenId(code);
        vo.setSubscribe(false);
        vo.setOpenId(openId);
        int count = patientFeign.countRegister(openId);
        if (count > 0) {
            vo.setSubscribe(true);
        }
        return vo;
    }

    public void register(String openId, WxRegisterModel model) {
        WxFansSaveForm fansSaveForm = new WxFansSaveForm();
        int count = patientFeign.countRegister(openId);
        if (count > 0) {
            throw new ClientServiceException(WeChatError.USER_IS_REGISTERED);
        }
        //获取微信用户信息
        String userInfoStr = redisUtils.get(openId);
        WxFans wxFans = this.assembleWxFans(userInfoStr);
        List<WxFansBind> wxFansBinds = this.buildWxFansBind(wxFans, model);
        fansSaveForm.setWxFans(wxFans);
        fansSaveForm.setFansBind(wxFansBinds);
        //调用患者服务的保存微信用户接口，患者绑定关系表
        patientFeign.saveWx(fansSaveForm);
    }

    public WxVipInfo vipInfo(String openId) {
        WxVipInfo wxVipInfo;
        WxUserQuery query = new WxUserQuery();
        query.setOpenId(openId);
        //查询微信患者id
        WxFans wxFans = patientFeign.getWxFans(query);
        Integer patientId = wxFans.getPatientId();
        if (patientId == null) {
            wxVipInfo = this.assembleWxUserInfo(wxFans);
        } else {
            wxVipInfo = this.assembleWxPatientInfo(patientId);
            if (wxVipInfo == null) {
                wxVipInfo = this.assembleWxUserInfo(wxFans);
            }
        }
        return wxVipInfo;
    }

    private WxVipInfo assembleWxUserInfo(WxFans wxFans) {
        //未注册
        WxVipInfo wxVipInfo = new WxVipInfo();
        wxVipInfo.setRegisterName(wxFans.getRegisterName());
        wxVipInfo.setRegisterMobile(wxFans.getRegisterMobile());
        wxVipInfo.setHeadImgUrl(wxFans.getHeadImgurl());
        return wxVipInfo;
    }

    private WxVipInfo assembleWxPatientInfo(Integer patientId) {
        WxVipInfo wxVipInfo = null;
        PatientPublicInfoVo patientInfo = patientFeign.findPatientPublicInfoById(patientId);
        if (patientInfo != null) {
            wxVipInfo = new WxVipInfo();
            wxVipInfo.setRegisterName(patientInfo.getName());
            wxVipInfo.setRegisterMobile(patientInfo.getMobile());
            wxVipInfo.setHeadImgUrl(patientInfo.getFaceUrl());
            wxVipInfo.setMemberType(patientInfo.getMemberTypeId());
            wxVipInfo.setExistPrePayment(false);
            wxVipInfo.setExistMemberCard(false);
            //是否有预付款账号
            if (StringUtils.isNotBlank(patientInfo.getPrepaymentNumber())) {
                wxVipInfo.setExistPrePayment(true);
            }
            //是否有会员卡账号
            if (StringUtils.isNotBlank(patientInfo.getCardNumber())) {
                wxVipInfo.setExistMemberCard(true);
                wxVipInfo.setMemberBalance(patientInfo.getMemberCardMoneySum());
            }
            List<WxPatientEffectiveVo> effectCardList = discountFeign.getPatientEffectCardList(patientId);
            if (CollectionUtils.isNotEmpty(effectCardList)) {
                wxVipInfo.setCardList(effectCardList);
            }
        }
        return wxVipInfo;
    }

    private WxFans assembleWxFans(String userInfoStr) {
        WxFans wxFans = JSONObject.parseObject(userInfoStr, WxFans.class);
        this.jsonToFans(wxFans, userInfoStr);
        return wxFans;
    }

    private List<WxFansBind> buildWxFansBind(WxFans wxFans, WxRegisterModel model) {
        List<WxFansBind> list = Lists.newArrayList();
        wxFans.setRegisterName(model.getUserName());
        wxFans.setRegisterMobile(model.getMobile());
        PatientBaseInfo baseInfo = new PatientBaseInfo();
        baseInfo.setMobile(model.getMobile());
        baseInfo.setName(model.getUserName());
        List<PatientBaseInfo> patientInfoList = patientFeign.findPatientInfoList(baseInfo);
        if (CollectionUtils.isNotEmpty(patientInfoList)) {
            DictionaryItem dictItem = systemServiceFeign.getDictItemByNames(QIN_SHU_GUAN_XI, BEN_REN);
            if (dictItem == null) {
                throw new ClientServiceException(WeChatError.DICT_NO_CONFIG);
            }
            wxFans.setBind(true);
            wxFans.setBindTime(new Date());
            wxFans.setPatientId(patientInfoList.get(0).getId());
            list = patientInfoList.stream().map(obj -> {
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
