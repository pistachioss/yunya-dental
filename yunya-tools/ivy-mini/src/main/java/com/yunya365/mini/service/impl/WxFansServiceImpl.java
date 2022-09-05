package com.yunya365.mini.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yunya.feign.ivy_mini.domain.bo.WeChatSessionBO;
import com.yunya.feign.ivy_mini.domain.form.*;
import com.yunya.feign.ivy_mini.domain.vo.FansDetailVO;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.TrueFalseEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.models.patient_central.WxFans;
import com.yunya365.mini.service.IWxFansService;
import com.yunya365.mini.service.WxApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

import static com.yunya.framework.common.constant.WxMiniUri.*;
import static com.yunya365.mini.enums.IvyMiniError.*;

/**
 * <p>
 * 公司微信公众号粉丝 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-11
 */
@Service
@Slf4j
public class WxFansServiceImpl implements IWxFansService {

    @Resource
    private WxApi wxApi;
    @Resource
    private RemotePatientCentralServiceFeign patientFeign;


    private static final String HEAD_PREFIX = "thirdwx.qlogo.cn";

    @Override
    public WxFans getByOpenId(String openId) {
        return patientFeign.countRegister(openId);
    }

    @Override
    public void saveMiniAuth(WxFans wxFans, WxUserInfoForm userInfo, WeChatSessionBO sessionBO) {
        WxSaveFansForm fansForm = new WxSaveFansForm();
        fansForm.setSessionBO(sessionBO);
        userInfo.setGender(Objects.isNull(userInfo.getGender()) || Objects.equals(userInfo.getGender(), 0) ? TrueFalseEnum.TRUE.getCode() : userInfo.getGender());
        fansForm.setUserInfo(userInfo);
        fansForm.setFansId(Objects.isNull(wxFans) ? null : wxFans.getId());
        patientFeign.saveMiniAuth(fansForm);
    }

    @Override
    public String competeAuthPhone(WxAuthUserInfoForm form) {
        String code = form.getCode();
        String phoneNumber;
        JSONObject result;
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        if (StringUtils.isNoneBlank(code)) {
            //获取用户手机号
            String url = String.format(PHONE_NUMBER_URL, wxApi.getAccessToken());
            Map<String, String> param = Maps.newHashMap();
            param.put("code", code);
            result = wxApi.wxPostObject(url, param);
            phoneNumber = result.getJSONObject("phone_info").getString("phoneNumber");
        } else {
            result = wxApi.decrypt(form.getEncryptedData(), form.getIv());
            phoneNumber = result.getString("phoneNumber");
        }
        //更新用户
        WxFans wxFans = new WxFans();
        wxFans.setRegisterMobile(phoneNumber);
        wxFans.setUpdateBy(userId);
        wxFans.setId(userId);
        patientFeign.saveOrUpdate(wxFans);
        return phoneNumber;
    }

    @Override
    public void modifyFans(ModifyFansForm form) {
        WxFans fans = patientFeign.countRegister(form.getOpenId());
        if (Objects.isNull(fans)) {
            throw ClientServiceException.wrap(FANS_NOT_EXIST);
        }
        WxFans wxFans = BeanUtil.copy(form, WxFans.class);
        wxFans.setId(fans.getId());
        patientFeign.saveOrUpdate(wxFans);
    }

    @Override
    public FansDetailVO fansDetail(String openId) {
        WxFans fans = patientFeign.countRegister(openId);
        FansDetailVO copy = BeanUtil.copy(fans, FansDetailVO.class);
        copy.setPhoneNumber(fans.getRegisterMobile());
        return copy;
    }
}
