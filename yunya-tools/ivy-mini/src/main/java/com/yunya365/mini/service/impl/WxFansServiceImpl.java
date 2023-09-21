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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

import static com.yunya.framework.common.constant.CommonConstants.EX_USER_PASS_INVALID_CODE;
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
    @Value("${wx.mini.name}")
    private String miniName;

    /** 最大重试次数 */
    private static final int MAX_RETRY_TIMES = 3;

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
        userInfo.setSourceType(1);
        userInfo.setSourceTypeName(miniName);
        fansForm.setUserInfo(userInfo);
        fansForm.setFansId(Objects.isNull(wxFans) ? null : wxFans.getId());
        patientFeign.saveMiniAuth(fansForm);
    }

    @Override
    public String competeAuthPhone(WxAuthUserInfoForm form) {
        log.info("微信手机授权参数:{}", form);
        String code = form.getCode();
        String phoneNumber;
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        if (StringUtils.isNoneBlank(code)) {
            //获取用户手机号
            phoneNumber = getWxPhoneNumber(code, MAX_RETRY_TIMES);
        } else {
            JSONObject result = wxApi.decrypt(form.getEncryptedData(), form.getIv());
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

    /**
     * 获取手机号码
     *
     * @param code
     * @return
     */
    private String getWxPhoneNumber(String code, int retryTimes) {
        String url = String.format(PHONE_NUMBER_URL, wxApi.getAccessToken());
        Map<String, String> param = Maps.newHashMap();
        param.put("code", code);
        try {
            JSONObject result = wxApi.wxPostObject(url, param);
            return result.getJSONObject("phone_info").getString("phoneNumber");
        } catch (ClientServiceException e) {
            log.error("微信手机授权异常：{}", e);
            if (retryTimes>=0 && EX_USER_PASS_INVALID_CODE.equals(e.getStatus())) {
                log.info("微信accessToken失效，即将刷新token后重试");
                wxApi.refreshToken();
                return getWxPhoneNumber(code, --retryTimes);
            }
        }
        return null;
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
