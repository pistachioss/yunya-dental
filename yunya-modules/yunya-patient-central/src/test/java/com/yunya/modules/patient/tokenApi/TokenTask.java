package com.yunya.modules.patient.tokenApi;

import com.uniubi.sdk.auth.authToken.AppAuthParam;
import com.uniubi.sdk.auth.authToken.CustomTokenFetcher;
import com.uniubi.sdk.auth.authToken.TokenFetcher;
import com.yunya.framework.common.utils.MD5Util;
import com.yunya.modules.patient_central.constant.WoPlatformConstants;

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
        long l = System.currentTimeMillis();
        System.out.println(l);
        String S = WoPlatformConstants.APPKEY+System.currentTimeMillis()+WoPlatformConstants.APPSECRET;
        System.out.println(S);
        String token = MD5Util.getStringMD5(S);
        System.out.println(token);
        AppAuthParam appAuthParam = new AppAuthParam(WoPlatformConstants.APPKEY,WoPlatformConstants.APPSECRET,WoPlatformConstants.APPID);
        TokenFetcher.init(appAuthParam);
        return token;
    }
}
