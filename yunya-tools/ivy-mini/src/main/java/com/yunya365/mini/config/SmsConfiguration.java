package com.yunya365.mini.config;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: xy
 * @date 2021/11/15 14:22
 **/
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(CoOpenSmsProperties.class)
public class SmsConfiguration {

    private final CoOpenSmsProperties coOpenSmsProperties;

    @Bean
    @ConditionalOnClass(CoOpenSmsProperties.class)
    @ConditionalOnMissingBean(CCPRestSmsSDK.class)
    public CCPRestSmsSDK getCoSmsClient() {
        CCPRestSmsSDK config = new CCPRestSmsSDK();
        config.init(coOpenSmsProperties.getHost(), coOpenSmsProperties.getPort());
        config.setAccount(coOpenSmsProperties.getAccountSid(), coOpenSmsProperties.getAccountToken());
        config.setAppId(coOpenSmsProperties.getAppId());
        return config;
    }
}
