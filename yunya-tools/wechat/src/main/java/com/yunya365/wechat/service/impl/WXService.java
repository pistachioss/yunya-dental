package com.yunya365.wechat.service.impl;

import com.yunya.feign.wechat.domain.vo.WxAccessTokenVo;
import com.yunya.framework.common.constant.WXConstant;
import com.yunya365.wechat.config.WXConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 15:35
 **/
@Slf4j
@Service
public class WXService extends AbstractWxBaseApi{
    @Resource
    private WXConfig wxConfig;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 刷新微信公众号的access_token
     * https请求:
     * https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
     * 微信返回数据:
     * {"access_token":"ACCESS_TOKEN","expires_in":7200}
     *
     */
    public void refreshToken() {
        String redisKey = String.format(WXConstant.ACCESS_TOKEN_KEY, wxConfig.getAppId());
        String url = String.format(WXConstant.WX_ACCESS_TOKEN_URL, wxConfig.getAppId(), wxConfig.getAppSecret());
        //HttpClient工具根据项目自行修改
        WxAccessTokenVo accessTokenRes = restTemplate.getForObject(url, WxAccessTokenVo.class);
        log.info("刷新access_token，返回结果是: {}", accessTokenRes);
        if (accessTokenRes == null || StringUtils.isNotBlank(accessTokenRes.getErrcode())) {
            return;
        }
        String accessToken = accessTokenRes.getAccess_token();
        //redis工具根据项目自行修改
        redisTemplate.opsForValue().set(redisKey, accessToken, 7200, TimeUnit.SECONDS);
    }
}
