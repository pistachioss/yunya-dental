package com.yunya365.mini.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xiangyang
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.mini")
public class WxMiniProperties {
    private String appId;
    private String appSecret;
}
