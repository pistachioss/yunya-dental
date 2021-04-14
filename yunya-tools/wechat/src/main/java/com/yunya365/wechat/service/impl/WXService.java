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

    public WxVipInfoVo vipInfo(String openId, Integer patientId) {
        WxFans wxFans = this.getOwnInfo(openId, patientId);
        if (patientId == null) {
            if (wxFans == null) {
                throw new ClientServiceException(WeChatError.USER_NOT_FOLLOW);
            }
            return this.getVipInfo(wxFans.getPatientId(), wxFans);
        } else {
            if (wxFans == null) {
                return this.assembleWxPatientInfo(patientId);
            } else {
                return this.getVipInfo(patientId, wxFans);
            }
        }
    }

    private WxFans getOwnInfo(String openId, Integer patientId) {
        WxUserQuery query = new WxUserQuery();
        if (patientId == null) {
            query.setOpenId(openId);
        } else {
            query.setPatientId(patientId);
        }
        //查询微信患者id
        return patientFeign.getWxFans(query);
    }

    private WxVipInfoVo getVipInfo(Integer patientId, WxFans wxFans) {
        WxVipInfoVo wxVipInfoVo;
        //未绑定患者直接返回注册信息
        if (patientId == null) {
            wxVipInfoVo = this.assembleWxUserInfo(wxFans);
        } else {
            wxVipInfoVo = this.assembleWxPatientInfo(patientId);
            if (wxVipInfoVo == null) {
                wxVipInfoVo = this.assembleWxUserInfo(wxFans);
            }
            //患者头像没有取微信用户头像
            if (StringUtils.isBlank(wxVipInfoVo.getHeadImgUrl())) {
                wxVipInfoVo.setHeadImgUrl(wxFans.getHeadImgurl());
            }
        }
        return wxVipInfoVo;
    }

    private WxVipInfoVo assembleWxUserInfo(WxFans wxFans) {
        //未注册
        WxVipInfoVo wxVipInfoVo = new WxVipInfoVo();
        wxVipInfoVo.setRegisterName(wxFans.getRegisterName());
        wxVipInfoVo.setRegisterMobile(wxFans.getRegisterMobile());
        wxVipInfoVo.setHeadImgUrl(wxFans.getHeadImgurl());
        return wxVipInfoVo;
    }

    private WxVipInfoVo assembleWxPatientInfo(Integer patientId) {
        WxVipInfoVo wxVipInfoVo = null;
        PatientPublicInfoVo patientInfo = patientFeign.findPatientPublicInfoById(patientId);
        if (patientInfo != null) {
            wxVipInfoVo = new WxVipInfoVo();
            wxVipInfoVo.setRegisterName(patientInfo.getName());
            wxVipInfoVo.setRegisterMobile(patientInfo.getMobile());
            wxVipInfoVo.setHeadImgUrl(patientInfo.getFaceUrl());
            wxVipInfoVo.setMemberType(patientInfo.getMemberTypeId());
            wxVipInfoVo.setExistPrePayment(false);
            wxVipInfoVo.setExistMemberCard(false);
            //是否有预付款账号
            if (StringUtils.isNotBlank(patientInfo.getPrepaymentNumber())) {
                wxVipInfoVo.setExistPrePayment(true);
            }
            //是否有会员卡账号
            if (StringUtils.isNotBlank(patientInfo.getCardNumber())) {
                wxVipInfoVo.setExistMemberCard(true);
                wxVipInfoVo.setMemberBalance(patientInfo.getMemberCardMoneySum());
            }
            List<WxPatientEffectiveVo> effectCardList = discountFeign.getPatientEffectCardList(patientId);
            if (CollectionUtils.isNotEmpty(effectCardList)) {
                wxVipInfoVo.setCardList(effectCardList);
            }
        }
        return wxVipInfoVo;
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
