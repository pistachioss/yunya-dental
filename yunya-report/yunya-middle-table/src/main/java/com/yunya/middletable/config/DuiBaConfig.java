package com.yunya.middletable.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: yunya-dental
 * @description: 兑吧配置
 * @author: LHB
 * @create: 2021-04-22 14:59
 **/
@Component
@ConfigurationProperties(prefix = "duiba")
@Data
public class DuiBaConfig {
    private String appKey;
    private String appSecret;
    private String autoLoginUrl;
}