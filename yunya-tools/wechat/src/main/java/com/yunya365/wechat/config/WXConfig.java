package com.yunya365.wechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 16:00
 **/
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WXConfig{
    private String appId;
    private String appSecret;
    private String token;
    private String encodingAESKey;
}
