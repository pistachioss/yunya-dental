package com.yunya.modules.treatment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sysconfig")
public class SysConfig {
    private Boolean executorAndconsulterCanSame;
}
