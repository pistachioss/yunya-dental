package com.yunya.modules.treatment.other.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("netty")
public class WsProperties {

    // 协议
    private String protocol;
    // websocket路径
    private String path;
    // ip
    private String ip = "0.0.0.0";
    // 端口
    private Integer port = 8081;
    // 服务名称
    private String serviceName;

    @Autowired
    InetUtils inetUtils;

    public void setIp(String ip){
        if (ip != null)
            this.ip = ip.trim();            
    }

    public String getFirstIp(){
        if ("0.0.0.0".equals(ip))
            return inetUtils.findFirstNonLoopbackAddress().getHostAddress();
        return ip;
    }
}