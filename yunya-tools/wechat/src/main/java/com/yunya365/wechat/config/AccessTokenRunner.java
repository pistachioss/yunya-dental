package com.yunya365.wechat.config;

import com.yunya.feign.wechat.domain.vo.WxAccessTokenVo;
import com.yunya.framework.common.constant.WXConstant;
import com.yunya.framework.redis.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: xy
 * @date 2021/3/26 13:16
 **/
@Configuration
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
        String url = String.format(WXConstant.WX_ACCESS_TOKEN_URL, wxConfig.getAppId(), wxConfig.getAppSecret());
        log.info("获取access_token的url：{}", url);
        WxAccessTokenVo accessTokenRes = restTemplate.getForObject(url, WxAccessTokenVo.class);
        log.info("调用微信access_token返回结果是: {}", accessTokenRes);
        if (accessTokenRes == null || (accessTokenRes.getErrcode() != null && accessTokenRes.getErrcode() != 0)) {
            log.info("公众号token获取失败");
            return;
        }
        accessToken = accessTokenRes.getAccess_token();
        String redisKey = String.format(WXConstant.ACCESS_TOKEN_KEY, wxConfig.getAppId());
        //redis工具根据项目自行修改
        redisUtils.set(redisKey, accessToken, 7200, TimeUnit.SECONDS);
    }
}
