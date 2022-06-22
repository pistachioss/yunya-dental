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
    /**
     * 企业微信获取客服列表
     */
    String WX_CUSTOMER_URL = "https://qyapi.weixin.qq.com/cgi-bin/kf/account/list?access_token=%s";

    /**
     * 获取企业微信的access_token
     */
    String WXCORP_ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";

    /**
     * 企业微信获取单个客服链接
     */
    String WX_FINDONE_CUSTOMER_URL = "https://qyapi.weixin.qq.com/cgi-bin/kf/add_contact_way?access_token=%s";
}
