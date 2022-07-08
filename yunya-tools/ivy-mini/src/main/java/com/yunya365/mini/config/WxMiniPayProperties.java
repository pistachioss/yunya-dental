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
    private String refundNotifyUrl;
    private boolean useSandboxEnv;
    /**
     * apiclient_cert.p12文件的绝对路径，或者如果放在项目中，请以classpath:开头指定
     */
    private String keyPath;

}
