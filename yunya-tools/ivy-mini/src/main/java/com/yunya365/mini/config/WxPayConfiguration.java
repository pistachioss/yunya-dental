package com.yunya365.mini.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @description:
 * @author: xy
 * @date 2022/6/27 17:14
 **/
@Configuration
@ConditionalOnClass(WxPayService.class)
@AllArgsConstructor
public class WxPayConfiguration {

    @Resource
    private WxMiniPayProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public WxPayService wxService() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(StringUtils.trimToNull(properties.getAppId()));
        payConfig.setMchId(StringUtils.trimToNull(properties.getMichId()));
        payConfig.setMchKey(StringUtils.trimToNull(properties.getAppSecret()));
        payConfig.setKeyPath(StringUtils.trimToNull(this.properties.getKeyPath()));
        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(properties.isUseSandboxEnv());
        payConfig.setNotifyUrl(properties.getNotifyUrl());
        payConfig.setTradeType("JSAPI");
        payConfig.setSignType("MD5");
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }
}
