package com.yunya365.mini.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xiangyang
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.pay")
public class WxMiniPayProperties {
    private String appId;
    private String michId;
    private String appSecret;
    private String notifyUrl;
    private boolean useSandboxEnv;

}
