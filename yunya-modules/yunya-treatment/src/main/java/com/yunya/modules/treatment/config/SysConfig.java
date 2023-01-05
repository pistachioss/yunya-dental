package com.yunya.modules.treatment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sysconfig")
public class SysConfig {
    /**
     * #1914 执行人与咨询师需要可以是同一人，设置为true则可为同一人；
     */
    private Boolean executorAndconsulterCanSame;
}
