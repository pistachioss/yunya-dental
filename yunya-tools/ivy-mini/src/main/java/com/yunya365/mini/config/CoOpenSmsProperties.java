package com.yunya365.mini.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @author: xy
 * @date 2021/11/15 14:13
 **/
@Data
@ConfigurationProperties("cloopen.sms")
public class CoOpenSmsProperties {

    private String appId;
    private String accountSid;
    private String accountToken;
    private String host;
    private String port;
    private String templateId;
    private Long timeOut;
}
