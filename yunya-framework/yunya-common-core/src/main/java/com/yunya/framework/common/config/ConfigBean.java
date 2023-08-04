package com.yunya.framework.common.config;

import cn.hutool.extra.qrcode.QrConfig;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.awt.*;

/**
 * @description:
 * @author: xy
 * @date 2022/9/26 16:02
 **/
@Configuration
public class ConfigBean {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public QrConfig qrConfig(){
        QrConfig qrConfig=new QrConfig();
        qrConfig.setBackColor(Color.white.getRGB());
        qrConfig.setForeColor(Color.black.getRGB());
        return qrConfig;
    }
}
