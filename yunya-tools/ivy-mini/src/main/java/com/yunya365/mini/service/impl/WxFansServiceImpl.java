package com.yunya365.mini.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yunya.feign.ivy_mini.domain.bo.WeChatSessionBO;
import com.yunya.feign.ivy_mini.domain.form.*;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.patient_central.WxFans;
import com.yunya365.mini.service.IWxFansService;
import com.yunya365.mini.service.WxApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

import static com.yunya.framework.common.constant.WxMiniUri.*;

/**
 * <p>
 * 公司微信公众号粉丝 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-11
 */
@Service
public class WxFansServiceImpl implements IWxFansService {

    @Resource
    private WxApi wxApi;
    @Resource
    private RemotePatientCentralServiceFeign patientFeign;

    @Override
    public WxFans getByOpenId(String openId) {
        return patientFeign.countRegister(openId);
    }

    @Override
    public void saveMiniAuth(WxUserInfoForm userInfo, WeChatSessionBO sessionBO) {
        WxSaveFansForm fansForm = new WxSaveFansForm();
        fansForm.setSessionBO(sessionBO);
        fansForm.setUserInfo(userInfo);
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
        WxFans member = new WxFans();
        member.setRegisterMobile(phoneNumber);
        member.setUpdateBy(userId);
        member.setId(userId);
        patientFeign.saveOrUpdate(member);
        return phoneNumber;
    }
}
