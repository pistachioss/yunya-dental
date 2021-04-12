package com.yunya365.wechat.config;

import com.yunya.feign.wechat.domain.vo.WxAccessTokenVo;
import com.yunya.framework.common.constant.WXConstant;
import com.yunya.framework.redis.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: xy
 * @date 2021/3/26 13:16
 **/
//@Configuration
@Slf4j
public class AccessTokenRunner{
    @Resource
    private WXConfig wxConfig;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisUtils redisUtils;

    public void refreshToken(){
        String accessToken = null;
        String redisKey = String.format(WXConstant.ACCESS_TOKEN_KEY, wxConfig.getAppId());
        accessToken = redisUtils.get(redisKey);
        if (StringUtils.isNotBlank(accessToken)) {
            log.info("access_token已存在，不需要刷新：{}", accessToken);
            return;
        }
        String url = String.format(WXConstant.WX_ACCESS_TOKEN_URL, wxConfig.getAppId(), wxConfig.getAppSecret());
        log.info("获取access_token的url：{}", url);
        //HttpClient工具根据项目自行修改
        WxAccessTokenVo accessTokenRes = restTemplate.getForObject(url, WxAccessTokenVo.class);
        log.info("调用微信access_token返回结果是: {}", accessTokenRes);
        if (accessTokenRes == null || StringUtils.isNotBlank(accessTokenRes.getErrcode())) {
            return;
        }
        accessToken = accessTokenRes.getAccess_token();
        //redis工具根据项目自行修改
        redisUtils.set(redisKey, accessToken, 7200, TimeUnit.SECONDS);
    }
}
