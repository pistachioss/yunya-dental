package com.yunya.framework.common.constant;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 13:53
 **/
public interface WXConstant {
    /**
     * 微信公众号access_token_key 用于保存在redis中的key
     */
    String ACCESS_TOKEN_KEY = "wechat:accessToken:%s";

    /**
     * 获取微信公众号的access_token
     */
    String WX_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    /**
     * 获取在线客服列表
     */
    String WX_ONLINE_KF_LIST_URL = "https://api.weixin.qq.com/cgi-bin/customservice/getonlinekflist?access_token=%s";

    /**
     * 获取所有客服列表
     */
    String WX_LIST_KF_LIST_URL = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=%s";

    /**
     * 授权获取code（snsapi_base）
     */
    String WX_AUTH_CODE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=123#wechat_redirect";

    /**
     * 通过code换取网页授权access_token（与基础支持中的access_token不同）
     */
    String WX_AUTH_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 通过refresh_token 刷新access_token
     */
    String WX_AUTH_REFRESH_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";

    /**
     * 获取用户基本信息（包括UnionID机制）
     */
    String WX_USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";

    /**
     * 发送模板消息
     */
    String WX_SEND_TEMPLATE_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
}
