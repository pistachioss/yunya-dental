package com.yunya.framework.common.constant;

/**
 * @description: 微信小程序api
 * @author: xy
 * @date 2022/5/11 10:41
 **/
public interface WxMiniUri {
    /**
     * 登录凭证校验。
     */
    String AUTH_CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    /**
     * 获取微信公众号的access_token
     */
    String WX_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    /**
     * 获取用户手机号
     */
    String PHONE_NUMBER_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=%s";

    /**
     * 获取微信小程序二维码
     */
    String WX_MINIPROGRAM_QR_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=%s";
}
