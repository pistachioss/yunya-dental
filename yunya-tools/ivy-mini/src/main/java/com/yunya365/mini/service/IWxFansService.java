package com.yunya365.mini.service;

import com.yunya.feign.ivy_mini.domain.bo.WeChatSessionBO;
import com.yunya.feign.ivy_mini.domain.form.WxAuthUserInfoForm;
import com.yunya.feign.ivy_mini.domain.form.WxUserInfoForm;
import com.yunya.models.patient_central.WxFans;

/**
 * <p>
 * 公司微信公众号粉丝 服务类
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-11
 */
public interface IWxFansService {

    /**
     * 通过openid获取用户信息
     * @param openId:
     * @return WxFans
     */
    WxFans getByOpenId(String openId);

    /**
     * 保存微信授权信息
     * @param sessionBO:
     * @param userInfo:
     * @return void
     */
    void saveMiniAuth(WxUserInfoForm userInfo, WeChatSessionBO sessionBO);

    /**
     * 保存微信授权手机号码
     * @param form:
     * @return String
     */
    String competeAuthPhone(WxAuthUserInfoForm form);
}
