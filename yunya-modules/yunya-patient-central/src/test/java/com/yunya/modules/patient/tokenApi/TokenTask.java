package com.yunya.modules.patient.tokenApi;

import com.uniubi.sdk.auth.authToken.AppAuthParam;
import com.uniubi.sdk.auth.authToken.CustomTokenFetcher;
import com.uniubi.sdk.auth.authToken.TokenFetcher;
import com.yunya.framework.common.utils.MD5Util;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/7 16:00
 * @description:
 * @since: 1.0.0
 */
public class TokenTask implements CustomTokenFetcher {

    //应用Id
    private static String appId = "D40708B670E54D2DA06B1A3974A66EA4";
    //设备序列号
    private static String deviceKey = "84E0F4246B261501";
    //公钥 秘钥
    private static String appSecret = "B496892726AC4D0BBCBC0A6575EC9365";
    private static String appKey = "2CA42A1905B44CD18D8EE83049903306";

    @Override
    public String getToken() {
        String S = appKey+System.currentTimeMillis()+appSecret;
        String token = MD5Util.getStringMD5(S);
        AppAuthParam appAuthParam = new AppAuthParam(appKey,appSecret,appId);
        TokenFetcher.init(appAuthParam);
        return "2da1a2ae13be926cd06d790057618e37d7d83702f59db83e205e8244e8976713";
    }
}
