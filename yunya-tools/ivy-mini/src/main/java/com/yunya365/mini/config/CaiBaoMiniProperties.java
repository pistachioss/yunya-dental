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
public class CaiBaoMiniProperties {
    private String operatorId;
    private String appId;
    private String key;
    private String paymentChannel;
    private String notifyUrl;
    private String publicKey;
}
